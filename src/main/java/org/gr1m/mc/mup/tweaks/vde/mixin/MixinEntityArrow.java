package org.gr1m.mc.mup.tweaks.vde.mixin;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.AxisAlignedBB;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.vde.config.VdeCustomConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityArrow.class)
public abstract class MixinEntityArrow
{
    @ModifyConstant(method = "isInRangeToRenderDist", constant = @Constant(doubleValue = 64.0))
    private double baseRenderDistance(double defaultBase)
    {
        if (Mup.config.vde.enabled)
        {
            return ((VdeCustomConfig)(Mup.config.vde.customConfig)).entityViewDistance;
        }
        else
        {
            return defaultBase;
        }
    }

    /*
        This is a bit silly. It looks like Mojang tried to increase the render distance of things such as this by applying
        a constant multiplier to the average hitbox size. In theory, this would work. However, the entity tracker isn't
        changed. Therefore, a client is only ever aware of items within a small rectangular area around the player. It
        never mattered how large the render distance ended up being. It is capped by the tracker.
        
        For now I'm dealing with this by applying the original scaling shenanigans unless view distance scaling is disabled
        in the mod options. In that case, much less aggressive values are applied. This might seem backwards.. I dunno.
     */
    
    @ModifyConstant(method = "isInRangeToRenderDist", constant = @Constant(doubleValue = 10.0))
    private double overrideRenderDistanceScalingFactor(double defaultFactor)
    {
        if (Mup.config.vde.enabled && ((VdeCustomConfig)(Mup.config.vde.customConfig)).enableScaling)
        {
            return defaultFactor;
        }
        else
        {
            return 2.0D;
        }
    }

    @Redirect(method = "isInRangeToRenderDist", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/AxisAlignedBB;getAverageEdgeLength()D", ordinal = 0))
    private double overrideRenderSizeCheck(AxisAlignedBB box)
    {
        if (Mup.config.vde.enabled && ((VdeCustomConfig)(Mup.config.vde.customConfig)).enableScaling)
        {
            return box.getAverageEdgeLength();
        }
        else
        {
            return 1.0D;
        }
    }
}
