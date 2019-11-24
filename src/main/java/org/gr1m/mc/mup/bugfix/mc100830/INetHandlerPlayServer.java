package org.gr1m.mc.mup.bugfix.mc100830;

import org.gr1m.mc.mup.bugfix.mc100830.network.CPacketVehicleMoveWithMotion;

public interface INetHandlerPlayServer
{
    void processVehicleMoveWithMotion(CPacketVehicleMoveWithMotion packetIn);
}
