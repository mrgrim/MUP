package org.gr1m.mc.mup.bugfix.mc109832;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// Making this kinda general in case I ever decide to do an API
public interface IMoveResponder
{
    void onMovedByPiston(IBlockState stateIn, World worldIn, BlockPos posIn);
}
