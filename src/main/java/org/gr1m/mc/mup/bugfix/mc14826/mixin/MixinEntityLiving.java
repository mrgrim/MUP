package org.gr1m.mc.mup.bugfix.mc14826.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc14826.ILeashBackRef;
import org.gr1m.mc.mup.bugfix.mc14826.ILeashSaver;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving extends EntityLivingBase implements ILeashSaver
{
    @Shadow
    private Entity leashHolder;
    
    @Shadow
    private NBTTagCompound leashNBTTag;

    @Shadow public abstract void clearLeashed(boolean sendPacket, boolean dropLead);

    public MixinEntityLiving(World worldIn)
    {
        super(worldIn);
    }
    
    @Inject(method = "writeEntityToNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setBoolean(Ljava/lang/String;Z)V", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=Leash"), to = @At(value = "CONSTANT", args = "stringValue=DeathLootTable")))
    private void saveLeashNBT(NBTTagCompound compound, CallbackInfo ci)
    {
        if (Mup.config.mc14826.enabled && this.leashHolder == null && this.leashNBTTag != null)
        {
            compound.setTag("Leash", leashNBTTag);
        }
    }
    
    @Inject(method = "clearLeashed", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLiving;leashHolder:Lnet/minecraft/entity/Entity;",
                                              ordinal = 0, opcode = Opcodes.PUTFIELD))
    private void clearLeashBackRef(boolean sendPacket, boolean dropLead, CallbackInfo ci)
    {
        if (Mup.config.mc14826.enabled && this.leashHolder != null)
        {
            ((ILeashBackRef)this.leashHolder).setLeashBackRef(null);
        }
    }

    @Inject(method = "setLeashHolder", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLiving;leashHolder:Lnet/minecraft/entity/Entity;",
                                                ordinal = 0, opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void setLeashBackRef(Entity entityIn, boolean sendAttachNotification, CallbackInfo ci)
    {
        if (Mup.config.mc14826.enabled && this.leashHolder != null)
        {
            ((ILeashBackRef)entityIn).setLeashBackRef(this);
        }
    }
    
    public void onRemovedFromWorld()
    {
        super.onRemovedFromWorld();
        
        if (Mup.config.mc14826.enabled && this.leashHolder != null)
        {
            ((ILeashBackRef)(this.leashHolder)).setLeashBackRef(null);
        }
    }
    
    public void convertLeashToNBT()
    {
        if (this.leashHolder != null && this.leashNBTTag == null)
        {
            this.leashNBTTag = new NBTTagCompound();

            if (this.leashHolder instanceof EntityLivingBase)
            {
                UUID uuid = this.leashHolder.getUniqueID();
                this.leashNBTTag.setUniqueId("UUID", uuid);
            }
            else if (this.leashHolder instanceof EntityHanging)
            {
                BlockPos blockpos = ((EntityHanging)this.leashHolder).getHangingPosition();
                this.leashNBTTag.setInteger("X", blockpos.getX());
                this.leashNBTTag.setInteger("Y", blockpos.getY());
                this.leashNBTTag.setInteger("Z", blockpos.getZ());
            }
            else
            {
                this.leashNBTTag = null;
                this.clearLeashed(true, true);
            }
            
            this.leashHolder = null;
        }
    }
}
