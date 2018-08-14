package org.gr1m.mc.mup.bugfix.mc54026.mixin;

import net.minecraft.world.WorldServer;
import org.gr1m.mc.mup.bugfix.mc54026.IWorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer implements IWorldServer {
    private boolean blockActionsProcessed;
    
    @Inject(method ="tick", at = @At("HEAD"))
    private void resetBlockActionsProcessed(CallbackInfo ci)
    {
        this.blockActionsProcessed = false;
    }
    
    @Inject(method = "sendQueuedBlockEvents", at = @At("RETURN"))
    private void setBlockActionsProcessed(CallbackInfo ci)
    {
        this.blockActionsProcessed = true;
    }
    
    public boolean haveBlockActionsProcessed()
    {
        return this.blockActionsProcessed;
    }
}
