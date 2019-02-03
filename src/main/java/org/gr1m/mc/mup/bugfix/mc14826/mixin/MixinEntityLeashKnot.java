package org.gr1m.mc.mup.bugfix.mc14826.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc14826.ILeashBackRef;
import org.gr1m.mc.mup.bugfix.mc14826.ILeashSaver;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityLeashKnot.class)
public abstract class MixinEntityLeashKnot extends EntityHanging
{
    public MixinEntityLeashKnot(World worldIn)
    {
        super(worldIn);
    }

    // The idea here is that any leashed entity is in lazy chunks and will not be processing, so we convert its leashed
    // entity pointer to a NBT tag, and if it becomes entity processing again the knot will be re-created.
    public void onRemovedFromWorld()
    {
        super.onRemovedFromWorld();

        if (Mup.config.mc14826.enabled)
        {
            Entity leashedEntity = ((ILeashBackRef) this).getLeashBackRef();
            if (leashedEntity != null)
            {
                ((ILeashSaver) leashedEntity).convertLeashToNBT();
            }
        }
    }
}
