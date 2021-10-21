package io.github.noeppi_noeppi.mods.torment.effect.instances;

import io.github.noeppi_noeppi.mods.torment.config.TormentConfig;
import io.github.noeppi_noeppi.mods.torment.effect.DefaultTormentEffect;
import io.github.noeppi_noeppi.mods.torment.effect.EffectConfig;
import io.github.noeppi_noeppi.mods.torment.effect.TormentEffect;
import io.github.noeppi_noeppi.mods.torment.util.WeightTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import javax.annotation.Nullable;
import java.util.Random;

public class SoundEffect extends DefaultTormentEffect {

    public static final SoundEffect INSTANCE = new SoundEffect();
    
    private final WeightTable<SoundEvent> sounds = WeightTable.<SoundEvent>builder()
            .add(SoundEvents.CREEPER_PRIMED, 10)
            .add(SoundEvents.MUSIC_DISC_11, 1)
            .add(SoundEvents.ANVIL_BREAK, 3)
            .add(SoundEvents.GENERIC_EXPLODE, 3)
            .add(SoundEvents.BELL_RESONATE, 3)
            .add(SoundEvents.STRIDER_RETREAT, 5)
            .add(SoundEvents.BEEHIVE_SHEAR, 5)
            .add(SoundEvents.CHEST_OPEN, 5)
            .build();
    
    private SoundInstance soundToStart = null;
    private SoundInstance soundToStop = null;

    private SoundEffect() {
        super(() -> TormentConfig.effects.sounds);
    }

    @Nullable
    @Override
    public EffectConfig start(LocalPlayer player, Random random) {
        SoundEvent event = sounds.random(random);
        if (event != null) {
            float volume = 0.5f + random.nextFloat();
            float pitch = 0.8f + (random.nextFloat() * 0.4f);
            double x = player.getX() + ((random.nextDouble() - 0.5) * 20);
            double y = player.getY() + ((random.nextDouble() - 0.5) * 10);
            double z = player.getZ() + ((random.nextDouble() - 0.5) * 20);
            SoundInstance instance = new SimpleSoundInstance(event, SoundSource.MASTER, volume, pitch, x, y, z);
            Minecraft.getInstance().getSoundManager().play(instance);
            if (event == SoundEvents.MUSIC_DISC_11) {
                soundToStop = instance;
                return new EffectConfig(100 + random.nextInt(500), 5);
            } else if (event == SoundEvents.CHEST_OPEN) {
                soundToStart = new SimpleSoundInstance(SoundEvents.CHEST_CLOSE, SoundSource.MASTER, volume, pitch, x, y, z);
                return new EffectConfig(40 + random.nextInt(160), 5);
            } else {
                return EffectConfig.instant(1);
            }
        } else {
            return EffectConfig.instant(0);
        }
    }

    @Override
    public void update(LocalPlayer player, Random random) {
        //
    }

    @Override
    public void stop(LocalPlayer player, Random random) {
        if (soundToStop != null) {
            Minecraft.getInstance().getSoundManager().stop(soundToStop);
            soundToStop = null;
        }
        if (soundToStart != null) {
            Minecraft.getInstance().getSoundManager().play(soundToStart);
            soundToStart = null;
        }
    }
}
