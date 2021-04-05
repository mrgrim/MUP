package org.gr1m.mc.mup.tweaks.profiler.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import org.gr1m.mc.mup.tweaks.profiler.MupProfiler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("ConstantConditions")
@Mixin(World.class)
public abstract class MixinWorld
{
    @Shadow
    @Final
    public WorldProvider provider;
    
    @Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Ljava/util/List;removeAll(Ljava/util/Collection;)Z", ordinal = 0, remap = false),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=remove", ordinal = 0), to = @At(value = "CONSTANT", args = "stringValue=regular")))
    private void profileEntitiesStart(CallbackInfo ci)
    {
        if ((World)((Object)(this)) instanceof WorldServer)
        {
            String world_name = this.provider.getDimensionType().getName();
            MupProfiler.startSection(world_name, "entities");
        }
    }
    
    @Redirect(method = "updateEntities", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 0, remap = false),
              slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/World;loadedEntityList:Ljava/util/List;", ordinal = 1),
                             to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getRidingEntity()Lnet/minecraft/entity/Entity;", ordinal = 0)))
    private Object profileEntityStart(List<Entity> entityList, int index)
    {
        Entity entity = entityList.get(index);

        if ((World)((Object)(this)) instanceof WorldServer)
        {
            MupProfiler.startEntitySection(this.provider.getDimensionType().getName(), entity);
        }

        return entity;
    }
    
    @Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V", ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;onEntityRemoved(Lnet/minecraft/entity/Entity;)V", ordinal = 1),
                           to = @At(value = "CONSTANT", args = "stringValue=blockEntities", ordinal = 0)))
    private void profilerEntityStop(CallbackInfo ci )
    {
        if ((World)((Object)(this)) instanceof WorldServer)
        {
            MupProfiler.endCurrentEntitySection();
        }
    }

    @Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=remove", ordinal = 1),
                           to = @At(value = "FIELD", target = "Lnet/minecraft/world/World;tileEntitiesToBeRemoved:Ljava/util/List;", ordinal = 0)))
    private void profileEntitiesStopTileEntitiesStart(CallbackInfo ci)
    {
        if ((World)((Object)(this)) instanceof WorldServer)
        {
            MupProfiler.endCurrentSection();
            String world_name = this.provider.getDimensionType().getName();
            MupProfiler.startSection(world_name, "tileentities");
        }
    }
    
    @Redirect(method = "updateEntities", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;", ordinal = 0, remap = false),
              slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/World;tickableTileEntities:Ljava/util/List;", ordinal = 1),
                             to = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;hasWorld()Z")))
    private Object profileTileEntityStart(Iterator<TileEntity> iter)
    {
        TileEntity tileEntity = iter.next();

        if ((World)((Object)(this)) instanceof WorldServer)
        {
            MupProfiler.startTileEntitySection(this.provider.getDimensionType().getName(), tileEntity);
        }
        
        return tileEntity;
    }

    @Inject(method = "updateEntities", at = @At(value = "JUMP", opcode = Opcodes.GOTO, shift = At.Shift.BEFORE),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;removeTileEntity(Lnet/minecraft/util/math/BlockPos;)V"),
                    to = @At(value = "CONSTANT", args = "stringValue=pendingBlockEntities")))
    private void profileTileEntityStop(CallbackInfo ci)
    {
        if ((World)((Object)(this)) instanceof WorldServer)
        {
            MupProfiler.endCurrentEntitySection();
        }
    }
    
    @Inject(method = "updateEntities", at = @At("TAIL"))
    private void profileTileEntitiesStop(CallbackInfo ci)
    {
        if ((World)((Object)(this)) instanceof WorldServer)
        {
            MupProfiler.endCurrentSection();
        }
    }
}
