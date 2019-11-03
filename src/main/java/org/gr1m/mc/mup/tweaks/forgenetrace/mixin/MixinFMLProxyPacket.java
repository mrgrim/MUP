package org.gr1m.mc.mup.tweaks.forgenetrace.mixin;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.INetHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.FMLNetworkException;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FMLProxyPacket.class)
public class MixinFMLProxyPacket
{
    @Shadow(remap = false)
    private INetHandler netHandler;

    @Shadow(remap = false)
    @Final
    String channel;

    @Shadow(remap = false)
    private Side target;

    @Shadow(remap = false)
    private static Multiset<String> badPackets;

    @Shadow(remap = false)
    private static int packetCountWarning;

    @Shadow(remap = false)
    private NetworkDispatcher dispatcher;

    // We need to synchronize a large chunk of this method so a soft override seems the only real way. FML 1.12.2 is
    // basically EoL anyway so there's no reason to expect major changes here.
    @Inject(method = "processPacket", at = @At("HEAD"), cancellable = true, remap = true)
    private void processPacketsynchronized(INetHandler inethandler, CallbackInfo ci)
    {
        if (Mup.config.forgenetrace.enabled)
        {
            ci.cancel();
            
            this.netHandler = inethandler;
            EmbeddedChannel internalChannel = NetworkRegistry.INSTANCE.getChannel(this.channel, this.target);
            if (internalChannel != null)
            {
                synchronized (internalChannel)
                {
                    internalChannel.attr(NetworkRegistry.NET_HANDLER).set(this.netHandler);
                    try
                    {
                        if (internalChannel.writeInbound(this))
                        {
                            badPackets.add(this.channel);
                            if (badPackets.size() % packetCountWarning == 0)
                            {
                                FMLLog.log.fatal("Detected ongoing potential memory leak. {} packets have leaked. Top offenders", badPackets.size());
                                int i = 0;
                                for (Multiset.Entry<String> s : Multisets.copyHighestCountFirst(badPackets).entrySet())
                                {
                                    if (i++ > 10) break;
                                    FMLLog.log.fatal("\t {} : {}", s.getElement(), s.getCount());
                                }
                            }
                        }
                        internalChannel.inboundMessages().clear();
                    }
                    catch (FMLNetworkException ne)
                    {
                        FMLLog.log.error("There was a network exception handling a packet on channel {}", channel, ne);
                        dispatcher.rejectHandshake(ne.getMessage());
                    }
                    catch (Throwable t)
                    {
                        FMLLog.log.error("There was a critical exception handling a packet on channel {}", channel, t);
                        dispatcher.rejectHandshake("A fatal error has occurred, this connection is terminated");
                    }
                }
            }
        }
    }
}
