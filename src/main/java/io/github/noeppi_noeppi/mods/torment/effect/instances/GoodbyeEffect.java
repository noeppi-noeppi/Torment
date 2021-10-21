package io.github.noeppi_noeppi.mods.torment.effect.instances;

import io.github.noeppi_noeppi.mods.torment.config.TormentConfig;
import io.github.noeppi_noeppi.mods.torment.effect.EffectConfig;
import io.github.noeppi_noeppi.mods.torment.effect.TormentEffect;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;

import javax.annotation.Nullable;
import java.util.Random;

public class GoodbyeEffect implements TormentEffect {

    public static final GoodbyeEffect INSTANCE = new GoodbyeEffect();

    private GoodbyeEffect() {

    }

    @Override
    public int minCoolDown() {
        return 0;
    }

    @Override
    public float minLevel() {
        return TormentConfig.effects.goodbye.min_level();
    }

    @Override
    public int weight() {
        return TormentConfig.effects.goodbye.weight();
    }

    @Nullable
    @Override
    public EffectConfig start(LocalPlayer player, Random random) {
        player.connection.handleDisconnect(new ClientboundDisconnectPacket(new TextComponent("Ahhhh AAAAhhh Ahhhh")));
        return EffectConfig.instant(0);
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
