package org.gr1m.mc.mup.bugfix.mc119971.mixin;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.RegionFileCache;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Mixin(AnvilChunkLoader.class)
public abstract class MixinAnvilChunkLoader {
    @Shadow
    private void writeChunkData(ChunkPos pos, NBTTagCompound compound) throws IOException { }
    
    @Shadow
    @Final
    public File chunkSaveLocation;
    
    @Shadow
    @Final
    private static Logger LOGGER;
    
    @Shadow
    private boolean flushing;

    /* --- new data structures for MC-119971 --- */

    // New data structures don't need to be concurrent since we're doing our
    // own mutex.  Using the concurrent ones would now just be extra overhead.
    @Shadow
    private final Map<ChunkPos, NBTTagCompound> chunksToSave = new HashMap();

    // Currently there will never be more than one chunk being written at
    // one time, but this is convenient and leaves open the future option.
    private final Map<ChunkPos, NBTTagCompound> chunksInWrite = new HashMap();

    /* --- new synchronized methods for MC-119971 --- */

    // Insert new chunk into pending queue, replacing any older one
    // at the same position
    synchronized private void queueChunkToSave(ChunkPos pos, NBTTagCompound data) {
        chunksToSave.put(pos, data);

        // No need to check chunksInWrite.  It may contain an older one
        // at this location, but what matters is not losing the new one.
        // This fixes Rich Crosby's bug.
    }

    // Fetch another chunk to save to disk and atomically move it into 
    // the queue of chunk(s) being written.
    synchronized private Map.Entry<ChunkPos, NBTTagCompound> fetchChunkToWrite() {
        if (chunksToSave.isEmpty()) return null;

        // Pick an entry in chunksToRemove and remove it from the collection
        Set<Map.Entry<ChunkPos, NBTTagCompound>> entrySet = chunksToSave.entrySet();
        Iterator<Map.Entry<ChunkPos, NBTTagCompound>> iter = entrySet.iterator();
        Map.Entry<ChunkPos, NBTTagCompound> entry = iter.next();
        iter.remove();

        // Indicate that this entry is going to be written out now
        chunksInWrite.put(entry.getKey(), entry.getValue());

        return entry;
    }

    // Once the write for a chunk is completely committed to disk,
    // this method discards it
    synchronized private void retireChunkToWrite(ChunkPos pos, NBTTagCompound data) {
        chunksInWrite.remove(pos);
    }

    // Check these data structures for a chunk being reloaded
    synchronized private NBTTagCompound reloadChunkFromSaveQueues(ChunkPos pos) {
        // If this chunk is queued at all, the most recent version will be in
        // chunksToRemove.  
        NBTTagCompound data = chunksToSave.get(pos);

        // Note:  The above line fetches the chunk but leaves it in the
        // queue to be saved.  This is the original behavior and probably
        // safest in terms of avoiding data loss on a crash.  However, if 
        // we wanted to *cancel* the save, replace 'get' with 'remove'.

        // If we found the chunk return it
        if (data != null) return data;

        // Otherwise, check in chunksInWrite.  This is what fixes
        // MC-119971.
        return chunksInWrite.get(pos);
    }

    // Check if chunk exists at all in any pending save state
    synchronized private boolean chunkExistInSaveQueues(ChunkPos pos) {
        return chunksToSave.containsKey(pos) || chunksInWrite.containsKey(pos);
    }

    /* --- end of new code for MC-119971 --- */

    @Redirect(method = "loadChunk__Async", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0, remap = false), remap = false)
    private Object pullChunkToSave(Map lChunksToSave, Object lpos)
    {
        return this.reloadChunkFromSaveQueues((ChunkPos)lpos);
    }
    
    @Inject(method = "isChunkGeneratedAt", at = @At("HEAD"), cancellable = true)
    private void overrideIsChunkGeneratedAt(int x, int z, CallbackInfoReturnable<Boolean> ci)
    {
        ChunkPos chunkpos = new ChunkPos(x, z);
        boolean exists = chunkExistInSaveQueues(chunkpos);
        ci.setReturnValue(exists || RegionFileCache.chunkExists(this.chunkSaveLocation, x, z));
    }
    
    @Redirect(method = "addChunkToPending", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z", remap = false))
    private boolean overrideAddChunkToPending(Set lChunksBeingSaved, Object lPos, ChunkPos pos, NBTTagCompound compound)
    {
        this.queueChunkToSave(pos, compound);
        return true;
    }

    @Inject(method = "writeNextIO", at = @At("HEAD"), cancellable = true)
    private void overrideWriteNextIO(CallbackInfoReturnable<Boolean> ci)
    {
        // New for MC-119971
        // Try to fetch a pending chunk
        Map.Entry<ChunkPos, NBTTagCompound> entry = this.fetchChunkToWrite();
        if (entry == null)
        {
            // If none left, here's code for some message that will never
            // be executed since there is no "extra data."
            if (this.flushing)
            {
                LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", new Object[] {this.chunkSaveLocation.getName()});
            }

            ci.setReturnValue(false);
            return;
        }

        // New for MC-119971
        ChunkPos chunkpos = entry.getKey();
        NBTTagCompound nbttagcompound = entry.getValue();

        try
        {
            this.writeChunkData(chunkpos, nbttagcompound);
        }
        catch (Exception exception)
        {
            LOGGER.error((String)"Failed to save chunk", exception);
        }

        // New for MC-119971
        // Now that the chunk is fully committed to disk and any
        // load would now get it from the RegionFile, we can 
        // retire this chunk from the chunkloader data structures.
        this.retireChunkToWrite(chunkpos, nbttagcompound);

        ci.setReturnValue(true);
    }
}
