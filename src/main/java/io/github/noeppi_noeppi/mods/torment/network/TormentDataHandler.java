package io.github.noeppi_noeppi.mods.torment.network;

import io.github.noeppi_noeppi.mods.torment.cap.CapabilityTorment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class TormentDataHandler {

    public static void handle(TormentDataSerializer.TormentDataMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.getCapability(CapabilityTorment.DATA).ifPresent(cap -> cap.read(msg.data()));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
