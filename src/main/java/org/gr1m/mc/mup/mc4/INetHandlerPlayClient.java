package org.gr1m.mc.mup.mc4;

import org.gr1m.mc.mup.mc4.network.SPacketNewEntityLookMove;
import org.gr1m.mc.mup.mc4.network.SPacketNewEntityRelMove;

public interface INetHandlerPlayClient {
    void handleNewEntityRelMove(SPacketNewEntityRelMove packetIn);
    void handleNewEntityLookMove(SPacketNewEntityLookMove packetIn);
}
