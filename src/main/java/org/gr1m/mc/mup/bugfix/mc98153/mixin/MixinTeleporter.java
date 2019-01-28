package org.gr1m.mc.mup.bugfix.mc98153.mixin;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.Teleporter;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc98153.INetHandlerPlayServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Teleporter.class)
public abstract class MixinTeleporter
{
    @Redirect(method = "placeInExistingPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetHandlerPlayServer;setPlayerLocation(DDDFF)V"))
    private void doCapturePlayerLocation(NetHandlerPlayServer connection, double x, double y, double z, float yaw, float pitch)
    {
        connection.setPlayerLocation(x, y, z, yaw, pitch);
        
        if (Mup.config.mc98153.enabled)
        {
            ((INetHandlerPlayServer) connection).callCaptureCurrentPosition();
        }
    }
}
