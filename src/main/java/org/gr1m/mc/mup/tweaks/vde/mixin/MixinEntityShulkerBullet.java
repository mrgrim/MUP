package org.gr1m.mc.mup.tweaks.vde.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.vde.config.VdeCustomConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityShulkerBullet.class)
public abstract class MixinEntityShulkerBullet extends Entity
{
    public MixinEntityShulkerBullet(World worldIn) { super(worldIn); }

    @Inject(method = "isInRangeToRenderDist", at = @At("HEAD"), cancellable = true)
    private void fixedViewDistanceCheck(double distance, CallbackInfoReturnable<Boolean> ci)
    {
        // tf happened to the vanilla version of this? :O

        if (Mup.config.vde.enabled)
        {
            double d0 = 1.0D;
            
            if (((VdeCustomConfig)(Mup.config.vde.customConfig)).enableScaling)
            {
                // Makes d0 roughly 2.0.. ish
                d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 6.0D;
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
