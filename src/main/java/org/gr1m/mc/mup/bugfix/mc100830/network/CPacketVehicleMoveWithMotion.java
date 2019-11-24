package org.gr1m.mc.mup.bugfix.mc100830.network;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.gr1m.mc.mup.bugfix.mc100830.IEntity;
import org.gr1m.mc.mup.bugfix.mc100830.INetHandlerPlayServer;

public class CPacketVehicleMoveWithMotion implements IMessage
{
    public double posX;
    public double posY;
    public double posZ;

    public float yaw;
    public float pitch;

    public double motionX;
    public double motionY;
    public double motionZ;
    
    public CPacketVehicleMoveWithMotion()
    {
        
    }
    
    public CPacketVehicleMoveWithMotion(Entity entityIn)
    {
        this.posX = entityIn.posX;
        this.posY = entityIn.posY;
        this.posZ = entityIn.posZ;
        
        this.yaw = entityIn.rotationYaw;
        this.pitch = entityIn.rotationPitch;
        
        this.motionX = ((IEntity)entityIn).getMoveDeltaX();
        this.motionY = ((IEntity)entityIn).getMoveDeltaY();
        this.motionZ = ((IEntity)entityIn).getMoveDeltaZ();
    }

    @Override
    public void fromBytes(final ByteBuf buf)
    {
        this.posX = buf.readDouble();
        this.posY = buf.readDouble();
        this.posZ = buf.readDouble();
        
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();

        this.motionX = buf.readDouble();
        this.motionY = buf.readDouble();
        this.motionZ = buf.readDouble();
    }
    
    @Override
    public void toBytes(final ByteBuf buf)
    {
        buf.writeDouble(this.posX);
        buf.writeDouble(this.posY);
        buf.writeDouble(this.posZ);
        
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        
        buf.writeDouble(this.motionX);
        buf.writeDouble(this.motionY);
        buf.writeDouble(this.motionZ);
    }

    public static class Handler implements IMessageHandler<CPacketVehicleMoveWithMotion, IMessage>
    {
        @Override
        public IMessage onMessage(final CPacketVehicleMoveWithMotion message, final MessageContext ctx)
        {
            NetHandlerPlayServer handler = ctx.getServerHandler();

            ReferenceCountUtil.retain(message);

            handler.player.getServerWorld().addScheduledTask(() ->
                                                                 ((INetHandlerPlayServer)handler).processVehicleMoveWithMotion(message)
                                                            );

            return null;
        }
    }
}
