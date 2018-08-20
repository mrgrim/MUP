package org.gr1m.mc.mup.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import org.gr1m.mc.mup.Mup;

import java.util.Set;

public class MupGuiFactory implements IModGuiFactory
{
    @Override
    public void initialize(Minecraft minecraftInstance)
    {

    }
    
    @Override
    public boolean hasConfigGui()
    {
        return !Mup.config.isServerLocked();
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen)
    {
        return new MupGuiConfig(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }
}
