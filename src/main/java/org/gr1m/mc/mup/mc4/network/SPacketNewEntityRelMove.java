package org.gr1m.mc.mup.mc4.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.gr1m.mc.mup.mc4.INetHandlerPlayClient;

public class SPacketNewEntityRelMove implements ICloneableMessage
{
    private int entityId;
    private double x, y, z;
    private boolean onGround;

    public SPacketNewEntityRelMove()
    {
        
    }
    
    public SPacketNewEntityRelMove(int entityIdIn, double xIn, double yIn, double zIn, boolean onGroundIn)
    {
        this.entityId = entityIdIn;
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
        this.onGround = onGroundIn;
    }
    
    public SPacketNewEntityRelMove cloneMessage()
    {
        return new SPacketNewEntityRelMove(this.entityId, this.x, this.y, this.z, this.onGround);
    }
    
    @Override
    public void fromBytes(final ByteBuf buf)
    {
        this.entityId = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.onGround = buf.readBoolean();
    }

    @Override
    public void toBytes(final ByteBuf buf)
    {
        buf.writeInt(this.entityId);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeBoolean(this.onGround);
    }

    public Entity getEntity(World worldIn) { return worldIn.getEntityByID(this.entityId); }
    public int getEntityId() { return this.entityId; }
    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public double getZ() { return this.z; }
    public boolean getOnGround() { return this.onGround; }

    public static class Handler implements IMessageHandler<SPacketNewEntityRelMove, IMessage>
    {
        @Override
        public IMessage onMessage(final SPacketNewEntityRelMove message, final MessageContext ctx)
        {
            INetHandlerPlayClient handler = (INetHandlerPlayClient)ctx.getClientHandler();
            
            Minecraft.getMinecraft().addScheduledTask(() ->
                    handler.handleNewEntityRelMove(message)
            );
            
            return null;
        }
    }
}
