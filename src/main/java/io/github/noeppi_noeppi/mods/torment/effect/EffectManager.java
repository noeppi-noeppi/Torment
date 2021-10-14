package io.github.noeppi_noeppi.mods.torment.effect;

import io.github.noeppi_noeppi.mods.torment.cap.TormentData;
import io.github.noeppi_noeppi.mods.torment.effect.instances.GoodbyeEffect;
import io.github.noeppi_noeppi.mods.torment.ghostie.Ghosties;
import io.github.noeppi_noeppi.mods.torment.util.WeightTable;
import net.minecraft.client.Minecraft;

import java.util.*;

public class EffectManager {
    
    private static final Random random = new Random();
    
    private static final Set<TormentEffect> allEffects = new HashSet<>();
    private static final Map<TormentEffect, EffectRuntime> runningEffects = new HashMap<>();
    private static final Map<TormentEffect, Integer> coolDowns = new HashMap<>();
    
    private static WeightTable<TormentEffect> effects = WeightTable.empty();
    private static boolean needsRecalculateEffects = false;
    
    private static int ticksSinceLastEffect = 0;
    private static float usedEffectLevel = 0;
    
    private static float cachedRawEffectLevel = 0;
    
    public static synchronized void registerEffect(TormentEffect effect) {
        allEffects.add(effect);
        recalculateEffects();
    }
    
    public static void reset() {
        runningEffects.clear();
        coolDowns.clear();
        effects = WeightTable.empty();
        ticksSinceLastEffect = 0;
        usedEffectLevel = 0;
        cachedRawEffectLevel = 0;
        // Add some random cool downs so players are not hit by all effects at once when joining the world.
        for (TormentEffect effect : allEffects) {
            int minCoolDown = effect.minCoolDown();
            if ((minCoolDown / 3) > 0) {
                coolDowns.put(effect, random.nextInt(effect.minCoolDown() / 3));
            }
        }
        coolDowns.put(GoodbyeEffect.INSTANCE, 6000);
        recalculateEffects();
        Ghosties.reset();
    }
    
    public static void tick() {
        if (Minecraft.getInstance().level != null && !Minecraft.getInstance().isPaused()) {
            tickPending();
            tickRunning();
            tickCoolDown();

            if (needsRecalculateEffects) {
                needsRecalculateEffects = false;
                doRecalculateEffects();
            }
            
            Ghosties.tick();
        }
    }
    
    private static void tickPending() {
        ticksSinceLastEffect += 1;
        if (!effects.isEmpty()) {
            int div = ticksSinceLastEffect / 300;
            if (div > 0 && random.nextDouble() > (1d / div)) {
                TormentEffect effect = effects.random(random);
                if (effect != null) {
                    startEffect(effect);
                }
            }
        }
    }
    
    public static void startEffect(TormentEffect effect) {
        if (Minecraft.getInstance().player != null) {
            EffectConfig config = effect.start(Minecraft.getInstance().player, random);
            if (config != null) {
                ticksSinceLastEffect = 0;
                usedEffectLevel += config.strength();
                if (config.duration() <= 0) {
                    effect.stop(Minecraft.getInstance().player, random);
                    int coolDownTime = effect.minCoolDown();
                    if (coolDownTime > 0) {
                        coolDowns.put(effect, coolDownTime);
                    }
                } else {
                    runningEffects.put(effect, new EffectRuntime(config));
                }
                recalculateEffects();
            }
        }
    }
    
    private static void tickRunning() {
        Iterator<Map.Entry<TormentEffect, EffectRuntime>> itr = runningEffects.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<TormentEffect, EffectRuntime> entry = itr.next();
            if (entry.getValue().tick()) {
                if (Minecraft.getInstance().player != null) {
                    entry.getKey().stop(Minecraft.getInstance().player, random);
                }
                usedEffectLevel -= entry.getValue().config.strength();
                itr.remove();
                int coolDownTime = entry.getKey().minCoolDown();
                if (coolDownTime > 0) {
                    coolDowns.put(entry.getKey(), coolDownTime);
                }
                recalculateEffects();
            } else {
                if (Minecraft.getInstance().player != null) {
                    entry.getKey().update(Minecraft.getInstance().player, random);
                }
            }
        }
    }
    
    private static void tickCoolDown() {
        Iterator<Map.Entry<TormentEffect, Integer>> itr = coolDowns.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<TormentEffect, Integer> entry = itr.next();
            if (entry.getValue() <= 0) {
                itr.remove();
                recalculateEffects();
            } else {
                entry.setValue(entry.getValue() - 1);
            }
        }
    }
    
    public static void recalculateEffects() {
        needsRecalculateEffects = true;
    }
    
    private static void doRecalculateEffects() {
        if (Minecraft.getInstance().player == null) {
            coolDowns.clear();
            effects = WeightTable.empty();
            cachedRawEffectLevel = 0;
        } else {
            TormentData data = TormentData.get(Minecraft.getInstance().player);
            float effectLevel = data.getEffectLevel();
            WeightTable.Builder<TormentEffect> builder = WeightTable.builder();
            for (TormentEffect effect : allEffects) {
                if (!runningEffects.containsKey(effect) && !coolDowns.containsKey(effect)
                        && (effectLevel - usedEffectLevel) >= effect.minLevel()) {
                    if (effect.cantRunWhile().stream().noneMatch(EffectManager::isRunning)) {
                        builder.add(effect, effect.weight());
                    }
                }
            }
            effects = builder.build();
            cachedRawEffectLevel = data.getEffectLevel();
            Ghosties.updateByLevel(data.getEffectLevel() - (0.25f * usedEffectLevel));
        }
    }
    
    public static boolean isRunning(TormentEffect effect) {
        return runningEffects.containsKey(effect);
    }
    
    public static int getRunningTicks(TormentEffect effect) {
        if (runningEffects.containsKey(effect)) {
            return runningEffects.get(effect).ticks;
        } else {
            return -1;
        }
    }

    public static float getCachedRawEffectLevel() {
        return cachedRawEffectLevel;
    }

    private static class EffectRuntime {
        
        public final EffectConfig config;
        private int ticks = 0;

        private EffectRuntime(EffectConfig config) {
            this.config = config;
        }
        
        public boolean tick() {
            ticks += 1;
            return ticks >= config.duration();
        }
    }
}
