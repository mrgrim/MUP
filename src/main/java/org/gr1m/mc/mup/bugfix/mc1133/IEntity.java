package org.gr1m.mc.mup.bugfix.mc1133;

import net.minecraft.util.math.BlockPos;

public interface IEntity
{
    public BlockPos findFloorEffectBlockPos(int x, int y, int z);
}
