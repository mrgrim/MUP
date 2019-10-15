package org.gr1m.mc.mup.tweaks.netseqcheck.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NettyPacketDecoder;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.netseqcheck.INetSequenceHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(NettyPacketDecoder.class)
public class MixinNettyPacketDecoder
{
    @Inject(method = "decode", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;readVarInt()I", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void checkSequenceNumber(ChannelHandlerContext ctx, ByteBuf buf, List<Object> packetsOut, CallbackInfo ci, PacketBuffer packetbuffer)
    {
        NetworkDispatcher dispatcher = ctx.pipeline().get(NetworkDispatcher.class);
        
        if (dispatcher != null)
        {
            INetSequenceHandler seqhandler = (INetSequenceHandler) (dispatcher.getNetHandler());

            int expectedSequence = seqhandler.getCheckingSequenceNumber();
            int recievedSequence = packetbuffer.readInt();

            if (recievedSequence > expectedSequence)
            {
                Mup.logger.error("Received packet with sequences number " + recievedSequence + " via " + seqhandler.toString() + " and expecting " + expectedSequence);
                Mup.logger.error("Missing sequence in receiving packet stream. Possible Dropped packet!");
            }
            else if (recievedSequence < expectedSequence)
            {
                Mup.logger.error("Received packet with sequences number " + recievedSequence + " via " + seqhandler.toString() + " and expecting " + expectedSequence);
                Mup.logger.error("Old sequence in packet stream. Possible out of order packet received!");
            }

            seqhandler.setCheckingSequenceNumber(recievedSequence + 1);
        }
    }
}
