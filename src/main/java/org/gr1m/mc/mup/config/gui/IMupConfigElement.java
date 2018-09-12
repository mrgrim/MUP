package org.gr1m.mc.mup.config.gui;

import net.minecraftforge.fml.client.config.IConfigElement;
import org.gr1m.mc.mup.config.PatchDef;

public interface IMupConfigElement extends IConfigElement
{
    boolean isToggleable();
    String getCredits();
    String getSideEffects();
    PatchDef getPatchDef();
}
