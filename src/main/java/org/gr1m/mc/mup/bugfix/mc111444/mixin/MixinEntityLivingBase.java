package org.gr1m.mc.mup.bugfix.mc111444.mixin;

import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity
{
    public MixinEntityLivingBase(World worldIn)
    {
        super(worldIn);
    }

    @Redirect(method = "travel", at = @At(value = "FIELD", ordinal = 1,
                                          target = "Lnet/minecraft/world/World;isRemote:Z"))
    private boolean fixElytraLanding(World world)
    {
        return world.isRemote && (Mup.config.mc111444.enabled == false || ((Object) this instanceof EntityPlayerSP) == false);
    }
}