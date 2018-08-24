package org.gr1m.mc.mup.bugfix.mc73051.mixin;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.structure.StructureStart;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureStart.class)
public abstract class MixinStructureStart
{
    @Shadow
    protected abstract void updateBoundingBox();
    
    @Inject(method = "writeStructureComponentsToNBT", at = @At(value = "CONSTANT", args = "stringValue=Children", ordinal = 0))
    public void callUpdateBoundingBox(int chunkX, int chunkZ, CallbackInfoReturnable<NBTTagCompound> ci)
    {
        if (Mup.config.mc73051.enabled) this.updateBoundingBox();
    }
}
