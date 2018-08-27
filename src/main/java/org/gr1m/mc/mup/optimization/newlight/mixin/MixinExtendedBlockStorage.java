package org.gr1m.mc.mup.optimization.newlight.mixins;

import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExtendedBlockStorage.class)
public abstract class MixinExtendedBlockStorage {
	@Inject(method = "isEmpty", at = @At("HEAD"), cancellable = true)
	private void onIsEmpty(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(false);
	}
}
