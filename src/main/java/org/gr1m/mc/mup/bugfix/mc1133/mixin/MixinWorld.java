package org.gr1m.mc.mup.bugfix.mc1133.mixin;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.gr1m.mc.mup.bugfix.mc1133.IWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(World.class)
public abstract class MixinWorld implements IWorld
{
    @Shadow
    public abstract WorldBorder getWorldBorder();

    @Shadow
    public abstract boolean isInsideWorldBorder(Entity entityToCheck);

    @Shadow
    public abstract boolean isBlockLoaded(BlockPos pos);

    @Shadow
    public abstract IBlockState getBlockState(BlockPos pos);

    // This is mostly taken from World.getCollisionBoxes_()
    public boolean getBlockCollisions(@Nullable Entity entityIn, AxisAlignedBB aabb, boolean ignoreBorder, Map<BlockPos, List<AxisAlignedBB>> blockList)
    {
        int xmin = MathHelper.floor(aabb.minX) - 1;
        int xmax = MathHelper.ceil(aabb.maxX) + 1;
        int ymin = MathHelper.floor(aabb.minY) - 1;
        int ymax = MathHelper.ceil(aabb.maxY) + 1;
        int zmin = MathHelper.floor(aabb.minZ) - 1;
        int zmax = MathHelper.ceil(aabb.maxZ) + 1;
        
        WorldBorder worldborder = this.getWorldBorder();
        boolean entityOutsideBorder = entityIn != null && entityIn.isOutsideBorder();
        boolean entityInsideBorder = entityIn != null && this.isInsideWorldBorder(entityIn);
        
        IBlockState iblockstate = Blocks.STONE.getDefaultState();
        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

        try
        {
            for (int x = xmin; x < xmax; ++x)
            {
                for (int z = zmin; z < zmax; ++z)
                {
                    boolean xOnEdge = x == xmin || x == xmax - 1;
                    boolean zOnEdge = z == zmin || z == zmax - 1;

                    if ((!xOnEdge || !zOnEdge) && this.isBlockLoaded(blockpos$pooledmutableblockpos.setPos(x, 64, z)))
                    {
                        for (int y = ymin; y < ymax; ++y)
                        {
                            if (!xOnEdge && !zOnEdge || y != ymax - 1)
                            {
                                if (ignoreBorder)
                                {
                                    if (x < -30000000 || x >= 30000000 || z < -30000000 || z >= 30000000)
                                    {
                                        return true;
                                    }
                                }
                                else if (entityIn != null && entityOutsideBorder == entityInsideBorder)
                                {
                                    entityIn.setOutsideBorder(!entityInsideBorder);
                                }

                                blockpos$pooledmutableblockpos.setPos(x, y, z);
                                IBlockState iblockstate1;

                                if (!ignoreBorder && !worldborder.contains(blockpos$pooledmutableblockpos) && entityInsideBorder)
                                {
                                    iblockstate1 = iblockstate;
                                }
                                else
                                {
                                    iblockstate1 = this.getBlockState(blockpos$pooledmutableblockpos);
                                }

                                BlockPos nextPos = new BlockPos(x, y, z);
                                List<AxisAlignedBB> aabbList = new ArrayList<>();
                                
                                iblockstate1.addCollisionBoxToList((World)((Object)this), blockpos$pooledmutableblockpos, aabb, aabbList, entityIn, false);
                                
                                if (!aabbList.isEmpty())
                                {
                                    blockList.put(nextPos, aabbList);
                                }
                            }
                        }
                    }
                }
            }
        }
        finally
        {
            blockpos$pooledmutableblockpos.release();
        }

        return !blockList.isEmpty();
    }

}
