package io.github.noeppi_noeppi.mods.torment.cap;

import io.github.noeppi_noeppi.libx.util.LazyValue;
import io.github.noeppi_noeppi.mods.torment.Torment;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityTorment {

    public static final ResourceLocation KEY = Torment.getInstance().resource("data");
    
    public static Capability<TormentData> DATA = CapabilityManager.get(new CapabilityToken<>() {});
    
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(TormentData.class);
    }

    public static void attachPlayerCaps(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(KEY, new Provider(new LazyValue<>(() -> {
                TormentData data = new TormentData();
                if (player instanceof ServerPlayer serverPlayer) data.attach(serverPlayer);
                return data;
            })));
        }
    }
    
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        event.getPlayer().getCapability(DATA).ifPresent(TormentData::sync);
    }
    
    @SuppressWarnings("CodeBlock2Expr")
    public static void playerCopy(PlayerEvent.Clone event) {
        // Keep the data on death
        if (event.isWasDeath()) {
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(DATA).ifPresent(oldData -> {
                event.getPlayer().getCapability(DATA).ifPresent(newData -> {
                    newData.read(oldData.write());
                    newData.deathValues();
                });
            });
            event.getOriginal().invalidateCaps();
        }
    }
    
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        event.player.getCapability(DATA).ifPresent(TormentData::tick);
    }
    
    private static class Provider implements ICapabilityProvider, INBTSerializable<Tag> {
        
        public final LazyValue<TormentData> value;

        public <T> Provider(LazyValue<TormentData> value) {
            this.value = value;
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            //noinspection NullableProblems
            return cap == DATA ? LazyOptional.of(this.value::get).cast() : LazyOptional.empty();
        }

        @Override
        public Tag serializeNBT() {
            return this.value.get().write();
        }

        @Override
        public void deserializeNBT(Tag nbt) {
            if (nbt instanceof CompoundTag tag) {
                this.value.get().read(tag);
            }
        }
    }
}
