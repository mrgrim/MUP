package org.gr1m.mc.mup.tweaks.vde.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.vde.config.VdeCustomConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLeashKnot.class)
public abstract class MixinEntityLeashKnot extends Entity
{
    public MixinEntityLeashKnot(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "isInRangeToRenderDist", at = @At("HEAD"), cancellable = true)
    private void fixedViewDistanceCheck(double distance, CallbackInfoReturnable<Boolean> ci)
    {
        // tf happened to the vanilla version of this? :O

        if (Mup.config.vde.enabled)
        {
            // Leash knots use the default entity size but override the bounding box.. wtf?!
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
