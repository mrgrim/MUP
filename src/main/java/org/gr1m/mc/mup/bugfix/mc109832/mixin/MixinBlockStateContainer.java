package org.gr1m.mc.mup.bugfix.mc109832.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gr1m.mc.mup.bugfix.mc109832.IMoveResponder;
import org.gr1m.mc.mup.bugfix.mc109832.IStateImplementation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockStateContainer.StateImplementation.class)
public abstract class MixinBlockStateContainer implements IStateImplementation
{
    @Shadow
    @Final
    private Block block;

    public void onMovedByPiston(World worldIn, BlockPos posIn)
    {
        if (this.block instanceof IMoveResponder)
        {
            ((IMoveResponder)this.block).onMovedByPiston((IBlockState) ((Object)(this)), worldIn, posIn);
        }
    }
}
