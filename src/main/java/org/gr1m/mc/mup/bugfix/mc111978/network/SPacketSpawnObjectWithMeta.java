package org.gr1m.mc.mup.bugfix.mc111978.network;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc111978.INetHandlerPlayClient;

import static io.netty.buffer.Unpooled.buffer;

public class SPacketSpawnObjectWithMeta implements IMessage
{
    private SPacketSpawnObject object;
    private SPacketEntityMetadata metadata;

    public SPacketSpawnObjectWithMeta()
    {
        this.object = new SPacketSpawnObject();
        this.metadata = new SPacketEntityMetadata();
    }

    public SPacketSpawnObjectWithMeta(SPacketSpawnObject objectIn, SPacketEntityMetadata metadataIn)
    {
        this.object = objectIn;
        this.metadata = metadataIn;
    }

    public SPacketSpawnObjectWithMeta cloneMessage()
    {
        return new SPacketSpawnObjectWithMeta(this.object, this.metadata);
    }

    @Override
    public void fromBytes(final ByteBuf buf)
    {
        int objectSize = buf.readInt();
        int metadataSize = buf.readInt();
        
        try
        {
            this.object.readPacketData(new PacketBuffer(buf.readBytes(objectSize)));
            this.metadata.readPacketData(new PacketBuffer(buf.readBytes(metadataSize)));
        }
        catch (Exception e)
        {
            // Eh..
            Mup.logger.error("SPacketSpawnObjectWithMeta failed in fromBytes.");
        }
    }

    @Override
    public void toBytes(final ByteBuf buf)
    {
        PacketBuffer objectPacket = new PacketBuffer(buffer());
        PacketBuffer metadataPacket = new PacketBuffer(buffer());

        try
        {
            this.object.writePacketData(objectPacket);
            this.metadata.writePacketData(metadataPacket);
        }
        catch (Exception e)
        {
            // Eh..
            Mup.logger.error("SPacketSpawnObjectWithMeta failed in toBytes.");
        }
        
        buf.writeInt(objectPacket.readableBytes());
        buf.writeInt(metadataPacket.readableBytes());
        
        buf.writeBytes(objectPacket);
        buf.writeBytes(metadataPacket);
        
        objectPacket.release();
        metadataPacket.release();
    }

    public SPacketSpawnObject getObjectPacket() { return this.object; }
    public SPacketEntityMetadata getMetadataPacket() { return this.metadata; }
    
    public static class Handler implements IMessageHandler<SPacketSpawnObjectWithMeta, IMessage>
    {
        @Override
        public IMessage onMessage(final SPacketSpawnObjectWithMeta message, final MessageContext ctx)
        {
            INetHandlerPlayClient handler = (INetHandlerPlayClient)ctx.getClientHandler();

            ReferenceCountUtil.retain(message);
            
            Minecraft.getMinecraft().addScheduledTask(() ->
                                                          handler.handleSpawnObjectWithMeta(message)
                                                     );

            return null;
        }
    }
}
