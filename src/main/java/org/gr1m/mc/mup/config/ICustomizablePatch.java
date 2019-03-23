package org.gr1m.mc.mup.config;

import net.minecraftforge.common.config.Configuration;

public interface ICustomizablePatch
{
    void loadConfig(Configuration config, String parentCategory);
}
