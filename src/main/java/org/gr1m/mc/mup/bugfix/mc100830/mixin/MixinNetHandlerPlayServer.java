package org.gr1m.mc.mup.bugfix.mc100830.mixin;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc100830.INetHandlerPlayServer;
import org.gr1m.mc.mup.bugfix.mc100830.network.CPacketVehicleMoveWithMotion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer implements INetHandlerPlayServer
{
    @Shadow
    public EntityPlayerMP player;

    @Shadow
    public abstract void disconnect(ITextComponent textComponent);

    @Shadow
    private Entity lowestRiddenEnt;

    @Shadow
    private double lowestRiddenX;

    @Shadow
    private double lowestRiddenY;

    @Shadow
    private double lowestRiddenZ;

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    @Final
    public NetworkManager netManager;

    @Shadow
    private double lowestRiddenX1;

    @Shadow
    private double lowestRiddenY1;

    @Shadow
    private double lowestRiddenZ1;

    @Shadow
    private boolean vehicleFloating;

    public void processVehicleMoveWithMotion(CPacketVehicleMoveWithMotion packetIn)
    {
        if (!Doubles.isFinite(packetIn.posX) || !Doubles.isFinite(packetIn.posY) || !Doubles.isFinite(packetIn.posZ) ||
            !Floats.isFinite(packetIn.pitch) || !Floats.isFinite(packetIn.yaw) ||
            !Doubles.isFinite(packetIn.motionX) || !Doubles.isFinite(packetIn.motionY) || !Doubles.isFinite(packetIn.motionZ))
        {
            this.disconnect(new TextComponentTranslation("multiplayer.disconnect.invalid_vehicle_movement", new Object[0]));
        }
        else
        {
            Entity riddenEntity = this.player.getLowestRidingEntity();

            if (riddenEntity != this.player && riddenEntity.getControllingPassenger() == this.player && riddenEntity == this.lowestRiddenEnt)
            {
                WorldServer worldserver = this.player.getServerWorld();
                
                double entityPosX = riddenEntity.posX;
                double entityPosY = riddenEntity.posY;
                double entityPosZ = riddenEntity.posZ;
                
                double packetPosX = packetIn.posX;
                double packetPosY = packetIn.posY;
                double packetPosZ = packetIn.posZ;
                
                float packetYaw = packetIn.yaw;
                float packetPitch = packetIn.pitch;
                
                double posXDelta = packetPosX - this.lowestRiddenX;
                double posYDelta = packetPosY - this.lowestRiddenY;
                double posZDelta = packetPosZ - this.lowestRiddenZ;
                
                double distMoveSq = posXDelta * posXDelta + posYDelta * posYDelta + posZDelta * posZDelta;

                if (distMoveSq > 100.0D && (!this.server.isSinglePlayer() || !this.server.getServerOwner().equals(riddenEntity.getName())))
                {
                    Mup.logger.warn("{} (vehicle of {}) moved too quickly! {},{},{}", riddenEntity.getName(), this.player.getName(), posXDelta, posYDelta, posZDelta);
                    
                    if (!Mup.config.dac.enabled)
                    {
                        this.netManager.sendPacket(new SPacketMoveVehicle(riddenEntity));
                    }
                    
                    return;
                }

                boolean isNotCollidingBefore = worldserver.getCollisionBoxes(riddenEntity, riddenEntity.getEntityBoundingBox()).isEmpty();
                
                double preEntityPosX = riddenEntity.posX;
                double preEntityPosY = riddenEntity.posY;
                double preEntityPosZ = riddenEntity.posZ;
                
                riddenEntity.move(MoverType.PLAYER, packetIn.motionX, packetIn.motionY, packetIn.motionZ);
                
                double trueYDelta = packetIn.motionY;
                
                posXDelta = packetPosX - riddenEntity.posX;
                posYDelta = packetPosY - riddenEntity.posY;
                posZDelta = packetPosZ - riddenEntity.posZ;

                if (posYDelta > -0.5D || posYDelta < 0.5D)
                {
                    posYDelta = 0.0D;
                }

                distMoveSq = posXDelta * posXDelta + posYDelta * posYDelta + posZDelta * posZDelta;
                
                boolean movedWrongly = false;
                if (distMoveSq > 0.0625D)
                {
                    movedWrongly = true;
                    Mup.logger.warn("{} moved wrongly!", riddenEntity.getName());

                    Mup.logger.debug("Server Pos Before: {}, {}, {}", preEntityPosX, preEntityPosY, preEntityPosZ);
                    Mup.logger.debug("Packet Motion: {}, {}, {}", packetIn.motionX, packetIn.motionY, packetIn.motionZ);
                    Mup.logger.debug("Packet Pos: {}, {}, {}", packetIn.posX, packetIn.posY, packetIn.posZ);
                    Mup.logger.debug("Server Pos After: {}, {}, {}", riddenEntity.posX, riddenEntity.posY, riddenEntity.posZ);
                }

                riddenEntity.setPositionAndRotation(packetPosX, packetPosY, packetPosZ, packetYaw, packetPitch);
                this.player.setPositionAndRotation(packetPosX, packetPosY, packetPosZ, this.player.rotationYaw, this.player.rotationPitch); // Forge - Resync player position on vehicle moving
                
                boolean isNotCollidingAfter = worldserver.getCollisionBoxes(riddenEntity, riddenEntity.getEntityBoundingBox()).isEmpty();

                if (isNotCollidingBefore && (movedWrongly || !isNotCollidingAfter))
                {
                    riddenEntity.setPositionAndRotation(entityPosX, entityPosY, entityPosZ, packetYaw, packetPitch);
                    this.player.setPositionAndRotation(entityPosX, entityPosY, entityPosZ, this.player.rotationYaw, this.player.rotationPitch); // Forge - Resync player position on vehicle moving
                    this.netManager.sendPacket(new SPacketMoveVehicle(riddenEntity));
                    return;
                }

                this.server.getPlayerList().serverUpdateMovingPlayer(this.player);
                this.player.addMovementStat(this.player.posX - entityPosX, this.player.posY - entityPosY, this.player.posZ - entityPosZ);
                
                this.vehicleFloating = trueYDelta >= -0.03125D && !this.server.isFlightAllowed() && !worldserver.checkBlockCollision(riddenEntity.getEntityBoundingBox().grow(0.0625D).expand(0.0D, -0.55D, 0.0D));
                
                this.lowestRiddenX1 = riddenEntity.posX;
                this.lowestRiddenY1 = riddenEntity.posY;
                this.lowestRiddenZ1 = riddenEntity.posZ;
            }
        }
    }
}
