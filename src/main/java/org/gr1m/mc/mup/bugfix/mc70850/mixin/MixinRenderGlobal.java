package org.gr1m.mc.mup.bugfix.mc70850.mixin;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.util.EnumFacing;
import org.gr1m.mc.mup.bugfix.mc70850.ICompiledChunk;
import org.gr1m.mc.mup.bugfix.mc70850.ISetVisibility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal
{
    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/CompiledChunk;isVisible(Lnet/minecraft/util/EnumFacing;Lnet/minecraft/util/EnumFacing;)Z", ordinal = 0),
              slice = @Slice(from = @At(value = "CONSTANT", args="stringValue=iteration"), to = @At(value = "CONSTANT", args="stringValue=captureFrustum")))
    private boolean checkTargetVisibility(CompiledChunk chunkIn, EnumFacing fromFace, EnumFacing toFace)
    {
        return ((ISetVisibility)((ICompiledChunk)chunkIn).getSetVisibility()).anyPathToFace(toFace);
    }
}
