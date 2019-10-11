package org.gr1m.mc.mup.bugfix.mc111978.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketSpawnObject;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc111978.network.MC111978PacketHandler;
import org.gr1m.mc.mup.bugfix.mc111978.network.SPacketSpawnObjectWithMeta;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(EntityTrackerEntry.class)
public class MixinEntityTrackerEntry
{
    @Shadow
    @Final
    private Entity trackedEntity;
    
    @Redirect(method = "updatePlayerEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetHandlerPlayServer;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0),
              slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityTrackerEntry;createSpawnPacket()Lnet/minecraft/network/Packet;"),
                             to = @At(value = "NEW", target = "net/minecraft/network/play/server/SPacketEntityMetadata")))
    private void combineObjectAndMeta(NetHandlerPlayServer handler, Packet<?> packetIn, EntityPlayerMP playerMP)
    {
        if (packetIn instanceof SPacketSpawnObject && !this.trackedEntity.getDataManager().isEmpty() && Mup.config.mc111978.enabled &&
            MC111978PacketHandler.registered_clients.contains(playerMP.connection))
        {
            SPacketEntityMetadata metadata = new SPacketEntityMetadata(this.trackedEntity.getEntityId(), this.trackedEntity.getDataManager(), true);
            SPacketSpawnObjectWithMeta newPacket = new SPacketSpawnObjectWithMeta((SPacketSpawnObject)packetIn, metadata);
            
            MC111978PacketHandler.INSTANCE.sendTo(newPacket, playerMP);
            
            return;
        }
        
        handler.sendPacket(packetIn);

        if (!this.trackedEntity.getDataManager().isEmpty())
        {
            playerMP.connection.sendPacket(new SPacketEntityMetadata(this.trackedEntity.getEntityId(), this.trackedEntity.getDataManager(), true));
        }
    }
    
    @Redirect(method = "updatePlayerEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/datasync/EntityDataManager;isEmpty()Z", ordinal = 0),
              slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityTrackerEntry;createSpawnPacket()Lnet/minecraft/network/Packet;"),
                             to = @At(value = "NEW", target = "net/minecraft/network/play/server/SPacketEntityMetadata")))
    private boolean cancelSeparateMetadata(EntityDataManager manager, EntityPlayerMP playerMP)
    {
        return true;
    }
}
