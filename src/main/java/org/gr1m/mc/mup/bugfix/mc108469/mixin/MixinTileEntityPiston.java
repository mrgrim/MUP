package org.gr1m.mc.mup.bugfix.mc108469.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(TileEntityPiston.class)
public abstract class MixinTileEntityPiston extends TileEntity
{
    // Nasty local var list.. unavoidable, tho :/
    @Inject(method = "moveCollidedEntities", at = @At(value = "INVOKE", target = "Ljava/lang/ThreadLocal;set(Ljava/lang/Object;)V", ordinal = 1, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void updateEntity(float p_184322_1_, CallbackInfo ci, EnumFacing enumfacing, double d0, List list, AxisAlignedBB axisalignedbb, List list1, boolean flag, int i, Entity entity, double d1)
    {
        if (Mup.config.mc108469.enabled) world.updateEntityWithOptionalForce(entity, false);
    }

    // Quark modifies the LVT adding an extra integer of unknown purpose
    @Surrogate
    public void updateEntity(float p_184322_1_, CallbackInfo ci, EnumFacing enumfacing, double d0, List list, AxisAlignedBB axisalignedbb, List list1, boolean flag, int i, Entity entity, double d1, int quark0)
    {
        if (Mup.config.mc108469.enabled) world.updateEntityWithOptionalForce(entity, false);
    }

}

