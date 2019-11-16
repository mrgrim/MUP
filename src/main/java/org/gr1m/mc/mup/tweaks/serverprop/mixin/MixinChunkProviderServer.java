package org.gr1m.mc.mup.tweaks.serverprop.mixin;

import net.minecraft.world.gen.ChunkProviderServer;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.serverprop.config.ServerPropCustomConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer
{
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 100))
    private int unloadedChunksPerAutosave(int defaultBase)
    {
        if (Mup.config.serverprop.enabled)
        {
            return ((ServerPropCustomConfig)(Mup.config.serverprop.customConfig)).maxChunkLimit;
        }
        else
        {
            return defaultBase;
        }
    }
}
