package org.gr1m.mc.mup.bugfix.mc98153.mixin;

import net.minecraft.block.BlockEndPortal;
import net.minecraft.entity.Entity;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc98153.IEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockEndPortal.class)
public abstract class MixinBlockEndPortal
{
    @Redirect(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;changeDimension(I)Lnet/minecraft/entity/Entity;", ordinal = 0))
    private Entity deferChangeDimension(Entity entityIn, int dimensionIn)
    {
        if (Mup.config.mc98153.enabled)
        {
            ((IEntity) entityIn).setDeferredDimensionChange(dimensionIn);
            return entityIn;
        }
        else
        {
            return entityIn.changeDimension(dimensionIn);
        }
    }
}
