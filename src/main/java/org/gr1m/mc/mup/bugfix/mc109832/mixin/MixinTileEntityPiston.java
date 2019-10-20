package org.gr1m.mc.mup.bugfix.mc109832.mixin;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gr1m.mc.mup.bugfix.mc109832.IStateImplementation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityPiston.class)
public abstract class MixinTileEntityPiston
{
    @Redirect(method = "clearPistonTileEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", ordinal = 0))
    private boolean checkForMovedStateFinished(World worldIn, BlockPos posIn, IBlockState stateIn, int flags)
    {
        boolean ret = worldIn.setBlockState(posIn, stateIn, flags);
        
        if (stateIn instanceof IStateImplementation)
        {
            ((IStateImplementation) stateIn).onMovedByPiston(worldIn, posIn);
        }
        
        return ret;
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", ordinal = 0))
    private boolean checkForMovedStateUpdate(World worldIn, BlockPos posIn, IBlockState stateIn, int flags)
    {
        boolean ret = worldIn.setBlockState(posIn, stateIn, flags);
        
        if (stateIn instanceof IStateImplementation)
        {
            ((IStateImplementation) stateIn).onMovedByPiston(worldIn, posIn);
        }

        return ret;
    }
}
