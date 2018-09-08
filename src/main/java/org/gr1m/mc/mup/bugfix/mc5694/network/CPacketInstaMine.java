package org.gr1m.mc.mup.bugfix.mc5694.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.gr1m.mc.mup.bugfix.mc5694.INetHandlerPlayServer;

public class CPacketInstaMine implements IMessage
{
    private BlockPos pos;
    private EnumFacing facing;

    public CPacketInstaMine()
    {
    }

    public CPacketInstaMine(BlockPos posIn, EnumFacing facingIn)
    {
        this.pos = posIn;
        this.facing = facingIn;
    }

    @Override
    public void fromBytes(final ByteBuf buf)
    {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.facing = EnumFacing.byIndex(buf.readByte());
    }

    @Override
    public void toBytes(final ByteBuf buf)
    {
        buf.writeLong(this.pos.toLong());
        buf.writeByte(this.facing.ordinal());
    }

    public BlockPos getPos() { return this.pos; }
    public EnumFacing getFacing() { return this.facing; }

    public static class Handler implements IMessageHandler<CPacketInstaMine, IMessage>
    {
        @Override
        public IMessage onMessage(final CPacketInstaMine message, final MessageContext ctx)
        {
            NetHandlerPlayServer handler = ctx.getServerHandler();

            handler.player.getServerWorld().addScheduledTask(() ->
                    ((INetHandlerPlayServer)handler).handleInstaMine(message)
            );

            return null;
        }
    }
}
