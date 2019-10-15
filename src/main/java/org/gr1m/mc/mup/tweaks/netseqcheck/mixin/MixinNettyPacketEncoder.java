package org.gr1m.mc.mup.tweaks.netseqcheck.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NettyPacketEncoder;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import org.gr1m.mc.mup.tweaks.netseqcheck.INetSequenceHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NettyPacketEncoder.class)
public abstract class MixinNettyPacketEncoder
{
    @Inject(method = "encode", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;writeVarInt(I)Lnet/minecraft/network/PacketBuffer;", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void addSequenceNumber(ChannelHandlerContext ctx, Packet<?> packetIn, ByteBuf buf, CallbackInfo ci, EnumConnectionState enumconnectionstate, Integer integer, PacketBuffer packetbuffer)
    {
        NetworkDispatcher dispatcher = ctx.pipeline().get(NetworkDispatcher.class);
        
        if (dispatcher != null)
        {
            INetSequenceHandler seqhandler = (INetSequenceHandler) (dispatcher.getNetHandler());

            packetbuffer.writeInt(seqhandler.getSendingSequenceNumber());
            seqhandler.incrSendingSequenceNumber();
        }
    }
}
