package org.gr1m.mc.mup.bugfix.mc98153.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketPlayer;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc98153.INetHandlerPlayServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer implements INetHandlerPlayServer
{
    @Shadow
    abstract void captureCurrentPosition();

    @Shadow
    public EntityPlayerMP player;

    public void callCaptureCurrentPosition()
    {
        this.captureCurrentPosition();
    }
    
    @Inject(method = "processPlayer", at = @At("HEAD"), cancellable = true)
    private void dontProcessDuringDimensionChange(CPacketPlayer packetIn, CallbackInfo ci)
    {
        if (this.player.getServerWorld().isCallingFromMinecraftThread() && Mup.config.mc98153.enabled && this.player.isInvulnerableDimensionChange())
        {
            ci.cancel();
        }
    }
}
