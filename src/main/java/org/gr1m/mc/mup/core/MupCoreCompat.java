package org.gr1m.mc.mup.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MupCoreCompat
{
    public static boolean modCompatEnabled = false;
    public static boolean FoamFixLoaded = false;
    public static boolean OptiFineLoaded = false;
    public static boolean JEIDsLoaded = false;
    
    public static Map<String, String> modList = new HashMap<>();
    
    public static void modCheck(String modName)
    {
        if (modName.contains("FoamFix coremod"))
            MupCoreCompat.FoamFixLoaded = true;
        else if (modName.contains("JustEnoughIDs Extension Plugin"))
            MupCoreCompat.JEIDsLoaded = true;
    }
    
    public static void tweakerCheck(String tweakerName)
    {
        if (tweakerName.contains("optifine.OptiFineForgeTweaker"))
            MupCoreCompat.OptiFineLoaded = true;
    }
    
    public static final Function<MupCoreConfig.Patch, String> mc134989CompatCheck = (patchIn) -> {
        if (MupCoreCompat.FoamFixLoaded)
        {
            patchIn.loaded = false;
            patchIn.reason = "Not compatible with FoamFix";
            
            MupCore.log.warn("Disabling MC-134989 Patch due to incompatibility with FoamFix.");
            
            return null;
        }
        
        return "mixins.mup.mc134989.json";
    };

    public static final Function<MupCoreConfig.Patch, String> redstonePlusPlusCompatCheck = (patchIn) -> {
        List<String> supportedVersions = Arrays.asList("1.2d", "1.3 BETA-2");

        if (!(MupCore.config.mc54026.enabled && MupCore.config.mc54026.loaded))
        {
            patchIn.loaded = false;
            patchIn.reason = "MC-54026 bug fix is not loaded.";

            MupCore.log.warn("Disabling RedStone++ compatibility patch. " + patchIn.reason);

            return null;
        }

        if (modList.containsKey("redstoneplusplus") && supportedVersions.contains(MupCoreCompat.modList.get("redstoneplusplus")))
        {
            if (MupCoreCompat.modList.get("redstoneplusplus").equals("1.2d"))
                return "mixins.mup.modcompat.redstoneplusplus.v12d.json";
            else if (MupCoreCompat.modList.get("redstoneplusplus").equals("1.3 BETA-2"))
                return "mixins.mup.modcompat.redstoneplusplus.v13b2.json";
        }
        
        patchIn.loaded = false;
        patchIn.reason = "No compatible version of Redstone++ found. Supported versions: " + String.join(", ", supportedVersions);

        MupCore.log.warn("Disabling RedStone++ compatibility patch. " + patchIn.reason);

        return null;
    };
}
