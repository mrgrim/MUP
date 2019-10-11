package org.gr1m.mc.mup.bugfix.mc111978;

import org.gr1m.mc.mup.bugfix.mc111978.network.SPacketSpawnObjectWithMeta;

public interface INetHandlerPlayClient
{
    void handleSpawnObjectWithMeta(SPacketSpawnObjectWithMeta packetIn);
}
