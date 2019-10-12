package org.gr1m.mc.mup.bugfix.mc1133;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface IWorld
{
    boolean getBlockCollisions(@Nullable Entity entityIn, AxisAlignedBB aabb, boolean ignoreBorder, Map<BlockPos, List<AxisAlignedBB>> blockList);
}
