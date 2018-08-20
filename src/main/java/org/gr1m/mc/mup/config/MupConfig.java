package org.gr1m.mc.mup.config;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc4.network.MC4PacketHandler;

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

    public final PatchDef mc4 = new PatchDef("mc4", PatchDef.Side.BOTH, PatchDef.ServerSyncHandlers.ACCEPT, (bug, enabled, handler) -> {
        // Remove the client from the MC-4 plugin channel registration if it insists this bug fix is disabled on its end
        if (!enabled) MC4PacketHandler.registered_clients.remove(handler);
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

    public final PatchDef mc5694 = new PatchDef("mc5694", PatchDef.Side.BOTH)
        .setDisplayName("MC-5694")
        .setCredits("Pokechu22, theosib, gnembon, Xcom, MrGrim")
        .setCategory("bug fixes")
        .setComment(new String[] {"High efficiency tools / fast mining destroys some blocks client-side only"});

    public final PatchDef mc9568 = new PatchDef("mc9568", PatchDef.Side.SERVER)
        .setDisplayName("MC-9568")
        .setCredits("Xcom, MrGrim")
        .setCategory("bug fixes")
        .setComment(new String[] {"Mobs suffocate / go through blocks when growing up near a solid block"});

    public final PatchDef mc54026 = new PatchDef("mc54026", PatchDef.Side.BOTH)
        .setDisplayName("MC-54026")
        .setCredits("gnembon, Xcom, MrGrim")
        .setCategory("bug fixes")
        .setComment(new String[] {"Blocks attached to slime blocks can create ghost blocks"});

    public final PatchDef mc118710 = new PatchDef("mc118710", PatchDef.Side.BOTH)
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
        if (!this.serverLocked)
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

                    bugState = config.get(patchDef.getCategory(), field.getName(), new boolean[] {true, true}, String.join("\n", patchDef.getComment()), true, 2).getBooleanList();

                    if (bugState[0]) patchDef.setLoaded();
                    patchDef.setEnabled(bugState[1]);
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
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onEvent(GuiOpenEvent event)
    {
        //        if (event.gui instanceof Gui)
        //        {
        //            event.gui = new GuiConfigMagicBeans(null);
        //        }
    }
}