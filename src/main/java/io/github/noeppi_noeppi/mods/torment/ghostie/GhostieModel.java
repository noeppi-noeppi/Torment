package io.github.noeppi_noeppi.mods.torment.ghostie;

import io.github.noeppi_noeppi.libx.render.ClientTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;

public class GhostieModel extends HierarchicalModel<Ghostie> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart tentacle1;
    private final ModelPart tentacle2;
    private final ModelPart tentacle3;
    private final ModelPart tentacle4;
    private final ModelPart tentacle5;

    public GhostieModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -7.0F, -3.5F, 10.0F, 10.0F, 10.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 14.0F, 0.0F));
        part.addOrReplaceChild("tentacle1", CubeListBuilder.create().texOffs(16, 20).addBox(-1.0F, -1.0295F, -0.8779F, 2.0F, 11.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(-3.0F, 10.5F, 0.0F));
        part.addOrReplaceChild("tentacle2", CubeListBuilder.create().texOffs(32, 20).addBox(-1.0F, -1.0295F, -0.8779F, 2.0F, 11.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(3.0F, 10.5F, 0.0F));
        part.addOrReplaceChild("tentacle3", CubeListBuilder.create().texOffs(24, 20).addBox(-1.0F, -1.0295F, -0.8779F, 2.0F, 11.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F,  10.5F, 0.0F));
        part.addOrReplaceChild("tentacle4", CubeListBuilder.create().texOffs(8, 20).addBox(-1.0F, -1.0295F, -0.8779F, 2.0F, 11.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(1.5F, 10.5F, -3.0F));
        part.addOrReplaceChild("tentacle5", CubeListBuilder.create().texOffs(0, 20).addBox(-1.0F, -1.0295F, -0.8779F, 2.0F, 11.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(-1.5F, 10.5F, -3.0F));
        LayerDefinition layer = LayerDefinition.create(mesh, 64, 64);
        this.root = layer.bakeRoot();
        this.head = this.root.getChild("head");
        this.tentacle1 = this.root.getChild("tentacle1");
        this.tentacle2 = this.root.getChild("tentacle2");
        this.tentacle3 = this.root.getChild("tentacle3");
        this.tentacle4 = this.root.getChild("tentacle4");
        this.tentacle5 = this.root.getChild("tentacle5");
    }

    @Nonnull
    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(@Nonnull Ghostie entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float partialTicks = Minecraft.getInstance().getDeltaFrameTime();
        this.head.setRotation((180  + Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot())) / 180 * Mth.PI, 0, 0);
        rotatePart(this.tentacle1, (entity.getUUID().getLeastSignificantBits() & 0xFF00) >> 8, partialTicks);
        rotatePart(this.tentacle2, (entity.getUUID().getLeastSignificantBits() & 0xFF000000) >> 24, partialTicks);
        rotatePart(this.tentacle3, entity.getUUID().getMostSignificantBits() & 0xFF, partialTicks);
        rotatePart(this.tentacle4, (entity.getUUID().getMostSignificantBits() & 0xFF00) >> 8, partialTicks);
        rotatePart(this.tentacle5, (entity.getUUID().getMostSignificantBits() & 0xFF0000) >> 16, partialTicks);
    }
    
    private void rotatePart(ModelPart part, long seed, float partialTicks) {
        float deg = 25 + (float) (10 * Math.sin(0.04 * ((((int) seed) & 0xFF) + ClientTickHandler.ticksInGame + partialTicks)));
        part.setRotation((180 + deg) / 180 * Mth.PI, 0, 0);
    }
}
