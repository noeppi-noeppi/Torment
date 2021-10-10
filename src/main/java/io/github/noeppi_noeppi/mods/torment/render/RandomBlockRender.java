package io.github.noeppi_noeppi.mods.torment.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.noeppi_noeppi.libx.render.RenderHelperLevel;
import io.github.noeppi_noeppi.mods.torment.effect.EffectManager;
import io.github.noeppi_noeppi.mods.torment.effect.instances.RandomBlocksEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Map;

public class RandomBlockRender {
    
    public static void renderWorld(RenderWorldLastEvent event) {
        if (EffectManager.isRunning(RandomBlocksEffect.INSTANCE)) {
            Level level = Minecraft.getInstance().level;
            if (level != null) {
                PoseStack poseStack = event.getMatrixStack();
                MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                for (Map.Entry<BlockPos, BlockState> entry : RandomBlocksEffect.INSTANCE.getBlocks().entrySet()) {
                    if (level.getBlockState(entry.getKey()).isAir()) {
                        poseStack.pushPose();
                        RenderHelperLevel.loadProjection(poseStack, entry.getKey());
                        int light = level.getLightEngine().getRawBrightness(entry.getKey(), level.getSkyDarken());
                        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                                entry.getValue(), poseStack, buffer, LightTexture.pack(light, light),
                                OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
                        );
                        buffer.endBatch();
                        poseStack.popPose();
                    }
                }
            }
        }
    }
}
