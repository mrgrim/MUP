package org.gr1m.mc.mup.bugfix.mc92916.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.WorldServer;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc92916.IWorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList
{
    // This one is enough for vanilla.
    @Redirect(method = "transferEntityToWorld(Lnet/minecraft/entity/Entity;ILnet/minecraft/world/WorldServer;Lnet/minecraft/world/WorldServer;Lnet/minecraftforge/common/util/ITeleporter;)V",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;updateEntityWithOptionalForce(Lnet/minecraft/entity/Entity;Z)V"),
              slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=moving"), to = @At(value = "CONSTANT", args = "stringValue=placing")))
    public void doPrepareLeaveDimension(WorldServer worldIn, Entity entityIn, boolean forceUpdate)
    {
        if (Mup.config.mc92916.enabled)
        {
            ((IWorldServer) worldIn).prepareLeaveDimension(entityIn);
        }
        else
        {
            worldIn.updateEntityWithOptionalForce(entityIn, forceUpdate);
        }
    }

    // This is needed for Forge
    @Redirect(method = "transferEntityToWorld(Lnet/minecraft/entity/Entity;ILnet/minecraft/world/WorldServer;Lnet/minecraft/world/WorldServer;Lnet/minecraftforge/common/util/ITeleporter;)V",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;updateEntityWithOptionalForce(Lnet/minecraft/entity/Entity;Z)V", ordinal = 0),
              slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=placing"),
                             to = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/util/ITeleporter;placeEntity(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;F)V", remap = false)))
    public void doPrepareLeaveDimensionForge(WorldServer worldIn, Entity entityIn, boolean forceUpdate)
    {
        if (Mup.config.mc92916.enabled)
        {
            ((IWorldServer) worldIn).prepareLeaveDimension(entityIn);
        }
        else
        {
            worldIn.updateEntityWithOptionalForce(entityIn, forceUpdate);
        }
    }
}