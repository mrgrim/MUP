package org.gr1m.mc.mup.tweaks.serverprop.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.serverprop.config.ServerPropCustomConfig;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Entity.class)
public abstract class MixinEntity
{
    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(DDD)D"),
              slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/MoverType;PISTON:Lnet/minecraft/entity/MoverType;", ordinal = 0, opcode = Opcodes.GETSTATIC),
                             to = @At(value = "CONSTANT", args = "stringValue=move", ordinal = 0)))
    private double pistonEntityMoveClamp(double delta, double lowerBound, double upperBound)
    {
        if (Mup.config.serverprop.enabled)
        {
            double overriddenBound = ((ServerPropCustomConfig) Mup.config.serverprop.customConfig).pistonEntityPushLimit;
            
            if (overriddenBound == 0.0D)
            {
                return delta;
            }
            else
            {
                return MathHelper.clamp(delta, 0 - overriddenBound, overriddenBound);
            }
        }
        else
        {
            return MathHelper.clamp(delta, lowerBound, upperBound);
        }
    }
}
