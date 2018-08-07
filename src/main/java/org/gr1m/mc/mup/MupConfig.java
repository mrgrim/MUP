package org.gr1m.mc.mup;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Mup.MODID, type = Config.Type.INSTANCE, category = "General")
public class MupConfig {
    
    @Config.Name("Bug Fixes")
    public static SubCatBugFixes BugFixes = new SubCatBugFixes();

    public static class SubCatBugFixes {
        @Config.RequiresMcRestart
        public boolean mc4 = true;

        @Config.RequiresMcRestart
        public boolean mc2025 = true;

        @Config.RequiresMcRestart
        public boolean mc5694 = true;

        @Config.RequiresMcRestart
        public boolean mc9568 = true;

        @Config.RequiresMcRestart
        public boolean mc54026 = true;

        @Config.RequiresMcRestart
        public boolean mc118710 = true;

        @Config.RequiresMcRestart
        public boolean mc119971 = true;
    }

    @Mod.EventBusSubscriber(modid = Mup.MODID)
    private static class EventHandler {

        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Mup.MODID)) {
                ConfigManager.sync(Mup.MODID, Config.Type.INSTANCE);
            }
        }
    }
}