package io.github.noeppi_noeppi.mods.torment.network;

import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class TormentDataSerializer implements PacketSerializer<TormentDataSerializer.TormentDataMessage> {

    @Override
    public Class<TormentDataMessage> messageClass() {
        return TormentDataMessage.class;
    }

    @Override
    public void encode(TormentDataMessage msg, FriendlyByteBuf buffer) {
        buffer.writeNbt(msg.data());
    }

    @Override
    public TormentDataMessage decode(FriendlyByteBuf buffer) {
        return new TormentDataMessage(buffer.readNbt());
    }

    public static record TormentDataMessage(CompoundTag data) {}
}
