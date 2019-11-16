package org.gr1m.mc.mup.tweaks.autosave.mixin;

import net.minecraft.server.MinecraftServer;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.autosave.config.AutosaveCustomConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer
{
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 900))
    private int autosaveInterval(int defaultBase)
    {
        if (Mup.config.autosave.enabled)
        {
            return ((AutosaveCustomConfig)(Mup.config.autosave.customConfig)).autosaveInterval;
        }
        else
        {
            return defaultBase;
        }
    }
}
