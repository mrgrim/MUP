package org.gr1m.mc.mup.tweaks.vde.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.vde.config.VdeCustomConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityFishHook.class)
public abstract class MixinEntityFishHook extends Entity
{
    public MixinEntityFishHook(World worldIn) {
        super(worldIn);
    }
    
    @Inject(method = "isInRangeToRenderDist", at = @At("HEAD"), cancellable = true)
    private void fixedViewDistanceCheck(double distance, CallbackInfoReturnable<Boolean> ci)
    {
        // tf happened to the vanilla version of this? :O
        
        if (Mup.config.vde.enabled)
        {
            // Fish hooks use the default entity size.. whatever
            double d0 = 1.0D;
            
            if (((VdeCustomConfig)(Mup.config.vde.customConfig)).enableScaling)
            {
                d0 = this.getEntityBoundingBox().getAverageEdgeLength();
            }

            if (Double.isNaN(d0))
            {
                d0 = 1.0D;
            }

            d0 = d0 * 64.0D * Entity.getRenderDistanceWeight();
            
            ci.setReturnValue(distance < d0 * d0);
            ci.cancel();
        }
    }
}
