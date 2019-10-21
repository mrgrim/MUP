package org.gr1m.mc.mup.bugfix.mc109832.mixin;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gr1m.mc.mup.bugfix.mc109832.IStateImplementation;
import org.gr1m.mc.mup.bugfix.mc109832.QuarkCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityPiston.class)
public class MixinTileEntityPistonQuark
{
    @Redirect(method = "clearPistonTileEntity", at = @At(value = "INVOKE", target = "Lvazkii/quark/base/asm/ASMHooks;setPistonBlock(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", ordinal = 0, remap = false))
    private boolean checkForMovedStateFinishedQuark(World worldIn, BlockPos posIn, IBlockState stateIn, int flags)
    {
        boolean ret;

        try
        {
            ret = (boolean)(QuarkCompat.quarkHookMethod.invoke(worldIn, posIn, stateIn, flags));
        }
        catch (Throwable e)
        {
            throw new RuntimeException();
        }

        if (stateIn instanceof IStateImplementation)
        {
            ((IStateImplementation) stateIn).onMovedByPiston(worldIn, posIn);
        }

        return ret;
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lvazkii/quark/base/asm/ASMHooks;setPistonBlock(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", ordinal = 0, remap = false))
    private boolean checkForMovedStateUpdateQuark(World worldIn, BlockPos posIn, IBlockState stateIn, int flags)
    {
        boolean ret;

        try
        {
            ret = (boolean)(QuarkCompat.quarkHookMethod.invoke(worldIn, posIn, stateIn, flags));
        }
        catch (Throwable e)
        {
            throw new RuntimeException();
        }

        if (stateIn instanceof IStateImplementation)
        {
            ((IStateImplementation) stateIn).onMovedByPiston(worldIn, posIn);
        }

        return ret;
    }
}
