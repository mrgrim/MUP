package org.gr1m.mc.mup.core;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.transformer.Config;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;

public class MupCoreCompat
{
    public static boolean modCompatEnabled = false;
    
    public static boolean JEIDsLoaded = false;
    public static boolean VanillaFixLoaded = false;

    public static boolean FoamFixLoaded = false;
    public static boolean OptiFineLoaded = false;
    public static boolean TweakerooLoaded = false;
    public static boolean QuarkLoaded = false;
    public static boolean SurgeLoaded = false;
    
    public static HashMap<String, String> coreModChecks = new HashMap<String, String>() {{
       put("JEIDsLoaded", "org.dimdev.jeid.JEIDLoadingPlugin");
       put("VanillaFixLoaded", "org.dimdev.vanillafix.VanillaFixLoadingPlugin");
       put("FoamFixLoaded", "pl.asie.foamfix.coremod.FoamFixCore");
       put("OptiFineLoaded", "optifine.OptiFineForgeTweaker");
       put("TweakerooLoaded", "fi.dy.masa.tweakeroo.core.TweakerooCore");
       put("QuarkLoaded", "vazkii.quark.base.asm.LoadingPlugin");
       put("SurgeLoaded", "net.darkhax.surge.core.SurgeLoadingPlugin");
    }};
    
    public static Map<String, String> modList = new HashMap<>();
    
    public static void runCoreModChecks()
    {
        for (String modName : coreModChecks.keySet())
        {
            try
            {
                Field modFlag = MupCoreCompat.class.getField(modName);
                Class.forName(coreModChecks.get(modName));
                modFlag.set(MupCoreCompat.class, true);
                MupCore.log.debug("Compatibility checker found mod: " + modName);
            }
            catch (Exception e)
            {
                // Eh..
            }
        }
    }

    // This is nasty and Mumfrey hates it with a passion, but for now there's no other way. Maybe in the
    // future there will be a cleaner way for mods to cooperate on the final set of mixins to be applied,
    // but the idea of "glue" or "patch" mods like this seems to be looked down upon. :(
    @SuppressWarnings("deprecation")
    public static boolean removeMixinConfiguration(String configFile)
    {
        Iterator<Config> configItr = Mixins.getConfigs().iterator();
        while (configItr.hasNext())
        {
            Config mixinConfig = configItr.next();

            if (mixinConfig.getName().equals(configFile))
            {
                if (mixinConfig.getEnvironment() != null)
                {
                    mixinConfig.getEnvironment().getMixinConfigs().remove(mixinConfig.getName());
                }

                configItr.remove();
                return true;
            }
        }
        
        return false;
    }

    public static final BiFunction<MupCoreConfig.Patch, LoadingStage, String> mc2025CompatCheck = (patchIn, stage) -> {
        if (stage == LoadingStage.CORE)
        {
            if (MupCoreCompat.SurgeLoaded)
            {
                MupCore.log.warn("Disabling conflicting Surge functionality for MC-2025.");
                
                // Reflection to avoid compile time dependency
                try
                {
                    Class<?> surgeConfigClass = Class.forName("net.darkhax.surge.core.SurgeConfiguration");
                    
                    Field surgeFixWallGlitchField = surgeConfigClass.getDeclaredField("fixWallGlitch");
                    Field surgeConfigField = surgeConfigClass.getDeclaredField("config");
                    
                    if (surgeConfigField.get(null) == null)
                    {
                        surgeConfigClass.getDeclaredMethod("init", File.class).invoke(null, new File("config/surge.cfg"));
                    }
                    
                    if ((boolean)(surgeFixWallGlitchField.get(null)))
                    {
                        surgeFixWallGlitchField.set(null, false);
                    }
                }
                catch (Exception e)
                {
                    MupCore.log.warn("Failed to disable Surge fix. Disabling MC-2025 patch instead.");
                    
                    patchIn.reason = "Incompatible with similar Surge feature and unable to disable Surge configuration.";
                    return null;
                }
            }

            patchIn.reason = null;
            return "mixins.mup.mc2025.json";
        }

        return null;
    };
    
    public static final BiFunction<MupCoreConfig.Patch, LoadingStage, String> mc63020CompatCheck = (patchIn, stage) -> {
        if (stage == LoadingStage.CORE)
        {
            if (MupCoreCompat.OptiFineLoaded)
            {
                MupCore.log.warn("Disabling MC-63020 due to functionality overlap with Optifine.");

                patchIn.reason = "Optifine provides a similar fix.";
                return null;
            }

            patchIn.reason = null;
            return "mixins.mup.mc63020.json";
        }

        return null;
    };

    public static final BiFunction<MupCoreConfig.Patch, LoadingStage, String> mc70850CompatCheck = (patchIn, stage) -> {
        if (stage == LoadingStage.CORE)
        {
            if (MupCoreCompat.OptiFineLoaded)
            {
                MupCore.log.warn("Loading Optifine compatible mixins for MC-70850.");

                patchIn.reason = null;
                return "mixins.mup.mc70850-optifine.json";
            }

            patchIn.reason = null;
            return "mixins.mup.mc70850.json";
        }

        return null;
    };

    public static final BiFunction<MupCoreConfig.Patch, LoadingStage, String> mc109832CompatCheck = (patchIn, stage) -> {
        if (stage == LoadingStage.CORE)
        {
            if (MupCoreCompat.QuarkLoaded)
            {
                MupCore.log.warn("Loading Quark compatible mixins for MC-109832.");

                patchIn.reason = null;
                return "mixins.mup.mc109832-quark.json";
            }

            patchIn.reason = null;
            return "mixins.mup.mc109832.json";
        }

        return null;
    };

    public static final BiFunction<MupCoreConfig.Patch, LoadingStage, String> mc111444CompatCheck = (patchIn, stage) -> {
        if (stage == LoadingStage.CORE)
        {
            if (MupCoreCompat.TweakerooLoaded)
            {
                patchIn.loaded = false;
                patchIn.reason = "Tweakeroo provides the same fix";

                MupCore.log.warn("Disabling MC-111444 Patch due to functionality overlap with Tweakeroo.");

                return null;
            }

            patchIn.reason = null;
            return "mixins.mup.mc111444.json";
        }
        
        return null;
    };

    public static final BiFunction<MupCoreConfig.Patch, LoadingStage, String> mc134989CompatCheck = (patchIn, stage) -> {
        if (stage == LoadingStage.CORE)
        {
            if (MupCoreCompat.FoamFixLoaded)
            {
                patchIn.loaded = false;
                patchIn.reason = "FoamFix provides the same fix.";

                MupCore.log.warn("Disabling MC-134989 Patch due to functionality overlap with FoamFix.");

                return null;
            }

            patchIn.reason = null;
            return "mixins.mup.mc134989.json";
        }
        
        return null;
    };

    public static final BiFunction<MupCoreConfig.Patch, LoadingStage, String> redstonePlusPlusCompatCheck = (patchIn, stage) -> {
        if (stage == LoadingStage.INIT)
        {
            List<String> supportedVersions = Arrays.asList("1.2d", "1.3 BETA-2");

            if (!MupCore.config.mc54026.loaded && !MupCore.config.mc88959.loaded)
            {
                patchIn.loaded = false;
                patchIn.reason = "Bug fixes for neither MC-88959 nor MC-54026 are loaded.";

                return null;
            }

            if (modList.containsKey("redstoneplusplus") && supportedVersions.contains(MupCoreCompat.modList.get("redstoneplusplus")))
            {
                if (MupCoreCompat.modList.get("redstoneplusplus").equals("1.2d"))
                {
                    patchIn.reason = null;
                    return "mixins.mup.modcompat.redstoneplusplus.v12d.json";
                }
                else if (MupCoreCompat.modList.get("redstoneplusplus").equals("1.3 BETA-2"))
                {
                    patchIn.reason = null;
                    return "mixins.mup.modcompat.redstoneplusplus.v13b2.json";
                }

                MupCore.log.warn("Loading RedStone++ compatibility patch.");
            }

            patchIn.loaded = false;
            patchIn.reason = "No compatible version of Redstone++ found. Supported versions: " + String.join(", ", supportedVersions);

            return null;
        }
        
        return null;
    };

    public static final BiFunction<MupCoreConfig.Patch, LoadingStage, String> vanillaAndFoamFixCompatCheck = (patchIn, stage) -> {
        if (stage == LoadingStage.CORE)
        {
            if (MupCoreCompat.FoamFixLoaded && MupCoreCompat.VanillaFixLoaded)
            {
                try
                {
                    // This is mostly to avoid a compile time dependency on FoamFix. Alternatively I could read its config
                    // with the shadowed forge config classes used by this mod. I'm not really sure which is better, tbh.
                    
                    Class<?> foamFixShared = Class.forName("pl.asie.foamfix.shared.FoamFixShared");
                    Object foamFixConfig = foamFixShared.getField("config").get(foamFixShared);
                    foamFixConfig.getClass().getMethod("init", File.class, boolean.class)
                                 .invoke(foamFixConfig, new File(new File("config"), "foamfix.cfg"), true);
                    
                    boolean gePatchChunkSerialization = (boolean) (foamFixConfig.getClass().getField("gePatchChunkSerialization").get(foamFixConfig));

                    if (!gePatchChunkSerialization)
                    {
                        patchIn.loaded = false;
                        patchIn.reason = "Conflicting FoamFix configuration is disabled.";

                        return null;
                    }
                }
                catch (Exception e)
                {
                    patchIn.loaded = false;
                    patchIn.reason = "Unable to load FoamFix configuration.";

                    return null;
                }

                if (MupCoreCompat.removeMixinConfiguration("mixins.vanillafix.bugs.json"))
                {
                    MupCore.log.warn("Loading VanillaFix and FoamFix interoperability patch.");

                    patchIn.reason = null;
                    return "mixins.mup.modcompat.vanillafoamfix.json";
                }
            }

            patchIn.loaded = false;
            patchIn.reason = "Requires both FoamFix and VanillaFix to be loaded.";
            
            return null;
        }
        
        return null;
    };

    public static final BiFunction<MupCoreConfig.Patch, LoadingStage, String> RCComplexNewlightCompatCheck = (patchIn, stage) -> {
        if (stage == LoadingStage.INIT)
        {
            if (!MupCore.config.newlight.loaded)
            {
                patchIn.loaded = false;
                patchIn.reason = "Newlight optimization is not loaded.";

                return null;
            }

            if (modList.containsKey("reccomplex"))
            {
                MupCore.log.warn("Loading Recurrent Complex and Newlight compatibility patch.");
                return "mixins.mup.modcompat.rcnewlight.json";
            }
            
            patchIn.reason = "Recurrent Complex not found.";
        }

        return null;
    };
}
