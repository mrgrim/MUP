package org.gr1m.mc.mup.bugfix.mc98153.mixin;

import net.minecraft.entity.Entity;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc98153.IEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity
{
    @Shadow
    @Nullable
    public abstract Entity changeDimension(int dimensionIn);

    private boolean deferredDimensionChangePending;
    private int deferredDimension;
    
    @Inject(method = "onEntityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;decrementTimeUntilPortal()V", ordinal = 0, shift = At.Shift.AFTER))
    private void performDeferredDimensionChange(CallbackInfo ci)
    {
        if (Mup.config.mc98153.enabled && this.hasDeferredDimensionChange())
        {
            this.changeDimension(this.getDeferredDimension());
            this.clearDeferredDimensionChange();
        }
    }
    
    public void setDeferredDimensionChange(int dimensionIn)
    {
        this.deferredDimensionChangePending = true;
        this.deferredDimension = dimensionIn;
    }
    
    public void clearDeferredDimensionChange()
    {
        this.deferredDimensionChangePending = false;
    }

    public boolean hasDeferredDimensionChange()
    {
        return deferredDimensionChangePending;
    }
    
    public int getDeferredDimension()
    {
        return deferredDimension;
    }
}
