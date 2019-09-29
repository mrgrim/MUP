package org.gr1m.mc.mup.tweaks.vde.mixin;

import net.minecraft.client.renderer.RenderGlobal;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal
{
    @ModifyConstant(method = "setupTerrain", constant = @Constant(doubleValue = 2.5D, ordinal = 0),
                    slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=update", ordinal = 0),
                                   to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setRenderDistanceWeight(D)V", opcode = Opcodes.INVOKESTATIC, ordinal = 0)))
    private double setMaximumClamp(double originalMax)
    {
        if (Mup.config.vde.enabled)
        {
            return 4.0D;
        }
        else
        {
            return originalMax;
        }
    }
}
