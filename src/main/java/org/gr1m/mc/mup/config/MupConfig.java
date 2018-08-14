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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Mup.MODID)
public class MupConfig {

    public static Configuration config;

    public final BugDef mc4       = new BugDef();
    public final BugDef mc2025    = new BugDef();
    public final BugDef mc5694    = new BugDef();
    public final BugDef mc9568    = new BugDef();
    public final BugDef mc54026   = new BugDef();
    public final BugDef mc118710  = new BugDef();
    public final BugDef mc119971  = new BugDef();

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
        
        List<Object[]> bugs = new ArrayList<>();
        
        bugs.add(new Object[] { "mc4",      "MC-4",      new String[] { "Item drops sometimes appear at the wrong location" }});
        bugs.add(new Object[] { "mc2025",   "MC-2025",   new String[] { "Mobs going out of fenced areas/suffocate in blocks when loading chunks" }});
        bugs.add(new Object[] { "mc5694",   "MC-5694",   new String[] { "High efficiency tools / fast mining destroys some blocks client-side only" }});
        bugs.add(new Object[] { "mc9568",   "MC-9568",   new String[] { "Mobs suffocate / go through blocks when growing up near a solid block" }});
        bugs.add(new Object[] { "mc54026",  "MC-54026",  new String[] { "Blocks attached to slime blocks can create ghost blocks" }});
        bugs.add(new Object[] { "mc118710", "MC-118710", new String[] { TextFormatting.RED + "[Experimental] " + TextFormatting.YELLOW + "Blocks take multiple attempts to mine" }});
        bugs.add(new Object[] { "mc119971", "MC-119971", new String[] { "Various duplications, deletions, and data corruption at chunk boundaries, caused",
                                                                        "by loading outdated chunks — includes duping and deletion of entities/mobs,",
                                                                        "items in hoppers, and blocks moved by pistons, among other problems" }});
        
        for (Object[] bugEntry : bugs) {
            String bug = (String)bugEntry[0];
            String displayName = (String)bugEntry[1];
            String[] comment = (String[])bugEntry[2];
            
            boolean[] bugState;
            BugDef bugDef;
                    
            bugState = config.get("bug fixes", bug, new boolean[]{true, true}, String.join("\n", comment), true, 2).getBooleanList();

            try {
                bugDef = (BugDef) (this.getClass().getField(bug).get(this));
            } catch (Exception e) {
                Mup.logger.error("Unknown field access reading configuration file.");
                continue;
            }
            
            if (bugState[0]) bugDef.setLoaded();
            bugDef.setEnabled(bugState[1]);
            bugDef.setDisplayName(displayName);
        }
        
        config.save();
    }
    
    public BugDef get(String propName)
    {
        try {
            return (BugDef) (this.getClass().getField(propName).get(this));
        } catch (Exception e) {
            Mup.logger.error("Unknown field access fetching property.");
            return null;
        }
    }

    public class BugDef {
        protected boolean loaded;
        protected boolean enabled;
        protected String displayName;
        
        public boolean isLoaded()
        {
            return this.loaded;
        }
        
        private void setLoaded()
        {
            // Can only set to true. Bug fixes can only be loaded at startup and cannot be unloaded
            this.loaded = true;
        }
        
        public boolean isEnabled()
        {
            return this.enabled;
        }
        public void setEnabled(boolean isEnabled)
        {
            this.enabled = isEnabled && this.loaded;
        }
        
        public String getDisplayName() { return this.displayName; }
        public void setDisplayName(String displayNameIn) { this.displayName = displayNameIn; }
    }

    @SubscribeEvent
    public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Mup.MODID)) {
            config.save();
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(GuiOpenEvent event)
    {
//        if (event.gui instanceof Gui)
//        {
//            event.gui = new GuiConfigMagicBeans(null);
//        }
    }
}