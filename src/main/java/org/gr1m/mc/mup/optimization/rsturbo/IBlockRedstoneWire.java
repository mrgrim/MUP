package org.gr1m.mc.mup.optimization.rsturbo;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlockRedstoneWire
{
    void setCanProvidePower(boolean canProvidePowerIn);
    IBlockState callCalculateCurrentChanges(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state);
}
