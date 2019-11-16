package org.gr1m.mc.mup.bugfix.mc108469.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(World.class)
public abstract class MixinWorld
{
    @Redirect(method = "updateEntityWithOptionalForce",
              slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getChunk(II)Lnet/minecraft/world/chunk/Chunk;", ordinal = 0),
                             to = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getChunk(II)Lnet/minecraft/world/chunk/Chunk;", ordinal = 1)),
              at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setPositionNonDirty()Z", ordinal = 0))
    public boolean alwaysLoadChunk(Entity entityIn)
    {
         if (Mup.config.mc108469.enabled)
         {
             return true;
         }
         else
         {
             // As far as I can tell the flag this function sets is used nowhere and this function always returns false
             return entityIn.setPositionNonDirty();
         }
    }
}
