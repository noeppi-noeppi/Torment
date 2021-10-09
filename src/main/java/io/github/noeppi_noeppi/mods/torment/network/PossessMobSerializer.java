package io.github.noeppi_noeppi.mods.torment.network;

import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import net.minecraft.network.FriendlyByteBuf;

public class PossessMobSerializer implements PacketSerializer<PossessMobSerializer.PossessMobMessage> {

    @Override
    public Class<PossessMobMessage> messageClass() {
        return PossessMobMessage.class;
    }

    @Override
    public void encode(PossessMobMessage msg, FriendlyByteBuf buffer) {
        //
    }

    @Override
    public PossessMobMessage decode(FriendlyByteBuf buffer) {
        return new PossessMobMessage();
    }

    public static record PossessMobMessage() {}
}
