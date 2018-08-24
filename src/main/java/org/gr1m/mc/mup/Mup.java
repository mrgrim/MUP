package org.gr1m.mc.mup;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.gr1m.mc.mup.bugfix.mc4.network.MC4PacketHandler;
import org.gr1m.mc.mup.bugfix.mc5694.network.MC5694PacketHandler;
import org.gr1m.mc.mup.config.network.ConfigPacketHandler;
import org.gr1m.mc.mup.config.MupConfig;

import java.io.File;

@Mod(modid = Mup.MODID,
     name = Mup.NAME,
     version = Mup.VERSION,
     acceptedMinecraftVersions = "1.12.2",
     certificateFingerprint = Mup.FINGERPRINT,
     guiFactory = "org.gr1m.mc.mup.config.MupGuiFactory")
public class Mup
{
    public static final String MODID = "mup";
    public static final String NAME = "EigenCraft Unofficial Patch";
    public static final String VERSION = "1.1.0";
    public static final String FINGERPRINT = "@FINGERPRINT@";

    public static Logger logger;
    public static MupConfig config = new MupConfig();

    public Mup()
    {
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        logger.info("EigenCraft Unofficial Patch Loading");

        config.init(new File(Launch.minecraftHome, "config/mup.cfg"));
        config.load();

        if (config.mc4.isLoaded()) MC4PacketHandler.registerMessagesAndEvents();
        if (config.mc5694.isLoaded()) MC5694PacketHandler.registerMessagesAndEvents();

        ConfigPacketHandler.registerMessagesAndEvents();
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
