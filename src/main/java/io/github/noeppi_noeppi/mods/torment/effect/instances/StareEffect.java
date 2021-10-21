package io.github.noeppi_noeppi.mods.torment.effect.instances;

import io.github.noeppi_noeppi.mods.torment.config.TormentConfig;
import io.github.noeppi_noeppi.mods.torment.effect.DefaultTormentEffect;
import io.github.noeppi_noeppi.mods.torment.effect.EffectConfig;
import io.github.noeppi_noeppi.mods.torment.effect.TormentEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class StareEffect extends DefaultTormentEffect {

    public static final StareEffect INSTANCE = new StareEffect();

    private StareEffect() {
        super(() -> TormentConfig.effects.stare);
    }

    @Override
    public List<TormentEffect> cantRunWhile() {
        return List.of(MobSwapEffect.INSTANCE);
    }
    
    @Nullable
    @Override
    public EffectConfig start(LocalPlayer player, Random random) {
        return new EffectConfig(3600 + random.nextInt(4800), 3);
    }

    @Override
    public void update(LocalPlayer player, Random random) {
        //
    }

    @Override
    public void stop(LocalPlayer player, Random random) {
        //
    }
}
