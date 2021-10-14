package io.github.noeppi_noeppi.mods.torment.ghostie;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import io.github.noeppi_noeppi.libx.render.RenderHelperLevel;
import io.github.noeppi_noeppi.mods.torment.Torment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class GhostieRenderer {
    
    private static final GhostieModel model = new GhostieModel();
    private static final Random random = new Random();
    private static final List<RenderType> types = IntStream.range(0, 5)
            .mapToObj(i -> Torment.getInstance().resource("textures/entity/ghostie/ghostie" + i + ".png"))
            .map(model::renderType)
            .toList();
    
    public static void renderGhostie(Ghostie ghostie, PoseStack poseStack) {
        float partialTicks = Minecraft.getInstance().getDeltaFrameTime();
        poseStack.pushPose();
        Vec3 pos = ghostie.getPosition(Minecraft.getInstance().getDeltaFrameTime());
        RenderHelperLevel.loadProjection(poseStack, pos);
        poseStack.translate(0, -0.7, 0.1);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.rotLerp(partialTicks, ghostie.yRotO, ghostie.getYRot())));
        random.setSeed(ghostie.getUUID().getLeastSignificantBits());
        RenderType type = types.get(random.nextInt(types.size()));
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertex = buffer.getBuffer(type);
        model.setupAnim(ghostie, 0, 0, 0, 0, 0);
        model.renderToBuffer(poseStack, vertex, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        buffer.endBatch(type);
        poseStack.popPose();
    }
}
