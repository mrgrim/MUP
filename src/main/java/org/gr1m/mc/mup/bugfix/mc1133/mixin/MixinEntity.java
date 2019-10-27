package org.gr1m.mc.mup.bugfix.mc1133.mixin;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc1133.IEntity;
import org.gr1m.mc.mup.bugfix.mc1133.IWorld;
import org.gr1m.mc.mup.bugfix.mc1133.config.MC1133CustomConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.HashMap;
import java.util.List;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity
{
    @Shadow
    public World world;

    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();
    
    @Redirect(method = "move", at = @At(value = "NEW", target = "net/minecraft/util/math/BlockPos", ordinal = 0),
              slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;resetPositionToBB()V", ordinal = 1),
                             to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateFallState(DZLnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;)V", ordinal = 0)))
    private BlockPos findTrueFloor(int x, int y, int z)
    {
        if (Mup.config.mc1133.enabled &&
            (!((MC1133CustomConfig)(Mup.config.mc1133.customConfig)).onlyPlayers ||
             (((MC1133CustomConfig)(Mup.config.mc1133.customConfig)).onlyPlayers && (Object)this instanceof EntityPlayer)))
        {
            return this.findFloorEffectBlockPos(x, y, z);
        }
        else
        {
            return new BlockPos(x, y, z);
        }
    }

    @Redirect(method = "move", at =  @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;getMaterial()Lnet/minecraft/block/material/Material;", ordinal = 0),
              slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;resetPositionToBB()V", ordinal = 1),
                             to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateFallState(DZLnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;)V", ordinal = 0)))
    private Material cancelEntityAirCheck(IBlockState state)
    {
        if (Mup.config.mc1133.enabled &&
            (!((MC1133CustomConfig)(Mup.config.mc1133.customConfig)).onlyPlayers ||
            (((MC1133CustomConfig)(Mup.config.mc1133.customConfig)).onlyPlayers && (Object)this instanceof EntityPlayer)))
        {
            return Material.ROCK;
        }
        else
        {
            return state.getMaterial();
        }
    }

    public BlockPos findFloorEffectBlockPos(int x, int y, int z)
    {
        BlockPos blockpos = new BlockPos(x, y, z);
        IBlockState iblockstate = this.world.getBlockState(blockpos);
        
// TODO: I need a better "simple" check here.         
//        if (iblockstate.getBlock().isAir(iblockstate, this.world, blockpos))
//        {
            HashMap<BlockPos, List<AxisAlignedBB>> blockList = new HashMap<>();

            AxisAlignedBB playerAABB = new AxisAlignedBB(this.getEntityBoundingBox().minX, this.getEntityBoundingBox().minY - 0.20000000298023224D, this.getEntityBoundingBox().minZ,
                                                         this.getEntityBoundingBox().maxX, this.getEntityBoundingBox().minY - 0.20000000298023224D, this.getEntityBoundingBox().maxZ);

            ((IWorld)(this.world)).getBlockCollisions((Entity)((Object)this), playerAABB, false, blockList);

            if (!blockList.isEmpty())
            {
                double largestIntersectingArea = 0.0D;
                BlockPos winningPos = null;

                for (BlockPos pos : blockList.keySet())
                {
                    double intersectingArea = 0.0D;

                    for (AxisAlignedBB blockAABB : blockList.get(pos))
                    {
                        double width = Math.abs(Math.min(this.getEntityBoundingBox().maxX, blockAABB.maxX) - Math.max(this.getEntityBoundingBox().minX, blockAABB.minX));
                        double height = Math.abs(Math.min(this.getEntityBoundingBox().maxZ, blockAABB.maxZ) - Math.max(this.getEntityBoundingBox().minZ, blockAABB.minZ));

                        intersectingArea += width * height;
                    }

                    if (intersectingArea > largestIntersectingArea)
                    {
                        largestIntersectingArea = intersectingArea;
                        winningPos = pos;
                    }
                }

                if (winningPos != null)
                {
                    blockpos = winningPos;
                }
            }
//        }

        return blockpos;
    }
}
