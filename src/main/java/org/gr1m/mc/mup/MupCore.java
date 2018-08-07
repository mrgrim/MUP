package org.gr1m.mc.mup;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.SortingIndex(-10000)
@IFMLLoadingPlugin.TransformerExclusions("org.gr1m.mc.mup.MupCore")
public class MupCore implements IFMLLoadingPlugin {
    private static final Logger log = LogManager.getLogger();
    private static boolean initialized = false;

    //private static MupConfig config;

    public MupCore() {
        initialize();

        MixinBootstrap.init();
        
        // @formatter:off
        if (MupConfig.BugFixes.mc4)           Mixins.addConfiguration("mixins.mup.mc4.json");
        if (MupConfig.BugFixes.mc2025)        Mixins.addConfiguration("mixins.mup.mc2025.json");
        if (MupConfig.BugFixes.mc5694)        Mixins.addConfiguration("mixins.mup.mc5694.json");
        if (MupConfig.BugFixes.mc9568)        Mixins.addConfiguration("mixins.mup.mc9568.json");
        if (MupConfig.BugFixes.mc54026)       Mixins.addConfiguration("mixins.mup.mc54026.json");
        if (MupConfig.BugFixes.mc118710)      Mixins.addConfiguration("mixins.mup.mc118710.json");
        if (MupConfig.BugFixes.mc119971)      Mixins.addConfiguration("mixins.mup.mc119971.json");
        // @formatter:on
    }

    public static void initialize() {
        if (initialized) return;
        initialized = true;

        ConfigManager.load(Mup.MODID, Config.Type.INSTANCE);
    }

    @Override public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override public String getModContainerClass() {
        return null;
    }

    @Nullable @Override public String getSetupClass() {
        return null;
    }

    @Override public void injectData(Map<String, Object> data) {}

    @Override public String getAccessTransformerClass() {
        return null;
    }
}