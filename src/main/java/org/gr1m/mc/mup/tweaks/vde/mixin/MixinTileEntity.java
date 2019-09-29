package org.gr1m.mc.mup.tweaks.vde.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.vde.config.VdeCustomConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(TileEntity.class)
public abstract class MixinTileEntity
{
    @ModifyConstant(method = "getMaxRenderDistanceSquared", constant = @Constant(doubleValue = 4096.0))
    private double baseRenderDistance(double defaultBase)
    {
        if (Mup.config.vde.enabled)
        {
            double distance = ((VdeCustomConfig)(Mup.config.vde.customConfig)).tileEntityViewDistance * Entity.getRenderDistanceWeight();
            return distance * distance;
        }
        else
        {
            return defaultBase;
        }
    }
}
