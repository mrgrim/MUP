package org.gr1m.mc.mup.tweaks.rbp.mixin;

import net.minecraft.block.BlockPumpkin;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.rbp.config.RbpCustomConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockPumpkin.class)
public abstract class MixinBlockPumpkin
{
    @Redirect(method = "canPlaceBlockAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isSideSolid(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z", remap = false))
    private boolean relaxedPlacement(World worldIn, BlockPos posIn, EnumFacing facingIn)
    {
        return worldIn.isSideSolid(posIn, facingIn) || (Mup.config.rbp.enabled && ((RbpCustomConfig)(Mup.config.rbp.customConfig)).pumpkin);
    }
}
