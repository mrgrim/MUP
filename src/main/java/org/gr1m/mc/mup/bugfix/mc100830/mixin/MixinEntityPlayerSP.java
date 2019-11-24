package org.gr1m.mc.mup.bugfix.mc100830.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc100830.network.CPacketVehicleMoveWithMotion;
import org.gr1m.mc.mup.bugfix.mc100830.network.MC100830PacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer
{
    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile)
    {
        super(worldIn, playerProfile);
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;getLowestRidingEntity()Lnet/minecraft/entity/Entity;"))
    private Entity sendCustomVehicleMovePacket(EntityPlayerSP that)
    {
        if (Mup.config.mc100830.enabled)
        {
            Entity entity = this.getLowestRidingEntity();

            if (entity != this && entity.canPassengerSteer())
            {
                MC100830PacketHandler.INSTANCE.sendToServer(new CPacketVehicleMoveWithMotion(entity));
            }
            
            return this;
        }
        else
        {
            return this.getLowestRidingEntity();
        }
    }    
}
