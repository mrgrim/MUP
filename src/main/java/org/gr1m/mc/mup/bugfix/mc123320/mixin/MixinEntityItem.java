package org.gr1m.mc.mup.bugfix.mc123320.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(EntityItem.class)
public abstract class MixinEntityItem extends Entity {

    public MixinEntityItem(World worldIn) {
        super(worldIn);
    }

    // If we have MC-4 fixed then enabling client side processing of items being pushed out of blocks is a good
    // compromise. This doesn't increase network traffic, but it does aggravate MC-4 symptoms.
    
    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isRemote:Z", opcode = Opcodes.GETFIELD, ordinal = 0),
              slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/EntityItem;hasNoGravity()Z", ordinal = 0),
                             to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/EntityItem;pushOutOfBlocks(DDD)Z", ordinal = 0)))
    private boolean clientPushOutOfBlocks(World world) {
        return !(Mup.config.mc123320.enabled && Mup.config.mc4.enabled) && world.isRemote;
    }
    
    // If the MC-4 fix is disabled then we correct the issue where change in acceleration instead of change in position
    // is being used to detect if an entity "isAirBorne" (isInMotion would be a better name for that). This will result
    // in many more movement packets being sent for items in motion.
    
    @ModifyVariable(method = "onUpdate", name = "d3", at = @At(value = "STORE", ordinal = 0, opcode = Opcodes.DSTORE),
                    slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/EntityItem;handleWaterMovement()Z", ordinal = 0),
                                   to = @At(value = "FIELD", target = "Lnet/minecraft/entity/item/EntityItem;isAirBorne:Z", opcode = Opcodes.PUTFIELD, ordinal = 0)))
    private double cancelPrevMotionX(double x)
    {
        return (Mup.config.mc123320.enabled && !Mup.config.mc4.enabled) ? this.motionX : x;
    }

    @ModifyVariable(method = "onUpdate", name = "d4", at = @At(value = "STORE", ordinal = 0, opcode = Opcodes.DSTORE),
                    slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/EntityItem;handleWaterMovement()Z", ordinal = 0),
                                   to = @At(value = "FIELD", target = "Lnet/minecraft/entity/item/EntityItem;isAirBorne:Z", opcode = Opcodes.PUTFIELD, ordinal = 0)))
    private double cancelPrevMotionY(double y)
    {
        return (Mup.config.mc123320.enabled && !Mup.config.mc4.enabled) ? this.motionY : y;
    }
    
    @ModifyVariable(method = "onUpdate", name = "d5", at = @At(value = "STORE", ordinal = 0, opcode = Opcodes.DSTORE),
                    slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/EntityItem;handleWaterMovement()Z", ordinal = 0),
                                   to = @At(value = "FIELD", target = "Lnet/minecraft/entity/item/EntityItem;isAirBorne:Z", opcode = Opcodes.PUTFIELD, ordinal = 0)))
    private double cancelPrevMotionZ(double z)
    {
        return (Mup.config.mc123320.enabled && !Mup.config.mc4.enabled) ? this.motionZ : z;
    }
}