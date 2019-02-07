package org.gr1m.mc.mup.bugfix.mc2025.mixin;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.config.MupConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity implements ICommandSender {
    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;

    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();
    
    @Shadow
    protected abstract NBTTagList newDoubleNBTList(double... numbers);
    
    @Shadow
    public abstract void setEntityBoundingBox(AxisAlignedBB bb);
    
    @Shadow
    protected abstract boolean shouldSetPosAfterLoading();
    
    @Shadow
    public abstract void setPosition(double x, double y, double z);
    
    @Inject(method = "writeToNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setUniqueId(Ljava/lang/String;Ljava/util/UUID;)V", shift = At.Shift.AFTER, ordinal = 0))
    private void saveAABBToNBT(NBTTagCompound compound, CallbackInfoReturnable<NBTTagCompound> ci)
    {
        if (Mup.config.mc2025.enabled)
        {
            AxisAlignedBB aabb = this.getEntityBoundingBox();
            compound.setTag("AABB", this.newDoubleNBTList(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ));
        }
    }
    
    @Redirect(method = "readFromNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;shouldSetPosAfterLoading()Z"))
    private boolean readAABBFromNBT(Entity entity, NBTTagCompound compound)
    {
        if (this.shouldSetPosAfterLoading())
        {
            this.setPosition(this.posX, this.posY, this.posZ);
        }
        
        if (Mup.config.mc2025.enabled && compound.hasKey("AABB", 9))
        {
            NBTTagList aabb = compound.getTagList("AABB", 6);
            this.setEntityBoundingBox(new AxisAlignedBB(aabb.getDoubleAt(0), aabb.getDoubleAt(1), aabb.getDoubleAt(2), aabb.getDoubleAt(3), aabb.getDoubleAt(4), aabb.getDoubleAt(5)));
        }
        
        return false;
    }
}