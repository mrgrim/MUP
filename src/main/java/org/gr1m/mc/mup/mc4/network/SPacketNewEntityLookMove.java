package org.gr1m.mc.mup.mc4.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.gr1m.mc.mup.mc4.INetHandlerPlayClient;

public class SPacketNewEntityLookMove implements ICloneableMessage
{
    private int entityId;
    private double x, y, z;
    private boolean onGround;
    private byte yaw, pitch;

    public SPacketNewEntityLookMove()
    {
        
    }

    public SPacketNewEntityLookMove(int entityIdIn, double xIn, double yIn, double zIn, byte yawIn, byte pitchIn, boolean onGroundIn)
    {
        this.entityId = entityIdIn;
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
        this.yaw = yawIn;
        this.pitch = pitchIn;
        this.onGround = onGroundIn;
    }

    public SPacketNewEntityLookMove cloneMessage()
    {
        return new SPacketNewEntityLookMove(this.entityId, this.x, this.y, this.z, this.yaw, this.pitch, this.onGround);
    }

    @Override
    public void fromBytes(final ByteBuf buf)
    {
        this.entityId = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readByte();
        this.pitch = buf.readByte();
        this.onGround = buf.readBoolean();
    }

    @Override
    public void toBytes(final ByteBuf buf)
    {
        buf.writeInt(this.entityId);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        buf.writeBoolean(this.onGround);
    }

    public Entity getEntity(World worldIn) { return worldIn.getEntityByID(this.entityId); }
    public int getEntityId() { return this.entityId; }
    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public double getZ() { return this.z; }
    public boolean getOnGround() { return this.onGround; }
    public byte getYaw() { return this.yaw; }
    public byte getPitch() { return this.pitch; }

    public static class Handler implements IMessageHandler<SPacketNewEntityLookMove, IMessage>
    {
        @Override
        public IMessage onMessage(final SPacketNewEntityLookMove message, final MessageContext ctx)
        {
            INetHandlerPlayClient handler = (INetHandlerPlayClient)ctx.getClientHandler();

            Minecraft.getMinecraft().addScheduledTask(() ->
                    handler.handleNewEntityLookMove(message)
            );

            return null;
        }
    }
}
