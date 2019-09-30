package org.gr1m.mc.mup.core;

import java.util.function.Function;

public class MupCoreCompat
{
    public static boolean FoamFixLoaded = false;
    public static boolean OptiFineLoaded = false;
    
    public static void modCheck(String modName)
    {
        if (modName.contains("FoamFix coremod"))
            MupCoreCompat.FoamFixLoaded = true;
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
}
