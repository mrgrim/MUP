package org.gr1m.mc.mup.mc54026.mixin;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import org.gr1m.mc.mup.mc54026.ITileEntityPiston;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityPiston.class)
public abstract class MixinTileEntityPiston extends TileEntity implements ITileEntityPiston {
    private long lastTicked;
    
    @Inject(method = "update", at = @At("HEAD"))
    private void setLastTicked(CallbackInfo ci)
    {
        this.lastTicked = this.world.getTotalWorldTime();
    }
    
    public long getLastTicked()
    {
        return this.lastTicked;
    }
}
