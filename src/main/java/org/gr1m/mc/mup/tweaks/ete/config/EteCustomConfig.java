package org.gr1m.mc.mup.tweaks.ete.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.config.ICustomizablePatch;
import org.gr1m.mc.mup.config.MupConfig;
import org.gr1m.mc.mup.config.gui.IMupConfigElement;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EteCustomConfig implements ICustomizablePatch
{
    public static class Var
    {
        // TODO: Add ability to store server settings for display. Will require custom GUI lists and sync handlers.
        public int range;
        public int frequency;
        int defaultRange;
        int defaultFrequency;
        String comment;
        
        Var(int defaultRangeIn, int defaultFrequencyIn, String commentIn)
        {
            this.defaultRange = defaultRangeIn;
            this.defaultFrequency = defaultFrequencyIn;
            this.comment = commentIn;
        }
    }
    
    public final Var Player = new Var(512, 2, "Player");
    public final Var FishHook = new Var(64, 5, "Fish Hook");
    public final Var Arrow = new Var(64, 20, "Arrow");
    public final Var SmallFireball = new Var(64, 10, "Small Fireball");
    public final Var Fireball = new Var(64, 10, "Fireball");
    public final Var Snowball = new Var(64, 10, "Snowball");
    public final Var LlamaSpit = new Var(64, 10, "Llama Spit");
    public final Var EnderPearl = new Var(64, 10, "Ender Pearl");
    public final Var EnderEye = new Var(64, 4, "Ender Eye");
    public final Var Egg = new Var(64, 10, "Egg");
    public final Var Potion = new Var(64, 10, "Potion");
    public final Var ExpBottle = new Var(64, 10, "Bottle o' Enchanting");
    public final Var FireworkRocket = new Var(64, 10, "Firework Rocket");
    public final Var Item = new Var(64, 20, "Item");
    public final Var Minecart = new Var(80, 3, "Minecart");
    public final Var Boat = new Var(80, 3, "Boat");
    public final Var Squid = new Var(64, 3, "Squid");
    public final Var Wither = new Var(80, 3, "Wither");
    public final Var ShulkerBullet = new Var(80, 3, "Shulker Bullet");
    public final Var Bat = new Var(80, 3, "Bat");
    public final Var Dragon = new Var(160, 3, "Dragon");
    public final Var Mobs = new Var(80, 3, "Mobs");
    public final Var PrimedTNT = new Var(160, 10, "Primed TNT");
    public final Var FallingBlock = new Var(160, 20, "Falling Block");
    public final Var Hanging = new Var(160, 0, "Hanging Items");
    public final Var ArmorStand = new Var(160, 3, "Armor Stand");
    public final Var XPOrb = new Var(160, 20, "XP Orb");
    public final Var AreaEffectCloud = new Var(160, 0, "Area of Effect Cloud");
    public final Var EnderCrystal = new Var(256, 0, "Ender Crystal");
    public final Var EvokerFangs = new Var(160, 2, "Evoker Fangs");
    
    public EteCustomConfig()
    {
    }
    
    @Override
    public void loadConfig(Configuration config, String parentCategory)
    {
        String distanceCat = parentCategory + ".distances";
        String frequencyCat = parentCategory + ".frequencies";

        for (Field field : this.getClass().getFields())
        {
            Object fieldObj;

            try
            {
                fieldObj = field.get(this);
            }
            catch (Exception e)
            {
                Mup.logger.error("Unknown field access reading ETE configuration.");
                continue;
            }
            
            if (fieldObj.getClass() == Var.class)
            {
                Var var = (Var)fieldObj;
                Property prop;
                
                prop = config.get(distanceCat, field.getName(), var.defaultRange, var.comment, 16, 512);
                prop.setHasSlidingControl(true);
                var.range = prop.getInt();

                prop = config.get(frequencyCat, field.getName(), var.defaultFrequency, var.comment, 0, 20);
                prop.setHasSlidingControl(true);
                var.frequency = prop.getInt();
            }
        }
    }
    
    @Override
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
                             ((owningScreen.titleLine2 == null ? "" : owningScreen.titleLine2) + " > ETE Properties"));
    }
}
