package io.github.noeppi_noeppi.mods.torment.effect.instances;

import io.github.noeppi_noeppi.mods.torment.config.TormentConfig;
import io.github.noeppi_noeppi.mods.torment.effect.DefaultTormentEffect;
import io.github.noeppi_noeppi.mods.torment.effect.EffectConfig;
import io.github.noeppi_noeppi.mods.torment.effect.TormentEffect;
import net.minecraft.client.player.LocalPlayer;

import javax.annotation.Nullable;
import java.util.Random;

public class MouseStutterEffect extends DefaultTormentEffect {

    public static final MouseStutterEffect INSTANCE = new MouseStutterEffect();

    private MouseStutterEffect() {
        super(() -> TormentConfig.effects.mouse_stutter);
    }

    @Nullable
    @Override
    public EffectConfig start(LocalPlayer player, Random random) {
        return new EffectConfig(500 + random.nextInt(1500), 5);
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
