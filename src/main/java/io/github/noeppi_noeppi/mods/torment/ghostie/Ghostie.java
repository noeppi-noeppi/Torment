package io.github.noeppi_noeppi.mods.torment.ghostie;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

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

    @Override
    protected void defineSynchedData() {
        //
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundTag compound) {
        //
    }

    @Override
    protected void addAdditionalSaveData(@Nonnull CompoundTag compound) {
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
