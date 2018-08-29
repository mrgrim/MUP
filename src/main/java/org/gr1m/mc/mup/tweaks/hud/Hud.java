package org.gr1m.mc.mup.tweaks.hud;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.gr1m.mc.mup.Mup;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Hud
{
    public static Set<INetHandler> registered_clients = new HashSet<>();
    
    public static void Init()
    {
        MinecraftForge.EVENT_BUS.register(Hud.class);
    }

    @SubscribeEvent
    public static void onServerDisconnectionFromClient(FMLNetworkEvent.ServerDisconnectionFromClientEvent event)
    {
        registered_clients.remove(event.getHandler());
    }
    
    public static void clearHudForPlayer(INetHandler player)
    {
        SPacketPlayerListHeaderFooter packet = new SPacketPlayerListHeaderFooter();

        ((ISPacketPlayerListHeaderFooter) (packet)).setHeader(new TextComponentString(""));
        ((ISPacketPlayerListHeaderFooter) (packet)).setFooter(new TextComponentString(""));

        ((NetHandlerPlayServer) (player)).sendPacket(packet);
    }

    @SubscribeEvent
    public static void serverTickEvent(TickEvent.ServerTickEvent event)
    {
        if (Mup.config.hud.enabled && event.side == Side.SERVER && event.phase == TickEvent.Phase.START)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            
            if (server.getTickCounter() % 20 == 0)
            {
                double MSPT = MathHelper.average(server.tickTimeArray) * 1.0E-6D;
                double TPS = 1000.0D / Math.max(50, MSPT);

                TextFormatting color = heatmap_color(MSPT, 50);
                TextComponentString tps_display = new TextComponentString(TextFormatting.GRAY + " TPS: " + color + String.format(Locale.US, "%.1f", TPS) +
                                                                          TextFormatting.GRAY + "  MSPT: " + color + String.format(Locale.US, "%.1f", MSPT));

                SPacketPlayerListHeaderFooter packet = new SPacketPlayerListHeaderFooter();

                ((ISPacketPlayerListHeaderFooter) (packet)).setHeader(new TextComponentString(""));
                ((ISPacketPlayerListHeaderFooter) (packet)).setFooter(tps_display);

                for (INetHandler handler : registered_clients)
                {
                    ((NetHandlerPlayServer) (handler)).sendPacket(packet);
                }
            }
        }
    }

    public static TextFormatting heatmap_color(double actual, double reference)
    {
        TextFormatting color = TextFormatting.DARK_GREEN;
        if (actual > 0.5D*reference) color = TextFormatting.YELLOW;
        if (actual > 0.8D*reference) color = TextFormatting.RED;
        if (actual > reference) color = TextFormatting.LIGHT_PURPLE;
        return color;
    }
}
