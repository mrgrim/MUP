package org.gr1m.mc.mup.config;

import net.minecraftforge.fml.client.config.IConfigElement;

public interface IMupConfigElement extends IConfigElement
{
    boolean isToggleable();
    String getCredits();
    String getSideEffects();
}
