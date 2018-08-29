package org.gr1m.mc.mup.bugfix.mc134989.mixin;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PropertyEnum.class)
public abstract class MixinPropertyEnum<T extends Enum<T> & IStringSerializable>
{
    @Shadow
    @Final
    private ImmutableSet<T> allowedValues;

    @Shadow
    @Final
    private Map<String, T> nameToValue;

    private int cachedHashCode;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void calculateCachedHashCode(CallbackInfo ci)
    {
        int i = super.hashCode();
        i = 31 * i + this.allowedValues.hashCode();
        i = 31 * i + this.nameToValue.hashCode();
        
        this.cachedHashCode = i;
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
