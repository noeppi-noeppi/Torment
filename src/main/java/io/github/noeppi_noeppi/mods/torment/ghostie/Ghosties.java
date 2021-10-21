package io.github.noeppi_noeppi.mods.torment.ghostie;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.noeppi_noeppi.mods.torment.config.TormentConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class Ghosties {
    
    private static final List<Ghostie> ghosties = new ArrayList<>();

    public static void tick() {
        Player player = Minecraft.getInstance().player;
        Level level = Minecraft.getInstance().level;
        if (player != null && level != null) {
            for (int i = 0; i < ghosties.size(); i++) {
                Ghostie ghostie = ghosties.get(i);
                if (ghostie.level != level) {
                    // Dimension changed or sth
                    // get a fresh ghostie
                    ghostie = new Ghostie();
                    ghostie.teleportNearPlayer();
                    ghosties.set(i, ghostie);
                }
                ghostie.ghostieTick(player);
            }
        }
    }
    
    public static void updateGhosties(int amount) {
        if (ghosties.size() > amount && amount >= 0) {
            while (ghosties.size() > amount) ghosties.remove(0);
        } else if (ghosties.size() < amount) {
            while (ghosties.size() < amount) {
                Ghostie ghostie = new Ghostie();
                ghostie.teleportNearPlayer();
                ghosties.add(ghostie);
            }
        }
    }
    
    public static void reset() {
        ghosties.clear();
    }
    
    public static void updateByLevel(float level) {
        if (level < TormentConfig.ghosties.min_level) {
            updateGhosties(0);
        } else {
            updateGhosties(Math.round(((2.5f * (float) Math.atan((0.16f * (level + TormentConfig.ghosties.adjustment_x)) - 7)) + 4.4f) + TormentConfig.ghosties.adjustment_y));
        }
    }
    
    public static void render(PoseStack poseStack) {
        Level level = Minecraft.getInstance().level;
        for (Ghostie ghostie : ghosties) {
            if (ghostie.level == level) {
                GhostieRenderer.renderGhostie(ghostie, poseStack);
            }
        }
    }
}
