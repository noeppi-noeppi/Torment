package io.github.noeppi_noeppi.mods.torment.cap;

import io.github.noeppi_noeppi.mods.torment.Torment;
import io.github.noeppi_noeppi.mods.torment.network.TormentDataSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import javax.annotation.Nullable;

public class TormentData {
    
    public static TormentData get(Player player) {
        // Capability should always be there.
        // If not print a warning and get default instance
        return player.getCapability(CapabilityTorment.DATA).orElseGet(() -> {
            Torment.getInstance().logger.warn("Torment player capability not present on player: " + player);
            return new TormentData();
        });
    }
    
    @Nullable
    private ServerPlayer player;
    
    private float targetLevel;
    private float tormentLevel;
    private float effectLevel;
    
    private boolean shouldSync;
    
    // Called when the capability is attached to the player
    public void attach(ServerPlayer player) {
        this.player = player;
    }
    
    public void tick() {
        if (this.player != null) {
            if (player.tickCount % 20 == 17 && targetLevel != tormentLevel) {
                float diff = Mth.clamp(targetLevel - tormentLevel, -0.25f, 0.25f);
                tormentLevel += diff;
                sync();
            }
            if (player.tickCount % 5 == 3) {
                float tormentDiff = Math.max(0, 10 + this.player.getHealth() - tormentLevel);
                float tormentFactor = tormentDiff / 5;
                float healthFactor = 2 * (1 - (this.player.getHealth() / this.player.getMaxHealth()));
                float dest = tormentDiff >= 20 ? 0 : tormentLevel * tormentFactor * healthFactor;
                float newEffect = ((2 * effectLevel) + dest) / 3f;
                if (dest != effectLevel) {
                    if (dest < effectLevel) {
                        effectLevel = Mth.clamp((float) Math.floor(newEffect * 10) / 10f, 0, 80);
                    } else {
                        effectLevel = Mth.clamp((float) Math.ceil(newEffect * 10) / 10f, 0, 80);
                    }
                    sync();
                }
            }
            
            if (shouldSync) {
                Torment.getNetwork().channel.send(PacketDistributor.PLAYER.with(() -> this.player), new TormentDataSerializer.TormentDataMessage(this.write()));
            }
        }
    }

    public float getTormentLevel() {
        return tormentLevel;
    }

    public float getEffectLevel() {
        return effectLevel;
    }
    
    public void deathValues() {
        effectLevel = 0;
        tormentLevel = 0;
        targetLevel /= 2;
    }
    
    public void addLevel(float value) {
        // No sync. Will sync next time in tick when updating
        // target level is not relevant on the client anyways
        this.targetLevel = Mth.clamp(this.targetLevel + value, 0, 20);
    }
    
    public void setLevel(float value) {
        // No sync. Will sync next time in tick when updating
        // target level is not relevant on the client anyways
        this.targetLevel = Mth.clamp(value, 0, 20);
    }

    public CompoundTag write() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("TargetLevel", targetLevel);
        nbt.putFloat("TormentLevel", tormentLevel);
        nbt.putFloat("EffectLevel", effectLevel);
        return nbt;
    }

    public void read(CompoundTag nbt) {
        targetLevel = Mth.clamp(nbt.getFloat("TargetLevel"), 0, 20);
        tormentLevel = Mth.clamp(nbt.getFloat("TormentLevel"), 0, 20);
        effectLevel = Mth.clamp(nbt.getFloat("EffectLevel"), 0, 80);
    }
    
    protected void sync() {
        shouldSync = true;
    }
}
