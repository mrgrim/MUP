package org.gr1m.mc.mup.optimization.newlight.mixins;

import org.gr1m.mc.mup.optimization.newlight.IWorld;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends World {
	protected MixinWorldServer(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
		super(saveHandlerIn, info, providerIn, profilerIn, client);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Teleporter;removeStalePortalLocations(J)V", shift = At.Shift.AFTER))
	private void onTick(CallbackInfo ci) {
		this.profiler.endStartSection("lighting");
		((IWorld) this).getLightingEngine().procLightUpdates();
	}
}
