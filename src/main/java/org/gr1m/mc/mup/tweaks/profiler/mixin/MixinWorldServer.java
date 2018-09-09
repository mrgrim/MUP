package org.gr1m.mc.mup.tweaks.profiler.mixin;

import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.gr1m.mc.mup.tweaks.profiler.MupProfiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends World
{
    @Shadow
    public abstract boolean tickUpdates(boolean runAllPending);
    
    @Shadow
    protected abstract void updateBlocks();
    
    public MixinWorldServer(ISaveHandler saveHandlerIn, WorldInfo info, int dimensionId, Profiler profilerIn)
    {
        super(saveHandlerIn, info, net.minecraftforge.common.DimensionManager.createProviderFor(dimensionId), profilerIn, false);
    }
    
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldEntitySpawner;findChunksForSpawning(Lnet/minecraft/world/WorldServer;ZZZ)I", ordinal = 0))
    private int profileSpawning(WorldEntitySpawner spawner, WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate)
    {
        MupProfiler.startSection(this.provider.getDimensionType().getName(), "spawning");
        int ret = spawner.findChunksForSpawning(worldServerIn, spawnHostileMobs, spawnPeacefulMobs, spawnOnSetTickRate);
        MupProfiler.endCurrentSection();
        return ret;
    }
    
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;tickUpdates(Z)Z", ordinal = 0))
    private boolean profileBlocksScheduled(WorldServer thisIn, boolean runAllPending)
    {
        MupProfiler.startSection(this.provider.getDimensionType().getName(), "blocks");
        boolean ret = this.tickUpdates(runAllPending);
        MupProfiler.endCurrentSection();
        return ret;
    }
    
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;updateBlocks()V", ordinal = 0))
    private void profileBlocksRandom(WorldServer thisIn)
    {
        MupProfiler.startSection(this.provider.getDimensionType().getName(), "blocks");
        this.updateBlocks();
        MupProfiler.endCurrentSection();
    }
}
