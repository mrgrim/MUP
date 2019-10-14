package org.gr1m.mc.mup.bugfix.mc161869.mixin;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlotCrafting.class)
public abstract class MixinSlotCrafting extends Slot
{
    @Shadow
    private int amountCrafted;

    public MixinSlotCrafting(IInventory inventoryIn, int index, int xPosition, int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
    }
    
    @Inject(method = "decrStackSize", at = @At("HEAD"), cancellable = true)
    private void fixQCraftingStats(int amount, CallbackInfoReturnable<ItemStack> cir)
    {
        if (Mup.config.mc161869.enabled)
        {
            ItemStack ret = super.decrStackSize(amount);
            this.amountCrafted += ret.getCount();
            
            cir.setReturnValue(ret);
        }
    }
}
