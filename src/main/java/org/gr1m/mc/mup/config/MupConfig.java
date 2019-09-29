package org.gr1m.mc.mup.config;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc4.network.MC4PacketHandler;
import org.gr1m.mc.mup.config.network.ConfigPacketHandler;
import org.gr1m.mc.mup.tweaks.ete.config.EteCustomConfig;
import org.gr1m.mc.mup.tweaks.hud.Hud;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Mup.MODID)
public class MupConfig
{
    public static Configuration config;

    private boolean serverLocked;

    public final PatchDef mc4 = new PatchDef("mc4", PatchDef.Side.BOTH, PatchDef.ServerSyncHandlers.TOGGLE, (bug, enabled, handler) -> {
        if (enabled)
        {
            MC4PacketHandler.registered_clients.add(handler);
        }
        else
        {
            MC4PacketHandler.registered_clients.remove(handler);
        }
        
        return false;
    })
        .setDisplayName("MC-4")
        .setCredits("theosib, MrGrim")
        .setSideEffects("Increases network traffic")
        .setCategory("bug fixes")
        .setComment(new String[] {"Item drops sometimes appear at the wrong location"});

    // First correct diagnosis of state loss on save with corrected code here: https://bugs.mojang.com/browse/MC-2025?focusedCommentId=74617&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-74617
    public final PatchDef mc2025 = new PatchDef("mc2025", PatchDef.Side.SERVER)
        .setDisplayName("MC-2025")
        .setCredits("WolfieMario")
        .setCategory("bug fixes")
        .setComment(new String[] {"Mobs going out of fenced areas/suffocate in blocks when loading chunks"});

    public final PatchDef mc5694 = new PatchDef("mc5694", PatchDef.Side.BOTH, PatchDef.ServerSyncHandlers.TOGGLE)
        .setDisplayName("MC-5694")
        .setCredits("Pokechu22, theosib, gnembon, Xcom, MrGrim")
        .setCategory("bug fixes")
        .setComment(new String[] {"High efficiency tools / fast mining destroys some blocks client-side only"});

    public final PatchDef mc9568 = new PatchDef("mc9568", PatchDef.Side.SERVER)
        .setDisplayName("MC-9568")
        .setCredits("Xcom, MrGrim")
        .setCategory("bug fixes")
        .setComment(new String[] {"Mobs suffocate / go through blocks when growing up near a solid block"});

    public final PatchDef mc14826 = new PatchDef("mc14826", PatchDef.Side.SERVER, PatchDef.ServerSyncHandlers.IGNORE, PatchDef.ClientSyncHandlers.IGNORE)
        .setDisplayName("MC-14826")
        .setCredits("Xcom, Kevin Gagnon")
        .setCategory("bug fixes")
        .setComment(new String[] {"Leads in unloaded chunks break, become invisible or connect to an invisible target far away"});

    public final PatchDef mc54026 = new PatchDef("mc54026", PatchDef.Side.BOTH, PatchDef.ServerSyncHandlers.ACCEPT)
        .setDisplayName("MC-54026")
        .setCredits("gnembon, Xcom, MrGrim")
        .setCategory("bug fixes")
        .setClientToggleable(true)
        .setComment(new String[] {"Blocks attached to slime blocks can create ghost blocks"});

    public final PatchDef mc73051 = new PatchDef("mc73051", PatchDef.Side.SERVER)
        .setDisplayName("MC-73051")
        .setCredits("Xcom")
        .setCategory("bug fixes")
        .setComment(new String[] {"Witch Hut structure data do not account for height the witch hut is generated at"});

    public final PatchDef mc80032 = new PatchDef("mc80032", PatchDef.Side.SERVER)
        .setDisplayName("MC-80032")
        .setCredits("Xcom")
        .setCategory("bug fixes")
        .setComment(new String[] {"Mobs suffocate when going through nether portals."});

    public final PatchDef mc92916 = new PatchDef("mc92916", PatchDef.Side.SERVER, PatchDef.ServerSyncHandlers.IGNORE, PatchDef.ClientSyncHandlers.IGNORE)
        .setDisplayName("MC-92916")
        .setCredits("Xcom, MrGrim")
        .setCategory("bug fixes")
        .setComment(new String[] {"Player is removed from the EntityTracker when teleporting to unloaded chunks or changing dimensions,",
                                  "resulting in client side desync"});

    public final PatchDef mc98153 = new PatchDef("mc98153", PatchDef.Side.SERVER)
        .setDisplayName("MC-98153")
        .setCredits("Xcom")
        .setCategory("bug fixes")
        .setComment(new String[] {"Portals generate far-away chunks & set player on fire."});

    public final PatchDef mc108469 = new PatchDef("mc108469", PatchDef.Side.SERVER)
        .setDisplayName("MC-108469")
        .setCredits("Xcom")
        .setSideEffects("May slightly increase RAM use.")
        .setCategory("bug fixes")
        .setComment(new String[] {"Chunk-wise entity lists often don't get updated correctly (Entities disappear)"});

    public final PatchDef mc111444 = new PatchDef("mc111444", PatchDef.Side.CLIENT, PatchDef.ServerSyncHandlers.IGNORE, PatchDef.ClientSyncHandlers.IGNORE)
        .setDisplayName("MC-111444")
        .setCredits("Earthcomputer, nessie, masa")
        .setCategory("bug fixes")
        .setComment(new String[] {"Elytras can't open in laggy game."});

    public final PatchDef mc118710 = new PatchDef("mc118710", PatchDef.Side.BOTH, PatchDef.ServerSyncHandlers.TOGGLE)
        .setDisplayName("MC-118710")
        .setCredits("theosib, MrGrim")
        .setSideEffects("Increases network traffic")
        .setCategory("bug fixes")
        .setComment(new String[] {TextFormatting.RED + "[Experimental]" + TextFormatting.YELLOW + " Blocks take multiple attempts to mine"});

    public final PatchDef mc119971 = new PatchDef("mc119971", PatchDef.Side.SERVER, PatchDef.ServerSyncHandlers.IGNORE)
        .setDisplayName("MC-119971")
        .setCredits("theosib")
        .setCategory("bug fixes")
        .setToggleable(false)
        .setComment(new String[] {"Various duplications, deletions, and data corruption at chunk boundaries, caused",
                                  "by loading outdated chunks — includes duping and deletion of entities/mobs,",
                                  "items in hoppers, and blocks moved by pistons, among other problems"});

    public final PatchDef mc123320 = new PatchDef("mc123320", PatchDef.Side.SERVER)
        .setDisplayName("MC-123320")
        .setCredits("nessie, MrGrim")
        .setSideEffects("If the MC-4 patch is disabled this will increase network use when large quantities of in motion item entities are present.")
        .setCategory("bug fixes")
        .setComment(new String[] {"Items do not move through blocks smoothly"});

    // Implemented hash code caching because these objects do not appear to be singletons as Grum suggested.
    public final PatchDef mc134989 = new PatchDef("mc134989", PatchDef.Side.BOTH, PatchDef.ServerSyncHandlers.TOGGLE)
        .setDisplayName("MC-134989")
        .setCredits("theosib")
        .setCategory("bug fixes")
        .setComment(new String[] {"AbstractMap::hashCode accounts for substantial CPU overhead (from profiling)"});

    public final PatchDef newlight = new PatchDef("newlight", PatchDef.Side.BOTH, PatchDef.ServerSyncHandlers.IGNORE)
        .setDisplayName("Newlight")
        .setCredits("PhiPro, Mathe172, nessie, MrGrim")
        .setCategory("optimizations")
        .setToggleable(false)
        .setComment(new String[] {"This is a complete drop in replacement for the vanilla Block and Sky lighting engine.",
                                  "It provides considerable performance improvements to light updates and fixes many",
                                  "vanilla lighting bugs such as MC-3329, MC-3961, MC-9188, MC-11571, MC-80966,",
                                  "MC-91136, MC-93132, MC-102162, and likely others."});

    public final PatchDef rsturbo = new PatchDef("rsturbo", PatchDef.Side.SERVER)
        .setDisplayName("RS Turbo")
        .setCredits("theosib")
        .setCategory("optimizations")
        .setDefaults(new boolean[] { true, false })
        .setSideEffects("Does not have 100% vanilla behavior, but is very close.")
        .setComment(new String[] {"This is a rewrite of redstone wire developed by theosib with the aim of increasing",
                                  "performance while maintaining compatibility with vanilla as much as possible. It has",
                                  "been shown to increase performance by as much as 10x and removes directional or",
                                  "locational requirements for many things. It fixes MC-81098 and MC-11193."});

    public final PatchDef hud = new PatchDef("hud", PatchDef.Side.BOTH, PatchDef.ServerSyncHandlers.TOGGLE, (bug, enabled, handler) -> {
        if (enabled)
        {
            Hud.registered_clients.add(handler);
        }
        else
        {
            Hud.registered_clients.remove(handler);
            Hud.clearHudForPlayer(handler);
        }
        
        return false;
    })
        .setDisplayName("HUD")
        .setCredits("nessie [CarpetMod]")
        .setSideEffects("Increases network traffic")
        .setCategory("tweaks")
        .setDefaults(new boolean[] { true, false })
        .setComment(new String[] {"Enables server MSPT/TPS display in player list overlay and enables the overlay in",
                                  "single player. Shows 5 second average and updates once per second."});
    
    public final PatchDef profiler = new PatchDef("profiler", PatchDef.Side.SERVER, PatchDef.ServerSyncHandlers.IGNORE, PatchDef.ClientSyncHandlers.IGNORE)
        .setDisplayName("Profiler")
        .setCredits("gnembon, Xcom [CarpetMod]")
        .setCategory("tweaks")
        .setToggleable(false)
        .setDefaults(new boolean[] { false, false })
        .setSideEffects("This is a fairly invasive patch. Recommend only loading temporarily when required.")
        .setComment(new String[] {"Enables server side profiler features available under the /tickhealth command:",
                                  "    /tickhealth <basic|entities> [tick count]"});

    public final PatchDef ete = new PatchDef("ete", PatchDef.Side.SERVER, PatchDef.ServerSyncHandlers.IGNORE, PatchDef.ClientSyncHandlers.IGNORE, new EteCustomConfig())
        .setDisplayName("Entity Tracking Editor")
        .setCredits("MrGrim")
        .setCategory("tweaks")
        .setToggleable(true)
        .setDefaults(new boolean[] { false, false })
        .setSideEffects("Modifying these values can cause entity \"pop in\", increase server bandwidth use, or harm performance.")
        .setComment(new String[] {"Edit the maximum range at which the server will send entity data to the client and how often the server updates the client.",
                                  "Setting the tracking distance larger than the view distance will cause it to be set to the current view distance."});

    public final PatchDef dac = new PatchDef("dac", PatchDef.Side.SERVER, PatchDef.ServerSyncHandlers.IGNORE, PatchDef.ClientSyncHandlers.IGNORE)
        .setDisplayName("Disable Movement Anti Cheat")
        .setCredits("MrGrim")
        .setCategory("tweaks")
        .setToggleable(true)
        .setDefaults(new boolean[] { false, false })
        .setSideEffects("This disables movement based anti cheat functionality. Only use on private servers with trusted players!")
        .setComment(new String[] {"Prevents the server from resetting the position of (rubber banding) clients that move \"too quickly\"."});

    public void init(File file)
    {
        if (config == null)
        {
            config = new Configuration(file);
            this.load();
        }
    }

    public void load()
    {
        config.load();
        this.sync();
        config.save();
    }

    public void sync()
    {
        for (Field field : this.getClass().getFields())
        {
            Object fieldObj;

            try
            {
                fieldObj = field.get(this);
            }
            catch (Exception e)
            {
                Mup.logger.error("Unknown field access reading configuration file.");
                continue;
            }

            if (fieldObj.getClass() == PatchDef.class)
            {
                boolean[] bugState;
                PatchDef patchDef = (PatchDef) fieldObj;

                if (!this.isServerLocked() || patchDef.isClientToggleable())
                {
                    bugState = config.get(patchDef.getCategory(), field.getName(), patchDef.getDefaults(), String.join("\n", patchDef.getComment()), true, 2).getBooleanList();

                    if (bugState[0]) patchDef.setLoaded();
                    patchDef.setEnabled(bugState[1]);
                    
                    if (patchDef.customConfig != null) patchDef.customConfig.loadConfig(config, patchDef.getCategory() + "." + patchDef.getFieldName());
                }
            }
        }
    }

    public void lock()
    {
        this.serverLocked = true;
    }

    public void unlock()
    {
        this.serverLocked = false;
    }
    
    public boolean isServerLocked()
    {
        return this.serverLocked;
    }

    @Nullable
    public PatchDef get(String propName)
    {
        try
        {
            return (PatchDef) (this.getClass().getField(propName).get(this));
        }
        catch (Exception e)
        {
            Mup.logger.error("Unknown field access fetching property.");
            return null;
        }
    }

    public List<PatchDef> getAll()
    {
        List<PatchDef> bugs = new ArrayList<>();

        for (Field field : this.getClass().getFields())
        {
            Object fieldObj;

            try
            {
                fieldObj = field.get(this);
            }
            catch (Exception e)
            {
                Mup.logger.error("Unknown field access enumerating PatchDef's.");
                continue;
            }

            if (fieldObj.getClass() == PatchDef.class)
            {
                bugs.add((PatchDef) fieldObj);
            }
        }

        return bugs;
    }

    @SubscribeEvent
    public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(Mup.MODID))
        {
            config.save();
            Mup.config.sync();

            // Right now this event is only ever called from GUI code by Forge itself, but just in case...
            if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
            {
                if (Minecraft.getMinecraft().getConnection() != null)
                {
                    ConfigPacketHandler.sendClientConfig();
                }
            }
        }
    }
}