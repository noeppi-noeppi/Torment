package io.github.noeppi_noeppi.mods.torment.cap;

import io.github.noeppi_noeppi.mods.torment.Torment;
import io.github.noeppi_noeppi.mods.torment.ability.Ability;
import io.github.noeppi_noeppi.mods.torment.network.TormentDataSerializer;
import io.github.noeppi_noeppi.mods.torment.ritual.Ritual;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    
    private int cureTimer;
    private float curedLevels;
    private float targetLevel;
    private float tormentLevel;
    private float effectLevel;
    private final List<Ability> abilities = new ArrayList<>();
    private final List<UUID> pendingDevilMobs = new ArrayList<>();
    
    // Not saved, rituals will stop if you log out
    private Ritual currentRitual;
    private BlockPos currentRitualPos;
    private int currentRitualTick;
    
    private boolean shouldSync;
    
    // Called when the capability is attached to the player
    public void attach(ServerPlayer player) {
        this.player = player;
    }
    
    public void tick() {
        if (this.player != null) {
            float curedTarget = Math.max(0, targetLevel - curedLevels);
            if (player.tickCount % 20 == 17 && curedTarget != tormentLevel) {
                float diff = Mth.clamp(curedTarget - tormentLevel, -0.25f, 0.25f);
                tormentLevel += diff;
                sync();
            }
            if (player.tickCount % 5 == 3) {
                float tormentDiff = Math.max(0, 10 + tormentLevel - this.player.getHealth());
                float tormentFactor = tormentDiff / 5;
                float healthFactor = 2 * (1 - (this.player.getHealth() / (this.player.getMaxHealth() + 10)));
                float dest = tormentLevel <= 0 || tormentDiff <= 0 ? 0 : tormentLevel * tormentFactor * healthFactor;
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
            if (cureTimer > 0) {
                cureTimer -= 1;
            } else {
                curedLevels = 0;
            }
            if (currentRitual != null && currentRitualPos != null) {
                currentRitual.tick(player, player.getLevel(), currentRitualPos, currentRitualTick);
                currentRitualTick += 1;
                if (currentRitualTick >= currentRitual.duration()) {
                    Ability newAbility = currentRitual.getAbility();
                    if (newAbility != null) {
                        this.addAbility(newAbility);
                        player.getLevel().sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), 1, 0, 0, 0, 0);
                        player.getLevel().sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), 20, 0.4, 0.4, 0.4, 0.1);
                    }
                    currentRitual = null;
                    currentRitualPos = null;
                    currentRitualTick = 0;
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
    
    public boolean hasAbility(Ability ability) {
        return abilities.contains(ability);
    }
    
    public boolean addAbility(Ability ability) {
        if (!abilities.contains(ability)) {
            if (this.targetLevel + ability.targetCost > 20) {
                return false;
            }
            abilities.add(ability);
            this.targetLevel += ability.targetCost;
            sync();
            return true;
        } else {
            return false;
        }
    }
    
    public boolean tryStartRitual(Ritual ritual, BlockPos pos) {
        if (player == null) return false;
        if (currentRitual != null) return false;
        if (ritual.getAbility() != null) {
            Ability ability = ritual.getAbility();
            if (abilities.contains(ability)) return false;
            if (this.targetLevel + ability.targetCost > 20) return false;
        }
        if (ritual.duration() > 1) {
            this.currentRitual = ritual;
            this.currentRitualPos = pos.immutable();
            this.currentRitualTick = 1;
        }
        ritual.tick(player, player.getLevel(), pos, 0);
        return true;
    }
    
    public void deathValues() {
        // Keep target level
        effectLevel = 0;
        tormentLevel = 0;
        pendingDevilMobs.clear();
    }
    
    public void cure(int time, float levels) {
        cureTimer = Math.max(cureTimer, time);
        curedLevels += levels;
        sync();
    }
    
    public void addPendingDevilMob(Entity mob) {
        if (pendingDevilMobs.size() >= 20) {
            pendingDevilMobs.remove(0);
        }
        if (!this.pendingDevilMobs.contains(mob.getUUID())) {
            this.pendingDevilMobs.add(mob.getUUID());
        }
    }
    
    public void targetAggro(@Nullable Entity target) {
        if (player != null && target instanceof LivingEntity living) {
            for (UUID uid : this.pendingDevilMobs) {
                if (player.getLevel().getEntity(uid) instanceof Mob mob) {
                    if (target != mob) {
                        mob.setTarget(living);
                    }
                }
            }
        }
    }
    
    public boolean isPossessed(@Nullable Entity target) {
        return target != null && this.pendingDevilMobs.contains(target.getUUID());
    }

    public CompoundTag write() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("TargetLevel", targetLevel);
        nbt.putFloat("TormentLevel", tormentLevel);
        nbt.putFloat("EffectLevel", effectLevel);
        nbt.putInt("CureTimer", cureTimer);
        nbt.putFloat("CuredLevels", curedLevels);
        
        ListTag abilityList = new ListTag();
        for (Ability ability : abilities) {
            abilityList.add(StringTag.valueOf(ability.name()));
        }
        nbt.put("Abilities", abilityList);
        
        ListTag targetList = new ListTag();
        for (UUID uid : pendingDevilMobs) {
            targetList.add(NbtUtils.createUUID(uid));
        }
        nbt.put("DevilMobs", targetList);
        
        return nbt;
    }

    public void read(CompoundTag nbt) {
        targetLevel = Mth.clamp(nbt.getFloat("TargetLevel"), 0, 20);
        tormentLevel = Mth.clamp(nbt.getFloat("TormentLevel"), 0, 20);
        effectLevel = Mth.clamp(nbt.getFloat("EffectLevel"), 0, 80);
        cureTimer = nbt.getInt("CureTimer");
        curedLevels = nbt.getFloat("CuredLevels");
        
        ListTag abilityList = nbt.getList("Abilities", Constants.NBT.TAG_STRING);
        abilities.clear();
        for (Tag tag : abilityList) {
            String key = tag.getAsString();
            try {
                abilities.add(Ability.valueOf(key));
            } catch (IllegalArgumentException e) {
                //
            }
        }

        ListTag targetList = nbt.getList("DevilMobs", Constants.NBT.TAG_INT_ARRAY);
        pendingDevilMobs.clear();
        for (Tag tag : targetList) {
            try {
                pendingDevilMobs.add(NbtUtils.loadUUID(tag));
            } catch (IllegalArgumentException e) {
                //
            }
        }
    }
    
    protected void sync() {
        shouldSync = true;
    }
}
