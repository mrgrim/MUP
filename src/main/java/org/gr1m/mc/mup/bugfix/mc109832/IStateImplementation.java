package org.gr1m.mc.mup.bugfix.mc109832;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IStateImplementation
{
    void onMovedByPiston(World worldIn, BlockPos posIn);
}
