package org.gr1m.mc.mup.bugfix.mc64836.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(EntityMinecart.class)
public abstract class MixinEntityMinecart
{
    @Redirect(method = "moveAlongTrack", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;moveForward:F", opcode = Opcodes.GETFIELD, ordinal = 0),
              slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/EntityMinecart;getPassengers()Ljava/util/List;", ordinal = 1),
                             to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/EntityMinecart;shouldDoRailFunctions()Z")))
    private float disableMobControl(EntityLivingBase entityIn)
    {
        if (Mup.config.mc64836.enabled && !(entityIn instanceof EntityPlayer))
        {
            return 0.0F;
        }
        
        return entityIn.moveForward;
    }
}
