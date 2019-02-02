package org.gr1m.mc.mup.core;

import org.gr1m.mc.mup.core.config.Configuration;

import java.io.File;

public class MupCoreConfig {

    private Configuration config;

    public boolean mc4;
    public boolean mc2025;
    public boolean mc5694;
    public boolean mc9568;
    public boolean mc54026;
    public boolean mc73051;
    public boolean mc80032;
    public boolean mc98153;
    public boolean mc108469;
    public boolean mc111444;
    public boolean mc118710;
    public boolean mc119971;
    public boolean mc123320;
    public boolean mc134989;
    
    public boolean newlight;
    public boolean rsturbo;
    
    public boolean hud;
    public boolean profiler;
    public boolean dac;

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
        
        // Bug Fixes
        mc4      = config.get("bug fixes", "mc4", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        mc2025   = config.get("bug fixes", "mc2025", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        mc5694   = config.get("bug fixes", "mc5694", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        mc9568   = config.get("bug fixes", "mc9568", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        mc54026  = config.get("bug fixes", "mc54026", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        mc73051  = config.get("bug fixes", "mc73051", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        mc80032  = config.get("bug fixes", "mc80032", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        mc98153  = config.get("bug fixes", "mc98153", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        mc108469 = config.get("bug fixes", "mc108469", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        mc111444 = config.get("bug fixes", "mc111444", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        mc118710 = config.get("bug fixes", "mc118710", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        mc119971 = config.get("bug fixes", "mc119971", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        mc123320 = config.get("bug fixes", "mc123320", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        mc134989 = config.get("bug fixes", "mc134989", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        
        // Optimizations
        newlight = config.get("optimizations", "newlight", new boolean[]{true, true}, null, true, 2).getBooleanList()[0];
        rsturbo  = config.get("optimizations", "rsturbo", new boolean[]{true, false}, null, true, 2).getBooleanList()[0];

        // Tweaks
        hud      = config.get("tweaks", "hud", new boolean[]{true, false}, null, true, 2).getBooleanList()[0];
        profiler = config.get("tweaks", "profiler", new boolean[]{false, false}, null, true, 2).getBooleanList()[0];
        dac      = config.get("tweaks", "dac", new boolean[]{false, false}, null, true, 2).getBooleanList()[0];
    }
    
}