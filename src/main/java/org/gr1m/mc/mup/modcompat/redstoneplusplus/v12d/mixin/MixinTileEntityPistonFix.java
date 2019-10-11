package org.gr1m.mc.mup.modcompat.redstoneplusplus.v12d.mixin;

import net.minecraft.tileentity.TileEntityPiston;
import org.gr1m.mc.mup.bugfix.mc54026.ITileEntityPiston;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "sandro.RedstonePlusPlus.Modules.PistonFix.TileEntityPistonFix")
public class MixinTileEntityPistonFix extends TileEntityPiston implements ITileEntityPiston
{
    @Shadow(remap = false)
    private float lastProgress;

    private long lastTicked;

    @Inject(method = "Lsandro/RedstonePlusPlus/Modules/PistonFix/TileEntityPistonFix;update()V", at = @At("HEAD"))
    private void setLastTicked(CallbackInfo ci)
    {
        this.lastTicked = this.world.getTotalWorldTime();
    }

    public long getLastTicked()
    {
        return this.lastTicked;
    }
    public float getLastProgress() { return this.lastProgress; }
}
