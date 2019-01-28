package org.gr1m.mc.mup.bugfix.mc80032.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class MixinEntity
{
    @Redirect(method = "changeDimension(ILnet/minecraftforge/common/util/ITeleporter;)Lnet/minecraft/entity/Entity;",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;moveToBlockPosAndAngles(Lnet/minecraft/util/math/BlockPos;FF)V", ordinal = 1))
    private void doNotCenterOnBlock(Entity entityIn, BlockPos pos, float rotationYawIn, float rotationPitchIn)
    {
        if (!Mup.config.mc80032.enabled)
        {
            entityIn.moveToBlockPosAndAngles(pos, rotationYawIn, rotationPitchIn);
        }
    }
}
