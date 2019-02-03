package org.gr1m.mc.mup.bugfix.mc14826.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityAttach;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc14826.ILeashBackRef;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTrackerEntry.class)
public abstract class MixinEntityTrackerEntry
{
    @Shadow
    @Final
    private Entity trackedEntity;

    @Inject(method = "updatePlayerEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetHandlerPlayServer;sendPacket(Lnet/minecraft/network/Packet;)V", shift = At.Shift.AFTER, ordinal = 0))
    private void sendLeashPacket(EntityPlayerMP playerMP, CallbackInfo ci)
    {
        if(Mup.config.mc14826.enabled)
        {
            if (this.trackedEntity instanceof EntityLiving && ((EntityLiving)this.trackedEntity).getLeashed())
            {
                playerMP.connection.sendPacket(new SPacketEntityAttach(this.trackedEntity, ((EntityLiving) this.trackedEntity).getLeashHolder()));
            }
            
            if (this.trackedEntity instanceof EntityLeashKnot && ((ILeashBackRef)(this.trackedEntity)).getLeashBackRef() != null)
            {
                playerMP.connection.sendPacket(new SPacketEntityAttach(((ILeashBackRef)(this.trackedEntity)).getLeashBackRef(), this.trackedEntity));
            }
        }
    }
}
