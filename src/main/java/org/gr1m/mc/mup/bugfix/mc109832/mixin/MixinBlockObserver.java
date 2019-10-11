package org.gr1m.mc.mup.bugfix.mc109832.mixin;

import net.minecraft.block.BlockObserver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockObserver.class)
public abstract class MixinBlockObserver
{
    @Inject(method = "onBlockAdded", at = @At("HEAD"), cancellable =  true)
    private void doNothingOnBlockAdded(World worldIn, BlockPos pos, IBlockState state, CallbackInfo ci)
    {
        if (Mup.config.mc109832.enabled)
        {
            ci.cancel();
        }
    }
}
