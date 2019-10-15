package org.gr1m.mc.mup.config.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
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

    // Forge irritatingly is fine with the server sending clients packets at this stage, but drops all replies. For
    // some bizarre reason there is no method available to extend negotiation, so all a mod can do is pray like hell
    // no emergent interactions cause problems for the brief moments the player is logged in before the client can
    // inform the server of any important state.

    //@SubscribeEvent
    //public static void onServerConnectionFromClient(FMLNetworkEvent.ServerConnectionFromClientEvent event)
    
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        // Send config state to client
        SPacketMupConfig message = new SPacketMupConfig();

        for (PatchDef patch : Mup.config.getAll())
        {
            message.addConfig(patch.getFieldName(), patch.isEnabled());
            Mup.logger.debug("Adding \"" + patch.getFieldName() + "\" to server config sync packet with value: " + (patch.isEnabled() ? "true" : "false"));
        }

        ConfigPacketHandler.INSTANCE.sendTo(message, (EntityPlayerMP)(event.player));
    }

    @SubscribeEvent
    public static void onServerDisconnectionFromClient(FMLNetworkEvent.ServerDisconnectionFromClientEvent event)
    {
        registered_clients.remove(event.getHandler());
    }

    @SubscribeEvent
    public static void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        // Event runs in network thread. Schedule for execution on the Minecraft thread.
        Minecraft.getMinecraft().addScheduledTask(() -> {
            // Prevents the runtime configuration from synchronizing with the local config file.
            if (!event.isLocal()) Mup.config.lock();

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
        });
    }

    @SubscribeEvent
    public static void onClientDisconnectionFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        // Event runs in network thread. Schedule for execution on the Minecraft thread.
        Minecraft.getMinecraft().addScheduledTask(() -> {
            // Reset Mup.config PatchDef's to Configuration Properties.
            Mup.config.unlock();
            Mup.config.sync();
        });
    }

    public static void handleServerConfigReceived(SPacketMupConfig message, INetHandlerPlayClient handler)
    {
        // Attempt to apply all server supplied configuration settings, and report back any that are not recognized or
        // loaded by the client.
        
        CPacketMupConfig replyMessage = new CPacketMupConfig();
        
        for (Map.Entry<String, Boolean> configEntry : message.getConfigList().entrySet())
        {
            Mup.logger.debug("Received config sync from server for field \"" + configEntry.getKey() + "\" with value: " + (configEntry.getValue() ? "true" : "false"));

            PatchDef entry = Mup.config.get(configEntry.getKey());
            
            if (entry != null)
            {
                entry.processServerSync.accept(entry, configEntry.getValue(), handler);
                entry.setServerEnabled(configEntry.getValue());
                
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
        
        // Also send any client toggleable settings current status to the server.
        sendClientConfig();
    }

    public static void handleClientConfigReceived(CPacketMupConfig message, NetHandlerPlayServer handler)
    {
        for (Map.Entry<String, Boolean> configEntry : message.getConfigList().entrySet())
        {
            Mup.logger.debug("Received config sync from client for field \"" + configEntry.getKey() + "\" with value: " + (configEntry.getValue() ? "true" : "false"));

            PatchDef entry = Mup.config.get(configEntry.getKey());

            if (entry != null)
            {
                if (entry.processClientSync.apply(entry, configEntry.getValue(), handler)) break;
            }
        }
    }
    
    public static void sendClientConfig()
    {
        CPacketMupConfig message = new CPacketMupConfig();

        for (PatchDef patchDef : Mup.config.getAll())
        {
            if ((patchDef.getSide() == PatchDef.Side.CLIENT || patchDef.getSide() == PatchDef.Side.BOTH) && patchDef.isClientToggleable())
            {
                message.addConfig(patchDef.getFieldName(), patchDef.isEnabled() && patchDef.wasLoaded());
            }
        }
        
        ConfigPacketHandler.INSTANCE.sendToServer(message);
    }

    public static void registerMessagesAndEvents()
    {
        INSTANCE.registerMessage(SPacketMupConfig.Handler.class, SPacketMupConfig.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(CPacketMupConfig.Handler.class, CPacketMupConfig.class, 1, Side.SERVER);

        MinecraftForge.EVENT_BUS.register(ConfigPacketHandler.class);
    }
}
