package org.gr1m.mc.mup.bugfix.mc109832.mixin;

import net.minecraft.block.BlockObserver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc109832.IMoveResponder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import static net.minecraft.block.BlockObserver.POWERED;

@Mixin(BlockObserver.class)
public abstract class MixinBlockObserver implements IMoveResponder
{
    @Shadow
    protected abstract void startSignal(IBlockState p_190960_1_, World p_190960_2_, BlockPos pos);

    @Shadow
    public abstract void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand);

    @Inject(method = "onBlockAdded", at = @At("HEAD"), cancellable = true)
    private void cancelBlockOnAdded(World worldIn, BlockPos posIn, IBlockState stateIn, CallbackInfo ci)
    {
        if (Mup.config.mc109832.enabled)
        {
            ci.cancel();
        }
    }

    public void onMovedByPiston(IBlockState stateIn, World worldIn, BlockPos posIn)
    {
        if (!worldIn.isRemote && Mup.config.mc109832.enabled)
        {
            if (stateIn.getValue(POWERED))
            {
                this.updateTick(worldIn, posIn, stateIn, worldIn.rand);
            }

            this.startSignal(stateIn, worldIn, posIn);
        }
    }
}
