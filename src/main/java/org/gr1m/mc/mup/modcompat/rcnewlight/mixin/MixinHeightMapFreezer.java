package org.gr1m.mc.mup.modcompat.rcnewlight.mixin;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "ivorius.reccomplex.world.gen.feature.HeightMapFreezer", remap = false)
public abstract class MixinHeightMapFreezer
{
    // The goal is to completely disable this class. This optimization is completely redundant with Newlight enabled.
    boolean wasInitialized = false;
    
    @Inject(method = "initialize", at = @At("HEAD"), remap = false, cancellable = true)
    private void dontInitialize(CallbackInfo ci)
    {
        if (Mup.config.newlight.enabled && Mup.config.rcnewlight.enabled)
        {
            ci.cancel();
        }
        else
        {
            // This'll prevent operating on incomplete state in case this is disabled or enabled halfway through structure
            //  generation (can that even happen?). It'll result in a badly lit structure, but it's better than a crash. 
            this.wasInitialized = true;
        }
    }
    
    @Inject(method = "markBlock", at = @At("HEAD"), remap = false, cancellable = true)
    private void dontMarkBlock(BlockPos posIn, IBlockState stateIn, CallbackInfo ci)
    {
        if (!this.wasInitialized && Mup.config.newlight.enabled && Mup.config.rcnewlight.enabled)
        {
            ci.cancel();
        }
    }
    
    @Inject(method = "melt", at = @At("HEAD"), remap = false, cancellable = true)
    private void dontMelt(CallbackInfo ci)
    {
        if (!this.wasInitialized && Mup.config.newlight.enabled && Mup.config.rcnewlight.enabled)
        {
            ci.cancel();
        }
    }
}
