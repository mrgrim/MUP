package org.gr1m.mc.mup;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = org.gr1m.mc.mup.Mup.MODID, name = org.gr1m.mc.mup.Mup.NAME, version = org.gr1m.mc.mup.Mup.VERSION)
public class Mup
{
    public static final String MODID = "mup";
    public static final String NAME = "EigenCraft Unofficial Patch";
    public static final String VERSION = "1.0";

    private static Logger logger;
    
    public Mup()
    {
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        logger.info("EigenCraft Unofficial Patch Loading");
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
