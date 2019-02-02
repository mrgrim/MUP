package org.gr1m.mc.mup.optimization.newlight.mixin;

import org.gr1m.mc.mup.optimization.newlight.IChunk;
import org.gr1m.mc.mup.optimization.newlight.IWorld;
import org.gr1m.mc.mup.optimization.newlight.LightingHooks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.minecraft.world.chunk.Chunk.NULL_BLOCK_STORAGE;

@Mixin(Chunk.class)
public abstract class MixinChunk implements IChunk {
	private short[] neightborLightChecks = null;
	private short pendingNeighborLightInits;
	private int copyOfJ;
	private int copyOfK;
	@Shadow
	@Final
	private int[] heightMap;
	@Shadow
	@Final
	private World world;
	@Shadow
	private int heightMapMinimum;
	@Shadow
	@Final
	private ExtendedBlockStorage[] storageArrays;
	@Shadow
	private boolean isTerrainPopulated;

	@Shadow
	public abstract boolean canSeeSky(BlockPos pos);

	@Shadow
	protected abstract int getBlockLightOpacity(int x, int y, int z);

	// Since we can't use LocalCapture directly in @Redirected methods we'll simply make a copy of them for ourselves.
	@Inject(method = "generateSkylightMap", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;hasSkyLight()Z"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void setJAndKFields(CallbackInfo ci, int i, int j, int k) {
		this.copyOfJ = j;
		this.copyOfK = k;
	}

	@Redirect(method = "generateSkylightMap", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;hasSkyLight()Z"))
	private boolean callFillSkylightColumnInWorldsWithSkylight(WorldProvider worldProvider) {
		if (this.world.provider.hasSkyLight()) {
			LightingHooks.fillSkylightColumn((Chunk) (Object) this, this.copyOfJ, this.copyOfK);
		}
		return false;
	}

	// Soft override the method since there isn't really a "clean" way to do it with Mixins.
	@Inject(method = "relightBlock", at = @At("HEAD"), cancellable = true)
	private void onRelightBlock(int x, int y, int z, CallbackInfo ci) {
		ci.cancel();
		int i = this.heightMap[z << 4 | x];
		int j = i;

		if (y > i) {
			j = y;
		}

		while (j > 0 && this.getBlockLightOpacity(x, j - 1, z) == 0) {
			--j;
		}

		if (j != i) {
			this.heightMap[z << 4 | x] = j;

			if (this.world.provider.hasSkyLight()) {
				LightingHooks.relightSkylightColumn(this.world, (Chunk) (Object) this, x, z, i, j); // Forge: Optimized version of World.markBlocksDirtyVertical; heightMap is now updated (See #3871)
			}

			int l1 = this.heightMap[z << 4 | x];
			if (l1 < this.heightMapMinimum) {
				this.heightMapMinimum = l1;
			}
		}
	}

	@Redirect(method = "setBlockState", at = @At(value = "NEW", target = "net/minecraft/world/chunk/storage/ExtendedBlockStorage"))
	private ExtendedBlockStorage onSetBlockState(final int y, final boolean storeSkylight)
	{
		final ExtendedBlockStorage extendedblockstorage = new ExtendedBlockStorage(y, storeSkylight);
		LightingHooks.initSkylightForSection(this.world, (Chunk) (Object) this, extendedblockstorage); //Forge: Always initialize sections properly (See #3870 and #3879)
		return extendedblockstorage;
	}

	@ModifyVariable(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;set(IIILnet/minecraft/block/state/IBlockState;)V", ordinal = 0))
	private boolean setFlagToFalse(boolean flag) {
		return false;
	}

	@Redirect(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;propagateSkylightOcclusion(II)V"))
	private void cancelPropagateSkylightOcclusion(Chunk chunk, int x, int z) {
	}

	@Redirect(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getLightFor(Lnet/minecraft/world/EnumSkyBlock;Lnet/minecraft/util/math/BlockPos;)I"))
	public int cancelGetLightFor(Chunk chunk, EnumSkyBlock type, BlockPos pos) {
		return 0;
	}
	
	@Inject(method = "getLightFor", at = @At("HEAD"), cancellable = true)
	private void onGetLightFor(EnumSkyBlock type, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		((IWorld) this.world).getLightingEngine().procLightUpdates(type);
		cir.setReturnValue(this.getCachedLightFor(type, pos));
	}

	public int getCachedLightFor(EnumSkyBlock type, BlockPos pos) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		ExtendedBlockStorage extendedblockstorage = this.storageArrays[j >> 4];

		if (extendedblockstorage == NULL_BLOCK_STORAGE) {
			return this.canSeeSky(pos) ? type.defaultLightValue : 0;
		} else if (type == EnumSkyBlock.SKY) {
			return !this.world.provider.hasSkyLight() ? 0 : extendedblockstorage.getSkyLight(i, j & 15, k);
		} else {
			return type == EnumSkyBlock.BLOCK ? extendedblockstorage.getBlockLight(i, j & 15, k) : type.defaultLightValue;
		}
	}

	// TODO: Give better name.
	@Redirect(method = "setLightFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;generateSkylightMap()V"))
	private void weCallThisElsewhere(Chunk chunk) {
	}

	// TODO: Give better name.
	@Inject(method = "setLightFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;generateSkylightMap()V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void insteadOfGenerateSkylightMap(EnumSkyBlock type, BlockPos pos, int value, CallbackInfo ci, int i, int j, int k, ExtendedBlockStorage extendedblockstorage) {
		LightingHooks.initSkylightForSection(this.world, (Chunk) (Object) this, extendedblockstorage); //Forge: generateSkylightMap produces the wrong result (See #3870)
	}

	@Inject(method = "getLightSubtracted", at = @At("HEAD"))
	private void onGetLightSubtracted(BlockPos pos, int amount, CallbackInfoReturnable<Integer> cir) {
		((IWorld) this.world).getLightingEngine().procLightUpdates();
	}

	@Inject(method = "onLoad", at = @At("RETURN"))
	private void postOnLoad(CallbackInfo ci) {
		LightingHooks.onLoad(this.world, (Chunk) (Object) this);
	}

	@Redirect(method = "populate(Lnet/minecraft/world/gen/IChunkGenerator;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;checkLight()V"))
	protected void noPopulateCheckLight(Chunk chunk)
	{
		this.isTerrainPopulated = true;
	}
	
	@Redirect(method = "onTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;checkLight()V"))
	private void onCheckLight(Chunk chunk) {
	}
	
	@Inject(method = "propagateSkylightOcclusion", at = @At("HEAD"), cancellable = true)
    private void nullPropagateSkylightOcclusion(int x, int z, CallbackInfo ci)
    {
        ci.cancel();
        
        // This is a workaround for Recurrent Complex's height map freezer which ends up calling vanilla lighting methods directly.
        LightingHooks.fillSkylightColumn((Chunk)(Object)(this), x, z);
    }

	public short[] getNeighborLightChecks() {
		return this.neightborLightChecks;
	}

	public void setNeighborLightChecks(short[] in) {
		this.neightborLightChecks = in;
	}

	public short getPendingNeighborLightInits() {
		return this.pendingNeighborLightInits;
	}

	public void setPendingNeighborLightInits(short in) {
		this.pendingNeighborLightInits = in;
	}
}
