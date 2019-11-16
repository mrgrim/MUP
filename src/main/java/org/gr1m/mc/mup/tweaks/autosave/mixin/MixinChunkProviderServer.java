package org.gr1m.mc.mup.tweaks.autosave.mixin;

import net.minecraft.world.gen.ChunkProviderServer;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.autosave.config.AutosaveCustomConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer
{
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 100))
    private int unloadedChunksPerAutosave(int defaultBase)
    {
        if (Mup.config.autosave.enabled)
        {
            return ((AutosaveCustomConfig)(Mup.config.autosave.customConfig)).maxChunkLimit;
        }
        else
        {
            return defaultBase;
        }
    }
}
