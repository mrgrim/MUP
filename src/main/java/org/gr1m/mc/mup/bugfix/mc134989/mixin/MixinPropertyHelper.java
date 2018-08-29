package org.gr1m.mc.mup.bugfix.mc134989.mixin;

import net.minecraft.block.properties.PropertyHelper;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PropertyHelper.class)
public abstract class MixinPropertyHelper<T extends Comparable<T>>
{
    @Shadow
    @Final
    private Class<T> valueClass;

    @Shadow
    @Final
    private String name;

    private int cachedHashCode;
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void calculateCachedHashCode(CallbackInfo ci)
    {
        this.cachedHashCode = 31 * this.valueClass.hashCode() + this.name.hashCode();
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
