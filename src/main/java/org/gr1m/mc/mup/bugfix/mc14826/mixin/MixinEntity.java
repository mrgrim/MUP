package org.gr1m.mc.mup.bugfix.mc14826.mixin;

import net.minecraft.entity.Entity;
import org.gr1m.mc.mup.bugfix.mc14826.ILeashBackRef;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class MixinEntity implements ILeashBackRef
{
    private Entity leashBackRef;
    
    public Entity getLeashBackRef()
    {
        return leashBackRef;
    }
    
    public void setLeashBackRef(Entity leashBackRefIn)
    {
        this.leashBackRef = leashBackRefIn;
    }
}
