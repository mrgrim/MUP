package org.gr1m.mc.mup.bugfix.mc54026.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc54026.ITileEntityPiston;
import org.gr1m.mc.mup.bugfix.mc54026.IWorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockPistonBase.class)
public abstract class MixinBlockPistonBase extends BlockDirectional
{

    private int mixinEventParam;

    public MixinBlockPistonBase()
    {
        super(Material.PISTON);
    }

    @Redirect(method = "checkForMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addBlockEvent(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V", ordinal = 1))
    private void sendDropBlockFlag(World world, BlockPos pos, Block blockIn, int eventID, int eventParam, World worldIn, BlockPos callpos, IBlockState state)
    {
        int suppress_move = 0;

        if (Mup.config.mc54026.enabled)
        {
            final EnumFacing enumfacing = state.getValue(FACING);

            final BlockPos blockpos = new BlockPos(callpos).offset(enumfacing, 2);
            final IBlockState iblockstate = worldIn.getBlockState(blockpos);

            if (iblockstate.getBlock() == Blocks.PISTON_EXTENSION)
            {
                final TileEntity tileentity = worldIn.getTileEntity(blockpos);

                if (tileentity instanceof TileEntityPiston)
                {
                    final TileEntityPiston tileentitypiston = (TileEntityPiston) tileentity;
                    if (tileentitypiston.getFacing() == enumfacing && tileentitypiston.isExtending()
                        && (((ITileEntityPiston) tileentitypiston).getLastProgress() < 0.5F
                            || tileentitypiston.getWorld().getTotalWorldTime() == ((ITileEntityPiston) tileentitypiston).getLastTicked()
                            || !((IWorldServer) worldIn).haveBlockActionsProcessed()))
                    {
                        suppress_move = 16;
                    }
                }
            }
        }

        worldIn.addBlockEvent(pos, blockIn, eventID, eventParam | suppress_move);
    }

    @Inject(method = "eventReceived", at = @At("HEAD"))
    private void setEventParam(IBlockState state, World worldIn, BlockPos pos, int id, int param, CallbackInfoReturnable<Integer> cir)
    {
        this.mixinEventParam = param;
    }

    @ModifyVariable(method = "eventReceived", name = "flag1", index = 11, at = @At(value = "LOAD", ordinal = 0))
    private boolean didServerDrop(boolean flag1)
    {
        if ((this.mixinEventParam & 16) == 16 && Mup.config.mc54026.enabled)
        {
            flag1 = true;
        }

        return flag1;
    }
}
