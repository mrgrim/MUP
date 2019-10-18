package org.gr1m.mc.mup.bugfix.mc70850.mixin;

import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.SetVisibility;
import org.gr1m.mc.mup.bugfix.mc70850.ICompiledChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CompiledChunk.class)
public class MixinCompiledChunk implements ICompiledChunk
{
    @Shadow
    private SetVisibility setVisibility;

    public SetVisibility getSetVisibility()
    {
        return setVisibility;
    }
}
