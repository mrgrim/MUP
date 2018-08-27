package org.gr1m.mc.mup.optimization.rsturbo.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.optimization.rsturbo.IBlockRedstoneWire;
import org.gr1m.mc.mup.optimization.rsturbo.RedstoneWireTurbo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRedstoneWire.class)
public abstract class MixinBlockRedstoneWire extends Block implements IBlockRedstoneWire
{
    @Shadow
    private IBlockState calculateCurrentChanges(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state) { return null; }
    
    @Shadow
    private int getMaxCurrentStrength(World worldIn, BlockPos pos, int strength) { return 0; }
    
    @Shadow
    private IBlockState updateSurroundingRedstone(World worldIn, BlockPos pos, IBlockState state) { return null; }
    
    @Shadow
    private boolean canProvidePower;

    private RedstoneWireTurbo turbo = new RedstoneWireTurbo((BlockRedstoneWire) ((Object) (this)));

    public MixinBlockRedstoneWire(Material blockMaterialIn, MapColor blockMapColorIn)
    {
        super(blockMaterialIn, blockMapColorIn);
    }

    public void setCanProvidePower(boolean canProvidePowerIn)
    {
        this.canProvidePower = canProvidePowerIn;
    }
    
    public IBlockState callCalculateCurrentChanges(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state)
    {
        return this.calculateCurrentChanges(worldIn, pos1, pos2, state);
    }
    
    @Inject(method = "updateSurroundingRedstone", at = @At("HEAD"), cancellable = true)
    private void updateSurroundingRedstoneNew(World worldIn, BlockPos pos, IBlockState state, CallbackInfoReturnable<IBlockState> ci)
    {
        if (Mup.config.rsturbo.enabled)
        {
            ci.setReturnValue(turbo.updateSurroundingRedstone(worldIn, pos, state, null));
        }
    }
    
    @Redirect(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockRedstoneWire;updateSurroundingRedstone(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;", ordinal = 0))
    private IBlockState updateSurroundingRedstoneTurbo(BlockRedstoneWire wire, World worldIn, BlockPos pos, IBlockState state, IBlockState methodState, World methodWorldIn, BlockPos methodPos, Block methodBlockIn, BlockPos methodFromPos)
    {
        if (Mup.config.rsturbo.enabled)
        {
            return turbo.updateSurroundingRedstone(worldIn, pos, state, methodFromPos);
        }
        else
        {
            return this.updateSurroundingRedstone(worldIn, pos, state);
        }
    }
    
    // I hate doing soft overrides, and while I could use a bunch of field redirects with local captures and constant
    // modifiers, it would litter the result with function calls. Maybe the JIT could deal with it? It just doesn't seem
    // worth the effort. - MrGrim
    @Inject(method = "calculateCurrentChanges", at = @At("HEAD"), cancellable = true)
    private void calculateCurrentChangesNew(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state, CallbackInfoReturnable<IBlockState> ci)
    {
        if (Mup.config.rsturbo.enabled)
        {
            ci.setReturnValue(this.calculateCurrentChangesTurbo(worldIn, pos1, pos2, state));
        }
    }

    private IBlockState calculateCurrentChangesTurbo(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state)
    {
        IBlockState iblockstate = state;
        int i = state.getValue(BlockRedstoneWire.POWER);
        int j;
        int l = 0;

        this.canProvidePower = false;
        int k = worldIn.isBlockIndirectlyGettingPowered(pos1);
        this.canProvidePower = true;

        // The variable 'k' holds the maximum redstone power value of any adjacent blocks.
        // If 'k' has the highest level of all neighbors, then the power level of this 
        // redstone wire will be set to 'k'.  If 'k' is already 15, then nothing inside the 
        // following loop can affect the power level of the wire.  Therefore, the loop is 
        // skipped if k is already 15. 
        if (k < 15)
        {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
            {
                BlockPos blockpos = pos1.offset(enumfacing);
                boolean flag = blockpos.getX() != pos2.getX() || blockpos.getZ() != pos2.getZ();

                if (flag)
                {
                    l = this.getMaxCurrentStrength(worldIn, blockpos, l);
                }

                if (worldIn.getBlockState(blockpos).isNormalCube() && !worldIn.getBlockState(pos1.up()).isNormalCube())
                {
                    if (flag && pos1.getY() >= pos2.getY())
                    {
                        l = this.getMaxCurrentStrength(worldIn, blockpos.up(), l);
                    }
                }
                else if (!worldIn.getBlockState(blockpos).isNormalCube() && flag && pos1.getY() <= pos2.getY())
                {
                    l = this.getMaxCurrentStrength(worldIn, blockpos.down(), l);
                }
            }
        }

        // The new code sets this redstonewire block's power level to the highest neighbor
        // minus 1.  This usually results in wire power levels dropping by 2 at a time.
        // This optimization alone has no impact on opdate order, only the number of updates.
        j = l - 1;

        // If 'l' turns out to be zero, then j will be set to -1, but then since 'k' will
        // always be in the range of 0 to 15, the following if will correct that.
        if (k > j)
        {
            j=k;
        }

        if (i != j)
        {
            state = state.withProperty(BlockRedstoneWire.POWER, j);

            if (worldIn.getBlockState(pos1) == iblockstate)
            {
                worldIn.setBlockState(pos1, state, 2);
            }
        }

        return state;
    }

}
