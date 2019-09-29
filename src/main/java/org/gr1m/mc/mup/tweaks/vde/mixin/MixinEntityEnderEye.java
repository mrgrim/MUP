package org.gr1m.mc.mup.tweaks.vde.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderEye;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.vde.config.VdeCustomConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityEnderEye.class)
public abstract class MixinEntityEnderEye
{
    @ModifyConstant(method = "isInRangeToRenderDist", constant = @Constant(doubleValue = 64.0))
    private double baseRenderDistance(double defaultBase)
    {
        double distance;
        
        if (Mup.config.vde.enabled)
        {
            distance = ((VdeCustomConfig)(Mup.config.vde.customConfig)).entityViewDistance;
        }
        else
        {
            distance = defaultBase;
        }
        
        return distance * Entity.getRenderDistanceWeight(); // For whatever reason this was missing for Ender Eyes
    }

    /*
        This is a bit silly. It looks like Mojang tried to increase the render distance of things such as this by applying
        a constant multiplier to the average hitbox size. In theory, this would work. However, the entity tracker isn't
        changed. Therefore, a client is only ever aware of items within a small rectangular area around the player. It
        never mattered how large the render distance ended up being. It is capped by the tracker.
        
        For now I'm dealing with this by applying the original scaling shenanigans unless view distance scaling is disabled
        in the mod options. In that case, much less aggressive values are applied. This might seem backwards.. I dunno.
     */

    /* Disabling for Ender Eyes as the vanilla scaling is the same as disabling scaling. Leaving commented to I remember
       to check in newer versions.
    
    @ModifyConstant(method = "isInRangeToRenderDist", constant = @Constant(doubleValue = 4.0, ordinal = 0))
    private double overrideRenderDistanceScalingFactor(double defaultFactor)
    {
        if (Mup.config.vde.enabled && ((VdeCustomConfig)(Mup.config.vde.customConfig)).enableScaling)
        {
            return defaultFactor;
        }
        else
        {
            return 1.0D;
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
    } */
}
