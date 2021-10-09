package io.github.noeppi_noeppi.mods.torment.effect;

import io.github.noeppi_noeppi.mods.torment.cap.TormentData;
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
    
    public static void registerEffect(TormentEffect effect) {
        allEffects.add(effect);
        recalculateEffects();
    }
    
    public static void reset() {
        runningEffects.clear();
        coolDowns.clear();
        effects = WeightTable.empty();
        ticksSinceLastEffect = 0;
        usedEffectLevel = 0;
        recalculateEffects();
    }
    
    public static void tick() {
        tickPending();
        tickRunning();
        tickCoolDown();
        
        if (needsRecalculateEffects) {
            needsRecalculateEffects = false;
            doRecalculateEffects();
        }
    }
    
    private static void tickPending() {
        ticksSinceLastEffect += 1;
        if (!effects.isEmpty()) {
            int div = ticksSinceLastEffect / 100;
            if (div > 0 && random.nextDouble() > (1d / div)) {
                TormentEffect effect = effects.random(random);
                if (effect != null) {
                    startEffect(effect);
                }
            }
        }
    }
    
    private static void startEffect(TormentEffect effect) {
        EffectConfig config = effect.start();
        ticksSinceLastEffect = 0;
        usedEffectLevel += config.strength();
        runningEffects.put(effect, new EffectRuntime(config));
        recalculateEffects();
    }
    
    private static void tickRunning() {
        Iterator<Map.Entry<TormentEffect, EffectRuntime>> itr = runningEffects.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<TormentEffect, EffectRuntime> entry = itr.next();
            if (entry.getValue().tick()) {
                entry.getKey().stop();
                usedEffectLevel -= entry.getValue().config.strength();
                itr.remove();
                int coolDownTime = entry.getKey().minCoolDown();
                if (coolDownTime > 0) {
                    coolDowns.put(entry.getKey(), coolDownTime);
                }
                recalculateEffects();
            } else {
                entry.getKey().update();
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
        } else {
            TormentData data = TormentData.get(Minecraft.getInstance().player);
            float effectLevel = data.getEffectLevel();
            WeightTable.Builder<TormentEffect> builder = WeightTable.builder();
            for (TormentEffect effect : allEffects) {
                if (!runningEffects.containsKey(effect) && !coolDowns.containsKey(effect)
                        && (effectLevel - usedEffectLevel) >= effect.minLevel()) {
                    builder.add(effect, effect.weight());
                }
            }
            effects = builder.build();
        }
    }
    
    public boolean isRunning(TormentEffect effect) {
        return runningEffects.containsKey(effect);
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
