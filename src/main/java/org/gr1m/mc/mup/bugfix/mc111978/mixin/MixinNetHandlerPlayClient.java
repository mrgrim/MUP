package org.gr1m.mc.mup.bugfix.mc111978.mixin;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketSpawnObject;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc111978.INetHandlerPlayClient;
import org.gr1m.mc.mup.bugfix.mc111978.network.SPacketSpawnObjectWithMeta;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient implements INetHandlerPlayClient
{
    @Shadow
    public abstract void handleSpawnObject(SPacketSpawnObject packetIn);
    
    @Shadow
    public abstract void handleEntityMetadata(SPacketEntityMetadata packetIn);
    
    public void handleSpawnObjectWithMeta(SPacketSpawnObjectWithMeta packetIn)
    {
        this.handleSpawnObject(packetIn.getObjectPacket());
        this.handleEntityMetadata(packetIn.getMetadataPacket());
    }
}
