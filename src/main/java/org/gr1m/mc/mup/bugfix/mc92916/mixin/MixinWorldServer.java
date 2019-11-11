package org.gr1m.mc.mup.bugfix.mc92916.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.gr1m.mc.mup.bugfix.mc92916.IWorldServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends World implements IWorldServer
{
    public MixinWorldServer(MinecraftServer server, ISaveHandler saveHandlerIn, WorldInfo info, int dimensionId, Profiler profilerIn)
    {
        super(saveHandlerIn, info, DimensionType.getById(dimensionId).createDimension(), profilerIn, false);
    }
    
    public void prepareLeaveDimension(Entity entityIn)
    {
        entityIn.lastTickPosX = entityIn.posX;
        entityIn.lastTickPosY = entityIn.posY;
        entityIn.lastTickPosZ = entityIn.posZ;
        entityIn.prevRotationYaw = entityIn.rotationYaw;
        entityIn.prevRotationPitch = entityIn.rotationPitch;

        // This is already done with the call to World.removeEntityDangerously in PlayerList.transferPlayerToDimension
        
        /*
        if (entityIn.addedToChunk && this.isChunkLoaded(entityIn.chunkCoordX, entityIn.chunkCoordZ, true))
        {
            this.getChunk(entityIn.chunkCoordX, entityIn.chunkCoordZ).removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
            entityIn.addedToChunk = false;
        }
        */
    }
}
