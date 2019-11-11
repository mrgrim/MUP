package org.gr1m.mc.mup.modcompat.rsppas.mixin;

import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.math.BlockPos;
import org.gr1m.mc.mup.modcompat.rsppas.IContainerWorkbench;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ContainerWorkbench.class)
public abstract class MixinContainerWorkbench implements IContainerWorkbench
{
    @Shadow
    @Final
    private BlockPos pos;

    public BlockPos getPos()
    {
        return this.pos;
    }
}
