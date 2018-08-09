package org.gr1m.mc.mup;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.gr1m.mc.mup.mc4.network.MC4PacketHandler;
import org.gr1m.mc.mup.mc5694.network.MC5694PacketHandler;

@Mod(modid = Mup.MODID, name = Mup.NAME, version = Mup.VERSION, certificateFingerprint = Mup.FINGERPRINT)
public class Mup
{
    public static final String MODID = "mup";
    public static final String NAME = "EigenCraft Unofficial Patch";
    public static final String VERSION = "1.0";
    public static final String FINGERPRINT = "@FINGERPRINT@";

    private static Logger logger;
    private static MupCoreConfig config;
    
    public Mup()
    {
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    { 
        logger = event.getModLog();
        logger.info("EigenCraft Unofficial Patch Loading");

        config = new MupCoreConfig();
        config.init(event.getSuggestedConfigurationFile());

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
