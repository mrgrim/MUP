package org.gr1m.mc.mup.modcompat.rsppas.mixin;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.gr1m.mc.mup.modcompat.rsppas.IContainerCraftingTableFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = { "sandro.RedstonePlusPlus.Patch.MC1_12_1.CraftingTableFix.ContainerCraftingTableFix",
                   "sandro.RedstonePlusPlus.Patch.MC1_12_2.CraftingTableFix.ContainerCraftingTableFix" })
public abstract class MixinContainerCraftingTableFix implements IContainerCraftingTableFix
{
    public BlockPos getTEPos()        
    {
        try {
            return ((TileEntity)(this.getClass().getDeclaredField("tileEntity").get(this))).getPos();
        }
        catch (Exception e)
        {
            return BlockPos.ORIGIN;
        }
    }
}
