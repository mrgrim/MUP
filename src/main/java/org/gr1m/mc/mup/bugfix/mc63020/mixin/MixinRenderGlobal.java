package org.gr1m.mc.mup.bugfix.mc63020.mixin;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Set;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal
{
    @Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Ljava/util/Set;size()I", remap = false, ordinal = 0),
              slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;getVisibleFacings(Lnet/minecraft/util/math/BlockPos;)Ljava/util/Set;", ordinal = 0),
                             to = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnumFacing;getFacingFromVector(FFF)Lnet/minecraft/util/EnumFacing;", ordinal = 0)))
    private int overrideOpposingSideCheck(Set<EnumFacing> facingSetIn)
    {
        return 0;
    }
    
}
