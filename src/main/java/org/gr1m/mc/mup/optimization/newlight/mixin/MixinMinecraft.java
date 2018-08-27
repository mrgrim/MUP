package org.gr1m.mc.mup.optimization.newlight.mixin;

import org.gr1m.mc.mup.optimization.newlight.IWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
	@Shadow
	@Final
	public Profiler mcProfiler;

	@Shadow
	public WorldClient world;

	@Inject(method = "runTick", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=levelRenderer"))
	private void preEndStartSectionLevelRenderer(CallbackInfo ci) {
		this.mcProfiler.endStartSection("lighting");
		((IWorld) this.world).getLightingEngine().procLightUpdates();
	}
}
