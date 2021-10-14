package io.github.noeppi_noeppi.mods.torment.ghostie;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

// Never added to the world
public class Ghostie extends Entity {
    
    private static final Random random = new Random();
    
    private final float maxAngle = 5 + (15 * random.nextFloat());
    private final double closeNess = 2 + (7 * random.nextDouble());
    private final float closeToleration = 4 + (2 * random.nextFloat());
    private final double speedFactor = 0.004 + (0.004 * random.nextDouble());
    private final float altToleration = 2 + (2 * random.nextFloat());
    private final double altFactor = 0.008 + (0.004 * random.nextDouble());
    private final float eyeHeightTarget = 0.5f + (0.7f * random.nextFloat());
    private final float rotLerpFactor = 600 + (240 * random.nextFloat());
    private final double fleeSpeed = 0.02 + (random.nextDouble() * 0.02);
    
    public Ghostie() {
        super(EntityType.FISHING_BOBBER /* Catch a nice little ghostie fish! */, Objects.requireNonNull(Minecraft.getInstance().level));
        // Minecraft restricts some uuids for entities. Probably for a reason. But as ghosties are never added
        // to the world, this should not be a problem. And the renderer relies on random uuids.
        this.uuid = UUID.randomUUID();
    }

    public void ghostieTick(Player player) {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();

        Vec3 entityPos = this.position();
        Vec3 playerPos = player.position().add(0, eyeHeightTarget * player.getEyeHeight(), 0);
        double xd = entityPos.x - playerPos.x;
        double zd = entityPos.z - playerPos.z;
        double horDist = Math.sqrt((xd * xd) + (zd * zd));
        
        float targetRot = zd == 0 ? Float.NaN : (float) -Mth.wrapDegrees(Math.toDegrees(Mth.atan2(zd, xd)) + 90);
        float rotDiff = 180;
        if (!Float.isNaN(targetRot)) {
            rotDiff = Math.abs(Mth.wrapDegrees(this.getYRot() - targetRot));
            this.setYRot(Mth.wrapDegrees(Mth.rotLerp(rotDiff / rotLerpFactor, getYRot(), targetRot)));
        }
        
        float targetPitch = (float) Math.toDegrees(Math.atan((entityPos.y - playerPos.y) / horDist));
        float pitchDiff = Math.abs(Mth.wrapDegrees(this.getXRot() - targetPitch));
        this.setXRot(Mth.wrapDegrees(Mth.rotLerp(pitchDiff / rotLerpFactor, getXRot(), targetPitch)));
        
        double move = 0;
        if (horDist < closeNess) {
            move = -fleeSpeed;
        } else if (horDist > closeNess + closeToleration) {
            move = Math.sqrt((horDist - 5) * speedFactor);
        }
        double xm = 0;
        double zm = 0;
        if (move != 0) {
            xm = move * Math.sin(Math.toRadians(rotDiff < maxAngle || Float.isNaN(targetRot) ? this.getYRot() : targetRot));
            zm = move * Math.cos(Math.toRadians(rotDiff < maxAngle || Float.isNaN(targetRot) ? this.getYRot() : targetRot));
        }
        
        double verDist = (playerPos.y - entityPos.y) + 2;
        double ym = 0;
        if (Math.abs(verDist) > altToleration) {
            ym = Math.signum(verDist) * Math.sqrt((Math.abs(verDist) - altToleration) * altFactor);
        }
        if (Math.abs(horDist) > 200 || Math.abs(verDist) > 50) {
            // The ghostie got lost. Teleport near the player
            teleportNear(player);
        } else {
            this.setPos(entityPos.add(xm, ym, zm));
        }
    }
    
    public void teleportNearPlayer() {
        if (Minecraft.getInstance().player != null) {
            teleportNear(Minecraft.getInstance().player);
        }
    }
    
    public void teleportNear(Player player) {
        this.setPos(player.position().add((Math.random() - 0.5) * 25, (Math.random() - 0.5) * 10, (Math.random() - 0.5) * 25));
    }

    @Override
    protected void defineSynchedData() {
        
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag compound) {
        //
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag compound) {
        //
    }

    @Nonnull
    @Override
    public Packet<?> getAddEntityPacket() {
        throw new NoSuchElementException("Can't add ghostie to world");
    }

    @Override
    public void onAddedToWorld() {
        throw new IllegalStateException("Can't add ghostie to world");
    }
}
