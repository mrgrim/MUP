package org.gr1m.mc.mup.bugfix.mc98153.mixin;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.management.PlayerList;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc98153.INetHandlerPlayServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerList.class)
public class MixinPlayerList
{
    // Forge specific
    @Redirect(method = "transferPlayerToDimension(Lnet/minecraft/entity/player/EntityPlayerMP;ILnet/minecraftforge/common/util/ITeleporter;)V",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetHandlerPlayServer;setPlayerLocation(DDDFF)V", ordinal = 0))
    private void doCapturePlayerLocation(NetHandlerPlayServer connection, double x, double y, double z, float yaw, float pitch)
    {
        connection.setPlayerLocation(x, y, z, yaw, pitch);

        if (Mup.config.mc98153.enabled)
        {
            ((INetHandlerPlayServer) connection).callCaptureCurrentPosition();
        }
    }
}
