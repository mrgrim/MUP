package org.gr1m.mc.mup.optimization.newlight.mixin;

import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.gr1m.mc.mup.optimization.newlight.LightingHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(Chunk.class)
public abstract class MixinChunkClient
{
    @Shadow
    protected abstract void generateHeightMap();

    @Shadow
    @Final
    private int[] heightMap;

    @Shadow
    @Final
    private World world;

    // TODO: Give better name.
    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;generateHeightMap()V"))
    private void weAlsoCallThisElsewhere(Chunk chunk) {
    }

    @Inject(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;generateHeightMap()V", shift = At.Shift.AFTER))
    private void preGenerateHeightMap(PacketBuffer buf, int availableSections, boolean groundUpContinuous, CallbackInfo ci) {
        final int[] oldHeightMap = groundUpContinuous ? null : Arrays.copyOf(heightMap, heightMap.length);
        this.generateHeightMap();
        LightingHooks.relightSkylightColumns(world, (Chunk) (Object) this, oldHeightMap);
    }
}
