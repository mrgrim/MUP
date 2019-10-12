package org.gr1m.mc.mup.bugfix.mc2399.mixin;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Block.class)
public abstract class MixinBlock
{
    @ModifyVariable(method = "registerBlocks", name = "flag4", at = @At(value = "STORE", ordinal = 0))
    private static boolean noTranslucentNeighborBrightness(boolean flag4)
    {
        return false;
    }
    
    @ModifyVariable(method = "registerBlocks", name = "flag5", at = @At(value = "STORE", ordinal = 0))
    private static boolean noZeroOpacityNeighborBrightness(boolean flag5)
    {
        return false;
    }
}
