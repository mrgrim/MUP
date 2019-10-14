package org.gr1m.mc.mup.core;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(-10000)
@IFMLLoadingPlugin.TransformerExclusions("org.gr1m.mc.mup.core.MupCore")
public class MupCore implements IFMLLoadingPlugin {
    public static final Logger log = LogManager.getLogger();
    private static boolean initialized = false;

    public static MupCoreConfig config;

    public MupCore()
    {
        initialize();

        MixinBootstrap.init();
    }

    public static void initialize()
    {
        if (initialized) return;
        initialized = true;

        config = new MupCoreConfig();
        config.init(new File(((File)(FMLInjectionData.data()[6])), "config/mup.cfg"));
    }
    
    public static void loadMixins(LoadingStage stage)
    {
        String compatReason = "Unknown failure.";
        
        // JustEnoughID's and VanillaFix use the same method for its own mod compatibility system. Coordination with
        //  DimDev will be required to deal with this cleanly.
        if (stage == LoadingStage.CORE)
        {
            Mixins.addConfiguration("mixins.mup.modcompat.core.json");
            MupCoreCompat.modCompatEnabled = true;
        }
        
        for (Field field : config.getClass().getFields())
        {
            Object fieldObj;

            try
            {
                fieldObj = field.get(config);
            }
            catch (Exception e)
            {
                MupCore.log.error("[MupCore] Unknown field access loading Mixins.");
                continue;
            }

            if (fieldObj.getClass() == MupCoreConfig.Patch.class)
            {
                MupCoreConfig.Patch patch = (MupCoreConfig.Patch) fieldObj;

                if (patch.enabled)
                {
                    String jsonConfig;

                    if (patch.compatCheck != null)
                    {
                        if (stage == LoadingStage.CORE)
                        {
                            patch.reason = compatReason; // Set default reason in case INIT stage is never called
                        }
                        
                        jsonConfig = patch.compatCheck.apply(patch, stage);
                    }
                    else
                    {
                        if (patch.category.equals("modpatches"))
                        {
                            MupCore.log.error("Mod patch found without compatibility check function: " + field.getName());
                            patch.reason = "Patch has missing compatibility checks. Please report this.";
                            return;
                        }
                        
                        jsonConfig = "mixins.mup." + field.getName() + ".json";
                    }

                    if (jsonConfig != null)
                    {
                        MupCore.log.debug("Loading mixin configuration: " + jsonConfig);
                        Mixins.addConfiguration(jsonConfig);
                        patch.loaded = true;
                    }
                 }
            }
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        // At this point all coremods should be detected and class loaded but not necessarily initialized.
        MupCoreCompat.runCoreModChecks();
        loadMixins(LoadingStage.CORE);
    }

    @Override public String getAccessTransformerClass() {
        return null;
    }
}