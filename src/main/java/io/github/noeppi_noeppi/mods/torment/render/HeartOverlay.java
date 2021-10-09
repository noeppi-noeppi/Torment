package io.github.noeppi_noeppi.mods.torment.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.noeppi_noeppi.mods.torment.Torment;
import io.github.noeppi_noeppi.mods.torment.cap.TormentData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

import javax.annotation.Nonnull;

public class HeartOverlay implements IIngameOverlay {

    private static final ResourceLocation MOD_ICONS = Torment.getInstance().resource("textures/icons.png");

    public static HeartOverlay INSTANCE = new HeartOverlay();
    
    private FakeGui fakeGui = null;
    
    @Override
    public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int width, int height) {
        if (Minecraft.getInstance().options.hideGui || !gui.shouldDrawSurvivalElements()) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        
        int purple = (int) Math.ceil(TormentData.get(player).getTormentLevel());
        if (purple > 0) {
            if (fakeGui == null) fakeGui = new FakeGui(Minecraft.getInstance());

            fakeGui.purple = purple;
            
            fakeGui.left_height = 39;
            fakeGui.healthBlinkTime = gui.healthBlinkTime;
            fakeGui.tickCount = gui.tickCount;
            fakeGui.lastHealthTime = gui.lastHealthTime;
            fakeGui.displayHealth = gui.displayHealth;
            fakeGui.setBlitOffset(gui.getBlitOffset());

            gui.setupOverlayRenderState(true, false);
            fakeGui.renderHealth(width, height, poseStack);
            
            gui.setBlitOffset(fakeGui.getBlitOffset());
        }
    }
    
    private static final class FakeGui extends ForgeIngameGui {

        public int purple;
        
        public FakeGui(Minecraft mc) {
            super(mc);
        }

        protected void renderHearts(@Nonnull PoseStack poseStack, @Nonnull Player player, int left, int top, int rowHeight, int regen, float healthMax, int health, int lastHealth, int absorption, boolean highlight) {
            int hearts = Mth.ceil(healthMax / 2d);
            int golden = Mth.ceil(absorption / 2d);
            boolean hardcore = player.level.getLevelData().isHardcore();

            for (int idx = hearts + golden - 1; idx >= 0; idx--) {
                int line = idx / 10;
                int column = idx % 10;
                int x = left + column * 8;
                int y = top - line * rowHeight;
                if (health + absorption <= 4) {
                    y += this.random.nextInt(2);
                }
                if (idx < hearts && idx == regen) {
                    y -= 2;
                }
                
                if (idx + 1 <= Math.ceil(purple / 2d)) {
                    this.renderHeart(poseStack, 0, x, y, 20, highlight, false, hardcore);

                    int fullHeartLife = (idx + 1) * 2;

                    if (idx < hearts) {

                        if (highlight && fullHeartLife < lastHealth) {
                            this.renderHeart(poseStack, 18, x, y, 40, fullHeartLife + 1 == lastHealth, true, hardcore);
                        }

                        if (fullHeartLife < health) {
                            this.renderHeart(poseStack, 18, x, y, 40, fullHeartLife > purple || fullHeartLife + 1 == health, false, hardcore);
                        }
                    }
                }
            }
        }
        
        private void renderHeart(PoseStack poseStack, int baseOffset, int x, int y, int z, boolean off1, boolean off2, boolean off3) {
            RenderSystem.setShaderTexture(0, MOD_ICONS);
            int u = baseOffset + (9 * ((off1 ? 1 : 0) + (off2 ? 2 : 0) + (off3 ? 4 : 0)));
            Gui.blit(poseStack, x, y, this.getBlitOffset() + z, u, 0, 9, 9, 256, 256);
        }
    }
}
