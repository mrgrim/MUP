package org.gr1m.mc.mup;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.gr1m.mc.mup.mc4.network.MC4PacketHandler;
import org.gr1m.mc.mup.mc5694.network.MC5694PacketHandler;

import java.io.File;

@Mod(modid = org.gr1m.mc.mup.Mup.MODID, name = org.gr1m.mc.mup.Mup.NAME, version = org.gr1m.mc.mup.Mup.VERSION)
public class Mup
{
    public static final String MODID = "mup";
    public static final String NAME = "EigenCraft Unofficial Patch";
    public static final String VERSION = "1.0";

    private static Logger logger;
    private static MupConfig config;
    
    public Mup()
    {
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    { 
        logger = event.getModLog();
        logger.info("EigenCraft Unofficial Patch Loading");

        config = new MupConfig();
        config.init(new File(Launch.minecraftHome, "config/mup.cfg"));

        if (config.mc4)            MC4PacketHandler.registerMessagesAndEvents();
        if (config.mc5694)         MC5694PacketHandler.registerMessagesAndEvents();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
