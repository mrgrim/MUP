package org.gr1m.mc.mup.core;

import org.gr1m.mc.mup.core.config.Configuration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MupCoreConfig {

    private Configuration config;
    
    public class Patch {
        public boolean enabled;
        public boolean loaded;
        public String reason;
        
        public String category;
        public boolean defaults[];
        LoadingStage stage;
        
        public BiFunction<Patch, LoadingStage, String> compatCheck = null;

        public Patch(String categoryIn, boolean defaultsIn[])
        {
            this(categoryIn, defaultsIn, LoadingStage.CORE);
        }
        
        public Patch(String categoryIn, boolean defaultsIn[], LoadingStage stageIn)
        {
            this(categoryIn, defaultsIn, stageIn, null);
        }

        public Patch(String categoryIn, boolean defaultsIn[], LoadingStage stageIn, BiFunction<Patch, LoadingStage, String> compatCheckIn)
        {
            this.category = categoryIn;
            this.defaults = defaultsIn;
            this.stage = stageIn;
            this.compatCheck = compatCheckIn;
        }
    }

    public Patch mc4 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc1133 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc2025 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc2399 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc5694 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc9568 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc14826 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc54026 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc73051 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc80032 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc92916 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc98153 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc108469 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc109832 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc111444 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true}, LoadingStage.CORE, MupCoreCompat.mc111444CompatCheck);
    public Patch mc111978 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc118710 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc119971 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc123320 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    public Patch mc134989 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true}, LoadingStage.CORE, MupCoreCompat.mc134989CompatCheck);
    public Patch mc161869 = new MupCoreConfig.Patch("bug fixes", new boolean[]{true, true});
    
    public Patch newlight = new MupCoreConfig.Patch("optimizations", new boolean[]{true, true});
    public Patch rsturbo = new MupCoreConfig.Patch("optimizations", new boolean[]{true, false});
    
    public Patch hud = new MupCoreConfig.Patch("tweaks", new boolean[]{true, false});
    public Patch profiler = new MupCoreConfig.Patch("tweaks", new boolean[]{false, false});
    public Patch dac = new MupCoreConfig.Patch("tweaks", new boolean[]{false, false});
    public Patch ete = new MupCoreConfig.Patch("tweaks", new boolean[]{false, false});
    public Patch vde = new MupCoreConfig.Patch("tweaks", new boolean[]{false, false});
    public Patch netseqcheck = new MupCoreConfig.Patch("tweaks", new boolean[]{false, false});

    public Patch redstoneplusplus = new MupCoreConfig.Patch("modpatches", new boolean[]{true, true}, LoadingStage.INIT, MupCoreCompat.redstonePlusPlusCompatCheck);
    public Patch vanillafoamfix = new MupCoreConfig.Patch("modpatches", new boolean[]{true, true}, LoadingStage.CORE, MupCoreCompat.vanillaAndFoamFixCompatCheck);

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

        for (Field field : this.getClass().getFields())
        {
            Object fieldObj;

            try
            {
                fieldObj = field.get(this);
            }
            catch (Exception e)
            {
                MupCore.log.error("[MupCore] Unknown field access reading configuration file.");
                continue;
            }

            if (fieldObj.getClass() == MupCoreConfig.Patch.class)
            {
                MupCoreConfig.Patch patch = (MupCoreConfig.Patch)fieldObj;
                patch.enabled = config.get(patch.category, field.getName(), patch.defaults, null, true, 2).getBooleanList()[0];
            }
        }
    }
    
}