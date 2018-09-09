package org.gr1m.mc.mup.tweaks.profiler;

// This is _heavily_ based on the profiler in CarpetMod by gnembon and Xcom.
// Changes are to account for non-vanilla dimensions, direct player messages, and to use Forge registry names

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.gr1m.mc.mup.Mup;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MupProfiler
{
    // lol
    private static final HashMap<String, HashMap<String, Long>> timeRepo = new HashMap<>();
    
    public static int tickHealthRequested = 0;
    private static int tickHealthElapsed = 0;
    private static int testType = 0; //1 for ticks, 2 for entities;

    private static String currentDimension = null;
    private static String currentSection = null;
    private static long currentSectionStart = 0;
    private static long currentTickStart = 0;

    static void prepareGlobalReport(int ticks)
    {
        timeRepo.clear();
        testType = 1;

        timeRepo.put("entities", new HashMap<>());
        timeRepo.put("global", new HashMap<>());
        timeRepo.get("global").put("tick", 0L);
        timeRepo.get("global").put("Network", 0L);
        timeRepo.get("global").put("Autosave", 0L);

        tickHealthElapsed = ticks;
        tickHealthRequested = ticks;
        currentTickStart = 0L;
        currentSectionStart = 0L;
        currentSection = null;

    }

    static void prepareEntityReport(int ticks)
    {
        timeRepo.clear();
        testType = 2;

        timeRepo.put("entities", new HashMap<>());
        timeRepo.put("global", new HashMap<>());
        timeRepo.get("global").put("tick", 0L);
        timeRepo.get("global").put("Network", 0L);
        timeRepo.get("global").put("Autosave", 0L);
        
        tickHealthElapsed = ticks;
        tickHealthRequested = ticks;
        currentTickStart = 0L;
        currentSectionStart = 0L;
        currentSection = null;
    }

    public static void startSection(String dimension, String name)
    {
        if (tickHealthRequested == 0L || testType != 1)
        {
            return;
        }
        
        if (currentTickStart == 0L)
        {
            return;
        }
        
        if (currentSection != null)
        {
            endCurrentSection();
        }
        
        currentDimension = dimension == null ? "global" : dimension;
        currentSection = name;
        currentSectionStart = System.nanoTime();
        
        if (!timeRepo.containsKey(currentDimension)) timeRepo.put(currentDimension, new HashMap<>());
    }

    public static void startEntitySection(String dimension, Entity entity)
    {
        if (tickHealthRequested == 0L || testType != 2)
        {
            return;
        }
        
        if (currentTickStart == 0L)
        {
            return;
        }
        
        if (currentSection != null)
        {
            endCurrentSection();
        }
        
        ResourceLocation res = null;
        EntityEntry entityEntry = EntityRegistry.getEntry(entity.getClass());
        if (entityEntry != null) res = entityEntry.getRegistryName();
        
        currentDimension = dimension == null ? "global" : dimension;
        currentSection = dimension + "." + (res == null ? entity.getName() : res.toString());
        currentSectionStart = System.nanoTime();
    }

    public static void startTileEntitySection(String dimension, TileEntity tileEntity)
    {
        if (tickHealthRequested == 0L || testType != 2)
        {
            return;
        }
        
        if (currentTickStart == 0L)
        {
            return;
        }
        
        if (currentSection != null)
        {
            endCurrentSection();
        }
        
        ResourceLocation res = TileEntity.getKey(tileEntity.getClass());

        currentDimension = dimension == null ? "global" : dimension;
        currentSection = dimension + "." + (res == null ? "generic_te" : res.toString());
        currentSectionStart = System.nanoTime();
    }

    public static void endCurrentSection()
    {
        if (tickHealthRequested == 0L || testType != 1)
        {
            return;
        }
        
        if (currentTickStart == 0L)
        {
            return;
        }
        
        if (currentSection == null)
        {
            Mup.logger.error("[profiler] Finishing section that hasn't started.");
            return;
        }

        long end_time = System.nanoTime();
        timeRepo.get(currentDimension).put(currentSection, timeRepo.get(currentDimension).getOrDefault(currentSection, 0L) + (end_time - currentSectionStart));
        
        currentSection = null;
        currentDimension = null;
        currentSectionStart = 0;
    }

    public static void endCurrentEntitySection()
    {
        if (tickHealthRequested == 0L || testType != 2)
        {
            return;
        }
        
        if (currentTickStart == 0L)
        {
            return;
        }
        
        if (currentSection == null)
        {
            Mup.logger.error("[profiler] Finishing section that hasn't started.");
            return;
        }
        
        String time_section = "t." + currentSection;
        String count_section = "c." + currentSection;

        long end_time = System.nanoTime();
        timeRepo.get("entities").put(time_section, timeRepo.get("entities").getOrDefault(time_section, 0L) + (end_time - currentSectionStart));
        timeRepo.get("entities").put(count_section, timeRepo.get("entities").getOrDefault(count_section, 0L) + 1);
        
        currentSection = null;
        currentSectionStart = 0;
    }

    public static void startTickProfiling()
    {
        currentTickStart = System.nanoTime();
    }

    public static void endTickProfiling(MinecraftServer server)
    {
        if (currentTickStart == 0L)
        {
            return;
        }
        
        timeRepo.get("global").put("tick", timeRepo.get("global").get("tick") + System.nanoTime() - currentTickStart);
        tickHealthElapsed--;
        
        if (tickHealthElapsed <= 0)
        {
            finalizeTickReport(server);
        }
    }

    private static void finalizeTickReport(MinecraftServer server)
    {
        if (testType == 1)
        {
            finalizeGlobalReport(server);
        }
        
        if (testType == 2)
        {
            finalizeEntityReport(server);
        }
        
        cleanupTickReport();
    }

    private static void cleanupTickReport()
    {
        timeRepo.clear();
        testType = 0;
        tickHealthElapsed = 0;
        tickHealthRequested = 0;
        currentTickStart = 0L;
        currentSectionStart = 0L;
        currentSection = null;
        currentDimension = null;
    }
    
    private static void sendMessage(MinecraftServer server, String message)
    {
        if (server != null)
        {
            server.sendMessage(new TextComponentString(message));
            
            ITextComponent txt = new TextComponentString(message);
            txt.getStyle().setItalic(true).setColor(TextFormatting.GRAY);
            
            if (ProfilerCommand.CALLER != null) ProfilerCommand.CALLER.sendMessage(txt);
        }
    }

    private static void finalizeGlobalReport(MinecraftServer server)
    {
        //print stats
        long total_tick_time = timeRepo.get("global").get("tick");
        double divider = 1.0D / tickHealthRequested / 1000000;
        long accumulated = 0L;
        
        sendMessage(server, String.format("Average tick time: %.3fms", divider * total_tick_time));

        accumulated += timeRepo.get("global").get("Autosave");
        sendMessage(server, String.format("Autosave: %.3fms", divider * timeRepo.get("global").get("Autosave")));

        accumulated += timeRepo.get("global").get("Network");
        sendMessage(server, String.format("Network: %.3fms", divider * timeRepo.get("global").get("Network")));

        for (String dimension : timeRepo.keySet())
        {
            if (!"global".equals(dimension) && !"entities".equals(dimension))
            {
                sendMessage(server, dimension + ":");
                
                for (String category : timeRepo.get(dimension).keySet())
                {
                    accumulated += timeRepo.get(dimension).get(category);
                    sendMessage(server, String.format(" - " + category + ": %.3fms", divider * timeRepo.get(dimension).get(category)));
                }
            }
        }

        long rest = total_tick_time-accumulated;

        sendMessage(server, String.format("Rest: %.3fms", divider * rest));
    }

    private static void finalizeEntityReport(MinecraftServer server)
    {
        long total_tick_time = timeRepo.get("global").get("tick");
        double divider = 1.0D / tickHealthRequested / 1000000;

        timeRepo.remove("global");

        sendMessage(server, String.format("Average tick time: %.3fms", divider * total_tick_time));
        sendMessage(server, "Top 10 counts:");
        int total = 0;
        
        for ( Map.Entry<String, Long> entry : timeRepo.get("entities").entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toList()) )
        {
            if (entry.getKey().startsWith("t."))
            {
                continue;
            }
            
            total++;
            
            if (total > 10)
            {
                continue;
            }

            String[] parts = entry.getKey().split("\\.", 3);
            String dim = parts[1];
            String name = (parts[2].equals("")) ? "unknown" : parts[2];
            
            sendMessage(server, String.format(" - %s in %s: %.3f", name, dim, 1.0D * entry.getValue() / tickHealthRequested));
        }
        
        sendMessage(server, "Top 10 grossing:");
        total = 0;
        
        for ( Map.Entry<String, Long> entry : timeRepo.get("entities").entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toList()) )
        {
            if (entry.getKey().startsWith("c."))
            {
                continue;
            }
            
            total++;
            
            if (total > 10)
            {
                continue;
            }
            
            String[] parts = entry.getKey().split("\\.", 3);
            String dim = parts[1];
            String name = (parts[2].equals("")) ? "unknown" : parts[2];
            
            sendMessage(server, String.format(" - %s in %s: %.3fms", name, dim, divider * entry.getValue()));
        }
    }
}
