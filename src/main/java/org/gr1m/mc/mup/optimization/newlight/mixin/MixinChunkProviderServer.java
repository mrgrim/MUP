package org.gr1m.mc.mup.optimization.newlight.mixin;

import org.gr1m.mc.mup.optimization.newlight.IWorld;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer {
	@Shadow
	@Final
	public WorldServer world;

	@Inject(method = "saveChunks", at = @At("HEAD"))
	private void onSaveChunks(boolean all, CallbackInfoReturnable<Boolean> cir) {
		((IWorld) this.world).getLightingEngine().procLightUpdates();
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Set;isEmpty()Z", remap = false))
	private boolean foo(Set set) {
		final boolean isEmpty = set.isEmpty();
		if (!isEmpty) {
			((IWorld) this.world).getLightingEngine().procLightUpdates();
		}
		return isEmpty;
	}
}
