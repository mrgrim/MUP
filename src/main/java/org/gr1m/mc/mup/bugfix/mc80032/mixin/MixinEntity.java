package org.gr1m.mc.mup.bugfix.mc80032.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class MixinEntity
{
    @Shadow
    public double posX;
    
    @Shadow
    public double posY;
    
    @Shadow
    public double posZ;
    
    @Redirect(method = "changeDimension(ILnet/minecraftforge/common/util/ITeleporter;)Lnet/minecraft/entity/Entity;",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;moveToBlockPosAndAngles(Lnet/minecraft/util/math/BlockPos;FF)V", ordinal = 1))
    private void doNotCenterOnBlock(Entity entityIn, BlockPos pos, float rotationYawIn, float rotationPitchIn)
    {
        // In the original method the BlockPos sent here is initialized with this, so the floored values should equal the BlockPos values
        // If they don't it means this is either an end portal teleport or a modded teleport, and in that case the entity position may
        // not be set correctly, so we set it.

        // For overworld or nether travel, however, the entity is set with more precision prior to this call, and we want to keep that rather
        // than re-center the entity.
        if (!Mup.config.mc80032.enabled ||
            (MathHelper.floor(this.posX) != pos.getX() || MathHelper.floor(this.posY) != pos.getY() || MathHelper.floor(this.posZ) != pos.getZ()))
        {
            entityIn.moveToBlockPosAndAngles(pos, rotationYawIn, rotationPitchIn);
        }
    }
}
