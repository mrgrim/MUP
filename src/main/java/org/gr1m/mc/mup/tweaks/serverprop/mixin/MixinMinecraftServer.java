package org.gr1m.mc.mup.tweaks.serverprop.mixin;

import net.minecraft.server.MinecraftServer;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.serverprop.config.ServerPropCustomConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer
{
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 900))
    private int autosaveInterval(int defaultBase)
    {
        if (Mup.config.serverprop.enabled)
        {
            return ((ServerPropCustomConfig)(Mup.config.serverprop.customConfig)).autosaveInterval;
        }
        else
        {
            return defaultBase;
        }
    }
}
