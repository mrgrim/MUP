package org.gr1m.mc.mup.mc4.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.gr1m.mc.mup.mc4.network.MC4PacketHandler;
import org.gr1m.mc.mup.mc4.network.SPacketNewEntityLookMove;
import org.gr1m.mc.mup.mc4.network.SPacketNewEntityRelMove;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import scala.xml.Null;

import java.util.List;
import java.util.Set;

@Mixin(EntityTrackerEntry.class)
public abstract class MixinEntityTrackerEntry {
    @Shadow
    @Final
    private Entity trackedEntity;

    @Shadow @Final public Set<EntityPlayerMP> trackingPlayers;
    private double posX;
    private double posY;
    private double posZ;
    
    private IMessage packet2;
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(Entity entityIn, int rangeIn, int maxRangeIn, int updateFrequencyIn, boolean sendVelocityUpdatesIn, CallbackInfo ci)
    {
        this.posX = entityIn.posX;
        this.posY = entityIn.posY;
        this.posZ = entityIn.posZ;
    }
    
    @Inject(method = "updatePlayerList", at = @At(value = "NEW", target = "net/minecraft/network/play/server/SPacketEntity$S15PacketEntityRelMove"))
    private void genAlternateRelMovePacket(List<EntityPlayer> players, CallbackInfo ci)
    {
        this.packet2 = new SPacketNewEntityRelMove(this.trackedEntity.getEntityId(),
                                                   this.trackedEntity.posX - this.posX,
                                                   this.trackedEntity.posY - this.posY,
                                                   this.trackedEntity.posZ - this.posZ,
                                                    this.trackedEntity.onGround);
    }
    
    @Inject(method = "updatePlayerList", at = @At(value = "NEW", target = "net/minecraft/network/play/server/SPacketEntity$S17PacketEntityLookMove"))
    private void genAlternateLookMovePacket(List<EntityPlayer> players, CallbackInfo ci)
    {
        this.packet2 = new SPacketNewEntityLookMove(this.trackedEntity.getEntityId(),
                                                    this.trackedEntity.posX - this.posX,
                                                    this.trackedEntity.posY - this.posY,
                                                    this.trackedEntity.posZ - this.posZ,
                                                    (byte)MathHelper.floor(this.trackedEntity.rotationYaw * 256.0F / 360.0F),
                                                    (byte)MathHelper.floor(this.trackedEntity.rotationPitch * 256.0F / 360.0F),
                                                    this.trackedEntity.onGround);
    }
    
    @Inject(slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityTrackerEntry;sendMetadata()V", ordinal = 2)),
            method = "updatePlayerList", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityTrackerEntry;encodedPosX:J", ordinal = 0))
    private void updatePrevPosition(List<EntityPlayer> players, CallbackInfo ci)
    {
        this.posX = this.trackedEntity.posX;
        this.posY = this.trackedEntity.posY;
        this.posZ = this.trackedEntity.posZ;
    }
    
    @Redirect(method = "sendPacketToTrackedPlayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetHandlerPlayServer;sendPacket(Lnet/minecraft/network/Packet;)V"))
    private void sendNewPacket(NetHandlerPlayServer connection, Packet<?> packetIn)
    {
        if (this.packet2 != null && MC4PacketHandler.registered_clients.contains(connection))
        {
            MC4PacketHandler.INSTANCE.sendTo(packet2, connection.player);
            packet2 = null;
        } else {
            connection.sendPacket(packetIn);
        }
    }
}
