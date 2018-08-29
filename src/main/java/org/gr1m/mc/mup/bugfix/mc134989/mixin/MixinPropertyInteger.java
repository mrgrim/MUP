package org.gr1m.mc.mup.bugfix.mc134989.mixin;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.properties.PropertyInteger;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PropertyInteger.class)
public abstract class MixinPropertyInteger
{
    @Shadow
    @Final
    private ImmutableSet<Integer> allowedValues;

    private int cachedHashCode;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void calculateCachedHashCode(CallbackInfo ci)
    {
        this.cachedHashCode = 31 * super.hashCode() + this.allowedValues.hashCode();
    }

    @Inject(method = "hashCode", at = @At("HEAD"), cancellable = true)
    private void cachedHashCode(CallbackInfoReturnable<Integer> ci)
    {
        if (Mup.config.mc134989.enabled)
        {
            ci.setReturnValue(this.cachedHashCode);
        }
    }
}
