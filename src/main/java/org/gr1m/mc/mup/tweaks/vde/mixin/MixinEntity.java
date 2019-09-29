package org.gr1m.mc.mup.tweaks.vde.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.vde.config.VdeCustomConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class MixinEntity
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
    
    @Redirect(method = "isInRangeToRenderDist", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/AxisAlignedBB;getAverageEdgeLength()D", ordinal = 0))
    private double overrideRenderSizeCheck(AxisAlignedBB box)
    {
        if (((VdeCustomConfig)(Mup.config.vde.customConfig)).enableScaling)
        {
            return box.getAverageEdgeLength();
        }
        else
        {
            return 1.0D;
        }
    }
}
