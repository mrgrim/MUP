package org.gr1m.mc.mup.tweaks.vde.mixin;

import net.minecraft.entity.item.EntityItemFrame;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.vde.config.VdeCustomConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityItemFrame.class)
public class MixinEntityItemFrame
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
        Item frames are a weird case. They did have their tracking range upped, but their viewing distance cut off was
        set to over 32 chunks. This effectively makes their viewing distance 160 blocks always. I'm going to reign that
        in and treat them like any other size 1.0D entity.
     */

    @ModifyConstant(method = "isInRangeToRenderDist", constant = @Constant(doubleValue = 16.0))
    private double overrideRenderDistanceScalingFactor(double defaultFactor)
    {
        return 1.0D;
    }
}
