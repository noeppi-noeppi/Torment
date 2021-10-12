package io.github.noeppi_noeppi.mods.torment.effect.instances;

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

public class StareEffect implements TormentEffect {

    public static final StareEffect INSTANCE = new StareEffect();

    private StareEffect() {

    }

    @Override
    public int minCoolDown() {
        return 7200;
    }

    @Override
    public float minLevel() {
        return 40;
    }

    @Override
    public int weight() {
        return 4;
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
