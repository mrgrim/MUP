package org.gr1m.mc.mup.modcompat.redstoneplusplus.v13b2.mixin;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc54026.ITileEntityPiston;
import org.gr1m.mc.mup.bugfix.mc54026.IWorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@Pseudo
@Mixin(targets = "sandro.RedstonePlusPlus.Modules.ImprovedPistons.Pistons.BlockPistonBaseFix")
public abstract class MixinBlockPistonBaseFix extends BlockPistonBase
{
    // Redstone++ 1.3 BETA-2 implements an earlier version of the MC-54026 fix that's not 100% vanilla, but damn close.
    // All of this is to override its fix to implement the newer one. All of the reflection and indirection may hurt
    // performance though.
    
    @Shadow(remap = false)
    private boolean shouldBeExtended(World worldIn, BlockPos pos, EnumFacing facing) { return false; };

    private Constructor<?> helperInit;
    private Method methodCanMove;
    
    public MixinBlockPistonBaseFix()
    {
        super(false);
    }
    
    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void doReflection(CallbackInfo ci)
    {
        try
        {
            Class<?> helperClass = Class.forName("sandro.RedstonePlusPlus.Modules.ImprovedPistons.Pistons.BlockPistonStructureHelperFix");
            this.helperInit = helperClass.getDeclaredConstructor(World.class, BlockPos.class, EnumFacing.class, boolean.class);
            
            try
            {
                this.methodCanMove = helperClass.getDeclaredMethod("func_177253_a");
            }
            catch (Exception e)
            {
                // Maybe a dev environment?
                this.methodCanMove = helperClass.getDeclaredMethod("canMove");
            }
        }
        catch (Exception e)
        {
            this.helperInit = null;
            this.methodCanMove = null;
        }
    }
    
    /**
     * @author Michael Kreitzer
     * 
     * @reason I don't mind overwriting here because this mixin targets a specific version of a mod and will never be forward
     * compatible.
     */
    @Overwrite(remap = false)
    private void checkForMove(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing enumfacing = state.getValue(FACING);
        boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);
        
        if (flag && !state.getValue(EXTENDED)) {
            // All of this is to avoid a compile time dependency...
            Object helper;

            try
            {
                helper = this.helperInit.newInstance(worldIn, pos, enumfacing, true);
                if (((boolean)this.methodCanMove.invoke(helper)))
                {
                    worldIn.addBlockEvent(pos, this, 0, enumfacing.getIndex());
                }
            }
            catch (Exception e)
            {
                // Try a vanilla extension
                if ((new BlockPistonStructureHelper(worldIn, pos, enumfacing, true)).canMove()) {
                    worldIn.addBlockEvent(pos, this, 0, enumfacing.getIndex());
                }
            }
        } else if (!flag && (Boolean)state.getValue(EXTENDED)) {
            int suppress_move = 0;

            if (Mup.config.mc54026.enabled)
            {
                final BlockPos blockpos = new BlockPos(pos).offset(enumfacing, 2);
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

            if (Mup.config.mc88959.enabled)
            {
                worldIn.setBlockState(pos, state.withProperty(EXTENDED, false), 2);
            }
            worldIn.addBlockEvent(pos, this, 1, enumfacing.getIndex() | suppress_move);
        }
    }

    // We need to do this in case the server has the fix enabled but the client has it disabled. Otherwise, this mod
    // already checks the flag we send.
    @ModifyVariable(method = "Lsandro/RedstonePlusPlus/Modules/ImprovedPistons/Pistons/BlockPistonBaseFix;eventReceived(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;II)Z",
                    name = "param", index = 5, at = @At(value = "LOAD", ordinal = 1))
    private int didServerDrop(int param)
    {
        if ((param & 16) == 16 && Mup.config.mc54026.enabled)
        {
            return 0;
        }

        return 16;
    }
}
