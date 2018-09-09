package org.gr1m.mc.mup.tweaks.profiler.mixin;

import net.minecraft.server.MinecraftServer;
import org.gr1m.mc.mup.tweaks.profiler.MupProfiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer
{
    @Inject(method = "tick", at = @At("HEAD"))
    private void startProfilerTick(CallbackInfo ci)
    {
        if (MupProfiler.tickHealthRequested != 0)
        {
            MupProfiler.startTickProfiling();
        }
    }
    
    @Inject(method = "tick", at = @At("TAIL"))
    private void stopProfilerTick(CallbackInfo ci)
    {
        if (MupProfiler.tickHealthRequested != 0)
        {
            //noinspection ConstantConditions
            MupProfiler.endTickProfiling((MinecraftServer)((Object)(this)));
        }
    }
    
    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkSystem;networkTick()V"))
    private void profileNetworkStart(CallbackInfo ci)
    {
        MupProfiler.startSection(null, "Network");
    }

    @Inject(method = "updateTimeLightAndEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerList;onTick()V", shift = At.Shift.AFTER))
    private void profileNetworkStop(CallbackInfo ci)
    {
        MupProfiler.endCurrentSection();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerList;saveAllPlayerData()V"))
    private void profileAutosaveStart(CallbackInfo ci)
    {
        MupProfiler.startSection(null, "Autosave");
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;saveAllWorlds(Z)V", shift = At.Shift.AFTER))
    private void profileAutosaveStop(CallbackInfo ci)
    {
        MupProfiler.endCurrentSection();
    }
}
