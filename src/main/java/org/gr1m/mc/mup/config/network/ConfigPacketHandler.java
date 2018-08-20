package org.gr1m.mc.mup.config.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.config.PatchDef;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigPacketHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("mup|config");
    public static Set<INetHandler> registered_clients = new HashSet<>();

    @SubscribeEvent
    public static void onCustomPacketRegistration(FMLNetworkEvent.CustomPacketRegistrationEvent event)
    {
        if (event.getSide() == Side.SERVER)
        {
            if (event.getRegistrations().contains("mup|config"))
            {
                registered_clients.add(event.getHandler());
            }
        }
    }

    @SubscribeEvent
    public static void onServerConnectionFromClient(FMLNetworkEvent.ServerConnectionFromClientEvent event)
    {
        if (!event.isLocal())
        {
            // Send config state to client
            SPacketMupConfig message = new SPacketMupConfig();

            for (PatchDef patch : Mup.config.getAll())
            {
                if (patch.getSide() == PatchDef.Side.BOTH)
                {
                    message.addConfig(patch.getFieldName(), patch.isEnabled());
                    Mup.logger.debug("Adding \"" + patch.getFieldName() + "\" to server config sync packet with value: " + (patch.isEnabled() ? "true" : "false"));
                }
            }

            ConfigPacketHandler.INSTANCE.sendTo(message, ((NetHandlerPlayServer) event.getHandler()).player);
            Mup.logger.debug("Sending server config sync packet to client.");
        }
    }

    @SubscribeEvent
    public static void onServerDisconnectionFromClient(FMLNetworkEvent.ServerDisconnectionFromClientEvent event)
    {
        registered_clients.remove(event.getHandler());
    }

    @SubscribeEvent
    public static void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        if (!event.isLocal())
        {
            // Prevents the runtime configuration from synchronizing with the local config file.
            Mup.config.lock();

            if (!event.getConnectionType().equals("MODDED"))
            {
                // Fire client side only config modifiers
                Mup.logger.warn("Connected to non-MODDED server. Only enabling client side patches.");

                for (PatchDef patch : Mup.config.getAll())
                {
                    if (patch.getSide() != PatchDef.Side.CLIENT)
                        patch.setEnabled(false);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientDisconnectionFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        // Reset Mup.config PatchDef's to Configuration Properties.
        Mup.config.unlock();
        Mup.config.sync();
    }

    public static void handleServerConfigReceived(SPacketMupConfig message)
    {
        CPacketMupConfig replyMessage = new CPacketMupConfig();
        
        for (Map.Entry<String, Boolean> configEntry : message.getConfigList().entrySet())
        {
            Mup.logger.debug("Received config sync from server for field \"" + configEntry.getKey() + "\" with value: " + (configEntry.getValue() ? "true" : "false"));

            PatchDef entry = Mup.config.get(configEntry.getKey());
            
            if (entry != null)
            {
                entry.processServerSync.accept(entry, configEntry.getValue());
                
                if (entry.isEnabled() != configEntry.getValue())
                {
                    replyMessage.addConfig(entry.getFieldName(), entry.isEnabled());
                }
            }
            else if (configEntry.getValue())
            {
                replyMessage.addConfig(configEntry.getKey(), false);
            }
        }
        
        if (replyMessage.getConfigList().size() > 0)
        {
            ConfigPacketHandler.INSTANCE.sendToServer(replyMessage);
        }
    }

    public static void handleClientConfigReceived(CPacketMupConfig message, NetHandlerPlayServer handler)
    {
        for (Map.Entry<String, Boolean> configEntry : message.getConfigList().entrySet())
        {
            Mup.logger.debug("Received config sync from server for field \"" + configEntry.getKey() + "\" with value: " + (configEntry.getValue() ? "true" : "false"));

            PatchDef entry = Mup.config.get(configEntry.getKey());

            if (entry != null)
            {
                if (entry.processClientSync.apply(entry, configEntry.getValue(), handler)) break;
            }
        }
    }

    public static void registerMessagesAndEvents()
    {
        INSTANCE.registerMessage(SPacketMupConfig.Handler.class, SPacketMupConfig.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(CPacketMupConfig.Handler.class, CPacketMupConfig.class, 1, Side.SERVER);

        MinecraftForge.EVENT_BUS.register(ConfigPacketHandler.class);
    }
}
