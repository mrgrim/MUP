package org.gr1m.mc.mup.bugfix.mc100830.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import org.gr1m.mc.mup.bugfix.mc100830.IEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity
{
    private double moveDeltaX;
    private double moveDeltaY;
    private double moveDeltaZ;
    
    public double getMoveDeltaX()
    {
        return moveDeltaX;
    }

    public double getMoveDeltaY()
    {
        return moveDeltaY;
    }

    public double getMoveDeltaZ()
    {
        return moveDeltaZ;
    }
    
    @Inject(method = "move", at = @At("HEAD"))
    private void captureMoveDeltas(MoverType type, double x, double y, double z, CallbackInfo ci)
    {
        this.moveDeltaX = x;
        this.moveDeltaY = y;
        this.moveDeltaZ = z;
    }
}
