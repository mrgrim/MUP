package org.gr1m.mc.mup.optimization.newlight.mixins;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal {
	@Shadow
	protected abstract void markBlocksForUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean updateImmediately);

	@Inject(method = "notifyLightSet", at = @At("HEAD"), cancellable = true)
	private void onNotifyLightSet(BlockPos pos, CallbackInfo ci) {
		ci.cancel();
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		this.markBlocksForUpdate(i - 1, j - 1, k - 1, i + 1, j + 1, k + 1, false); // Forge: Process immediately. Fixes MC-91136
	}
}
