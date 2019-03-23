package org.gr1m.mc.mup.tweaks.etde.config;

import net.minecraftforge.common.config.Configuration;
import org.gr1m.mc.mup.config.ICustomizablePatch;

public class EtdeCustomConfig implements ICustomizablePatch
{
    public int Player = 512;
    public int FishHook = 64;
    public int Arrow = 64;
    public int SmallFireball = 64;
    public int Fireball = 64;
    public int Snowball = 64;
    public int LlamaSpit = 64;
    public int EnderPearl = 64;
    public int EnderEye = 64;
    public int Egg = 64;
    public int Potion = 64;
    public int ExpBottle = 64;
    public int FireworkRocket = 64;
    public int Item = 64;
    public int Minecart = 80;
    public int Boat = 80;
    public int Squid = 64;
    public int Wither = 80;
    public int ShulkerBullet = 80;
    public int Bat = 80;
    public int Dragon = 160;
    public int Mobs = 80;
    public int PrimedTNT = 160;
    public int FallingBlock = 160;
    public int Hanging = 160;
    public int ArmorStand = 160;
    public int XPOrb = 160;
    public int AreaEffectCloud = 160;
    public int EnderCrystal = 256;
    public int EvokerFangs = 160;

    @Override
    public void loadConfig(Configuration config, String parentCategory)
    {
        String category = parentCategory + ".distances";
        
        this.Player = config.get(category, "Player", 512).getInt();
        this.FishHook = config.get(category, "FishHook", 64).getInt();
        this.Arrow = config.get(category, "Arrow", 64).getInt();
        this.SmallFireball = config.get(category, "SmallFireball", 64).getInt();
        this.Fireball = config.get(category, "Fireball", 64).getInt();
        this.Snowball = config.get(category, "Snowball", 64).getInt();
        this.LlamaSpit = config.get(category, "LlamaSpit", 64).getInt();
        this.EnderPearl = config.get(category, "EnderPearl", 64).getInt();
        this.EnderEye = config.get(category, "EnderEye", 64).getInt();
        this.Egg = config.get(category, "Egg", 64).getInt();
        this.Potion = config.get(category, "Potion", 64).getInt();
        this.ExpBottle = config.get(category, "ExpBottle", 64).getInt();
        this.FireworkRocket = config.get(category, "FireworkRocket", 64).getInt();
        this.Item = config.get(category, "Item", 64).getInt();
        this.Minecart = config.get(category, "Minecart", 80).getInt();
        this.Boat = config.get(category, "Boat", 80).getInt();
        this.Squid = config.get(category, "Squid", 64).getInt();
        this.Wither = config.get(category, "Wither", 80).getInt();
        this.ShulkerBullet = config.get(category, "ShulkerBullet", 80).getInt();
        this.Bat = config.get(category, "Bat", 80).getInt();
        this.Dragon = config.get(category, "Dragon", 160).getInt();
        this.Mobs = config.get(category, "Mobs", 80).getInt();
        this.PrimedTNT = config.get(category, "PrimedTNT", 160).getInt();
        this.FallingBlock = config.get(category, "FallingBlock", 160).getInt();
        this.Hanging = config.get(category, "Hanging", 160).getInt();
        this.ArmorStand = config.get(category, "ArmorStand", 160).getInt();
        this.XPOrb = config.get(category, "XPOrb", 160).getInt();
        this.AreaEffectCloud = config.get(category, "AreaEffectCloud", 160).getInt();
        this.EnderCrystal = config.get(category, "EnderCrystal", 256).getInt();
        this.EvokerFangs = config.get(category, "EvokerFangs", 160).getInt();
    }
}
