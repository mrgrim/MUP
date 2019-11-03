package org.gr1m.mc.mup.bugfix.mc4.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.shared.ICloneableMessage;
import org.gr1m.mc.mup.bugfix.mc4.network.MC4PacketHandler;
import org.gr1m.mc.mup.bugfix.mc4.network.SPacketNewEntityLookMove;
import org.gr1m.mc.mup.bugfix.mc4.network.SPacketNewEntityRelMove;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(EntityTrackerEntry.class)
public abstract class MixinEntityTrackerEntry {
    @Shadow
    @Final
    private Entity trackedEntity;

    @Shadow
    @Final
    public Set<EntityPlayerMP> trackingPlayers;
    
    private double posX;
    private double posY;
    private double posZ;
    
    private ICloneableMessage packet2;
    
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
                                                   this.trackedEntity.posX,
                                                   this.trackedEntity.posY,
                                                   this.trackedEntity.posZ,
                                                   this.trackedEntity.onGround);
    }
    
    @Inject(method = "updatePlayerList", at = @At(value = "NEW", target = "net/minecraft/network/play/server/SPacketEntity$S17PacketEntityLookMove"))
    private void genAlternateLookMovePacket(List<EntityPlayer> players, CallbackInfo ci)
    {
        this.packet2 = new SPacketNewEntityLookMove(this.trackedEntity.getEntityId(),
                                                    this.trackedEntity.posX,
                                                    this.trackedEntity.posY,
                                                    this.trackedEntity.posZ,
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
    
    @Redirect(slice = @Slice(from = @At(value = "NEW", target = "net/minecraft/network/play/server/SPacketEntityVelocity", ordinal = 0),
                             to   = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityTrackerEntry;sendMetadata()V", ordinal = 2)),
              method = "updatePlayerList", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityTrackerEntry;sendPacketToTrackedPlayers(Lnet/minecraft/network/Packet;)V", ordinal = 1))
    private void sendMC4PacketConditional(EntityTrackerEntry self, Packet<?> packetIn)
    {
        IMessage mc4Packet = null;
        
        if (this.packet2 != null)
        {
            mc4Packet = this.packet2.cloneMessage();
            this.packet2 = null;
        }
        
        for (EntityPlayerMP entityplayermp : this.trackingPlayers)
        {
            if (mc4Packet != null && MC4PacketHandler.registered_clients.contains(entityplayermp.connection) && Mup.config.mc4.enabled)
            {
                MC4PacketHandler.INSTANCE.sendTo(mc4Packet, entityplayermp.connection.player);
            } else {
                entityplayermp.connection.sendPacket(packetIn);
            }
        }
    }
}
