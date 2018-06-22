package org.gr1m.mc.mup.mc4.network;

import net.minecraft.network.INetHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashSet;
import java.util.Set;

public class MC4PacketHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("mup|mc4");
    public static Set<INetHandler> registered_clients = new HashSet<>();
    
    @SubscribeEvent
    public static void onCustomPacketRegistration(FMLNetworkEvent.CustomPacketRegistrationEvent event)
    {
        if (event.getSide() == Side.SERVER)
        {
            if (event.getRegistrations().contains("mup|mc4"))
            {
                registered_clients.add(event.getHandler());
            }
        }
    }
    
    @SubscribeEvent
    public static void onServerDisconnectionFromClient(FMLNetworkEvent.ServerDisconnectionFromClientEvent event)
    {
        registered_clients.remove(event.getHandler());
    }

    public static void registerMessagesAndEvents()
    {
        INSTANCE.registerMessage(SPacketNewEntityRelMove.Handler.class, SPacketNewEntityRelMove.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(SPacketNewEntityLookMove.Handler.class, SPacketNewEntityLookMove.class, 1, Side.CLIENT);

        MinecraftForge.EVENT_BUS.register(MC4PacketHandler.class);
    }
}
