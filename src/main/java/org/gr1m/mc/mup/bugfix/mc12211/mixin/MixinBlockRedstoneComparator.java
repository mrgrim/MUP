package org.gr1m.mc.mup.bugfix.mc12211.mixin;

import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRedstoneComparator.class)
public abstract class MixinBlockRedstoneComparator extends BlockRedstoneDiode
{
    public MixinBlockRedstoneComparator(boolean powered)
    {
        super(powered);
    }
    
    @Shadow
    protected abstract int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state);

    @Shadow
    @Final
    public static PropertyEnum<BlockRedstoneComparator.Mode> MODE;

    @Redirect(method = "calculateOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockRedstoneComparator;calculateInputStrength(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)I", ordinal = 1))
    private int fixCompareModeOutput(BlockRedstoneComparator that, World worldIn, BlockPos posIn, IBlockState stateIn)
    {
        if (Mup.config.mc12211.enabled)
        {
            int inputStrength = this.calculateInputStrength(worldIn, posIn, stateIn);
            
            if (this.getPowerOnSides(worldIn, posIn, stateIn) > inputStrength)
            {
                return 0;
            }
            else
            {
                return inputStrength;
            }
        }
        else
        {
            return this.calculateInputStrength(worldIn, posIn, stateIn);
        }
    }
    
    @Inject(method = "shouldBePowered", at = @At("HEAD"), cancellable = true)
    private void betterShouldBePowered(World worldIn, BlockPos posIn, IBlockState stateIn, CallbackInfoReturnable<Boolean> cir)
    {
        if (Mup.config.mc12211.enabled)
        {
            cir.setReturnValue(false);

            int inputStrength = this.calculateInputStrength(worldIn, posIn, stateIn);

            if (inputStrength > 0)
            {
                int sideStrength = this.getPowerOnSides(worldIn, posIn, stateIn);

                if (inputStrength > sideStrength)
                {
                    cir.setReturnValue(true);
                }
                else if (inputStrength == sideStrength)
                {
                    cir.setReturnValue(stateIn.getValue(MODE) == BlockRedstoneComparator.Mode.COMPARE);
                }
            }
        }
    }
}
