package org.gr1m.mc.mup.bugfix.mc1133.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.config.ICustomizablePatch;
import org.gr1m.mc.mup.config.MupConfig;
import org.gr1m.mc.mup.config.gui.IMupConfigElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MC1133CustomConfig implements ICustomizablePatch
{
    public boolean onlyPlayers;

    public MC1133CustomConfig()
    {
    }

    @Override
    public void loadConfig(Configuration config, String parentCategory)
    {
        Property prop;

        prop = config.get(parentCategory, "Only Players", true, "Only apply to players to help reduce CPU load.");
        onlyPlayers = prop.getBoolean();
    }

    public void sanitizeConfig(ConfigCategory categoryIn)
    {
        for (ConfigCategory category : categoryIn.getChildren())
        {
            categoryIn.removeChild(category);
        }

        categoryIn.entrySet().forEach((propSet) ->
                                      {
                                          Property prop = propSet.getValue();
                                          List<String> supportedProperties = Arrays.asList("Only Players");

                                          if (!supportedProperties.contains(prop.getName()))
                                          {
                                              categoryIn.remove(prop.getName());
                                          }
                                      });
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen createGuiScreen(GuiConfig owningScreen, IMupConfigElement patchProperty)
    {
        List<IConfigElement> list = new ArrayList<>();
        ConfigCategory parentCategory;

        parentCategory = MupConfig.config.getCategory(patchProperty.getPatchDef().getCategory() + "." + patchProperty.getPatchDef().getFieldName());

        for (ConfigCategory category : parentCategory.getChildren())
        {
            list.add(new ConfigElement(category));
        }

        for (Property property : parentCategory.getOrderedValues())
        {
            list.add(new ConfigElement(property));
        }

        return new GuiConfig(owningScreen, list, Mup.MODID, false, false, owningScreen.title,
                             ((owningScreen.titleLine2 == null ? "" : owningScreen.titleLine2) + " > MC-1133 Properties"));
    }
}
