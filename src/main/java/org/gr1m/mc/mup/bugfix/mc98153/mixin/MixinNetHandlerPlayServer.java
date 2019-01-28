package org.gr1m.mc.mup.bugfix.mc98153.mixin;

import net.minecraft.network.NetHandlerPlayServer;
import org.gr1m.mc.mup.bugfix.mc98153.INetHandlerPlayServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer implements INetHandlerPlayServer
{
    @Shadow
    abstract void captureCurrentPosition();
    
    public void callCaptureCurrentPosition()
    {
        this.captureCurrentPosition();
    }
}
