package io.github.noeppi_noeppi.mods.torment.ghostie;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.noeppi_noeppi.libx.util.CachedValue;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class Ghosties {
    
    // For testing
    public static final CachedValue<Ghostie> ghostie = new CachedValue<>(Ghostie::new);

    public static void tick() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            ghostie.get().ghostieTick(player);
        }
    }
    
    public static void render(PoseStack poseStack) {
        GhostieRenderer.renderGhostie(ghostie.get(), poseStack);
    }
}
