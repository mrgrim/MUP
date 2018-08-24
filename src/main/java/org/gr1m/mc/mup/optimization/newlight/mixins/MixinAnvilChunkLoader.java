package org.gr1m.mc.mup.optimization.newlight.mixins;

import org.gr1m.mc.mup.optimization.newlight.LightingHooks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AnvilChunkLoader.class)
public abstract class MixinAnvilChunkLoader {
	@Inject(method = "writeChunkToNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setTag(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V", ordinal = 0, shift = At.Shift.AFTER))
	private void postSetTagSections(Chunk chunkIn, World worldIn, NBTTagCompound compound, CallbackInfo ci) {
		LightingHooks.writeLightData(chunkIn, compound);
	}

	@Inject(method = "readChunkFromNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setStorageArrays([Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void postSetStorageArrays(World worldIn, NBTTagCompound compound, CallbackInfoReturnable<Chunk> cir, int i, int k, Chunk chunk) {
		LightingHooks.readLightData(chunk, compound);
	}
}
