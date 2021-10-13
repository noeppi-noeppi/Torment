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
import java.util.UUID;

// Never added to the world
public class Ghostie extends Entity {
    
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
        Vec3 playerPos = player.position();
        double xd = entityPos.x - playerPos.x;
        double zd = entityPos.z - playerPos.z;
        double horDist = Math.sqrt((xd * xd) + (zd * zd));
        
        float targetRot = zd == 0 ? Float.NaN : (float) -Mth.wrapDegrees(Math.toDegrees(Mth.atan2(zd, xd)) + 90);
        float rotDiff = 180;
        if (!Float.isNaN(targetRot)) {
            rotDiff = Math.abs(Mth.wrapDegrees(this.getYRot() - targetRot));
            this.setYRot(Mth.wrapDegrees(Mth.rotLerp(rotDiff / 720, getYRot(), targetRot)));
        }
        
        float targetPitch = (float) Math.toDegrees(Math.atan((entityPos.y - playerPos.y) / horDist));
        float pitchDiff = Math.abs(Mth.wrapDegrees(this.getXRot() - targetPitch));
        this.setXRot(Mth.wrapDegrees(Mth.rotLerp(pitchDiff / 720, getXRot(), targetPitch)));
        
        double move = 0;
        if (horDist < 5) {
            move = -0.03;
        } else if (horDist > 10) {
            move = Math.sqrt((horDist - 5) * 0.006);
        }
        double xm = 0;
        double zm = 0;
        if (move != 0) {
            xm = move * Math.sin(Math.toRadians(rotDiff < 10 || Float.isNaN(targetRot) ? this.getYRot() : targetRot));
            zm = move * Math.cos(Math.toRadians(rotDiff < 10 || Float.isNaN(targetRot) ? this.getYRot() : targetRot));
        }
        
        double verDist = (playerPos.y - entityPos.y) + 2;
        double ym = 0;
        if (Math.abs(verDist) > 3) {
            ym = Math.signum(verDist) * Math.sqrt((Math.abs(verDist) - 3) * 0.01);
        }
        if (Math.abs(horDist) > 200 || Math.abs(verDist) > 50) {
            // The ghostie got lost. Teleport near the player
            this.setPos(playerPos.add((Math.random() - 0.5) * 25, (Math.random() - 0.5) * 10, (Math.random() - 0.5) * 25));
        } else {
            this.setPos(entityPos.add(xm, ym, zm));
        }
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
