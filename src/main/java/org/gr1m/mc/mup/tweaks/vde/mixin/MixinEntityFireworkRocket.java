package org.gr1m.mc.mup.tweaks.vde.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.vde.config.VdeCustomConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityFireworkRocket.class)
public abstract class MixinEntityFireworkRocket extends Entity
{
    @Shadow
    public abstract boolean isAttachedToEntity();
    
    public MixinEntityFireworkRocket(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "isInRangeToRenderDist", at = @At("HEAD"), cancellable = true)
    private void fixedViewDistanceCheck(double distance, CallbackInfoReturnable<Boolean> ci)
    {
        if (Mup.config.vde.enabled)
        {
            double d0 = 1.0D;

            if (((VdeCustomConfig)(Mup.config.vde.customConfig)).enableScaling)
            {
                // Effectively doubles distance
                d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;
            }

            if (Double.isNaN(d0))
            {
                d0 = 1.0D;
            }

            d0 = d0 * 64.0D * Entity.getRenderDistanceWeight();

            ci.setReturnValue((distance < d0 * d0) && !this.isAttachedToEntity());
            ci.cancel();
        }
    }
}
