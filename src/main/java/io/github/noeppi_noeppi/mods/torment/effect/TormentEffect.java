package io.github.noeppi_noeppi.mods.torment.effect;

import net.minecraft.client.player.LocalPlayer;

import javax.annotation.Nullable;
import java.util.Random;

public interface TormentEffect {
    
    int minCoolDown();
    float minLevel();
    int weight();
    
    @Nullable
    EffectConfig start(LocalPlayer player, Random random);
    void update(LocalPlayer player, Random random);
    void stop(LocalPlayer player, Random random);
}
