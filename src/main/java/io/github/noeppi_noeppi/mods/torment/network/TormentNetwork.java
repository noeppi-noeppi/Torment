package io.github.noeppi_noeppi.mods.torment.network;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.network.NetworkX;
import net.minecraftforge.fmllegacy.network.NetworkDirection;

public class TormentNetwork extends NetworkX {

    public TormentNetwork(ModX mod) {
        super(mod);
    }

    @Override
    protected Protocol getProtocol() {
        return Protocol.of("1");
    }

    @Override
    protected void registerPackets() {
        this.register(new TormentDataSerializer(), () -> TormentDataHandler::handle, NetworkDirection.PLAY_TO_CLIENT);
    }
}
