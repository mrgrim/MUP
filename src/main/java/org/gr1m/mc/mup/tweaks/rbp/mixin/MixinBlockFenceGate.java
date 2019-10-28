package org.gr1m.mc.mup.tweaks.rbp.mixin;

import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.Material;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.rbp.config.RbpCustomConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockFenceGate.class)
public abstract class MixinBlockFenceGate
{
    @Redirect(method = "canPlaceBlockAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/material/Material;isSolid()Z"))
    private boolean relaxedPlacement(Material materialIn)
    {
        return materialIn.isSolid() || (Mup.config.rbp.enabled && ((RbpCustomConfig)(Mup.config.rbp.customConfig)).fenceGate);
    }
}
