package org.gr1m.mc.mup.mc54026.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockPistonBase.class)
public abstract class MixinBlockPistonBase extends BlockDirectional {
    
    private int mixinEventParam;
    
    public MixinBlockPistonBase() { super(Material.PISTON); }

    @Redirect(method = "checkForMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addBlockEvent(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V", ordinal = 1))
    private void sendDropBlockFlag(World world, BlockPos pos, Block blockIn, int eventID, int eventParam, World worldIn, BlockPos callpos, IBlockState state)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        
        BlockPos blockpos = new BlockPos(callpos).add(enumfacing.getFrontOffsetX() * 2, enumfacing.getFrontOffsetY() * 2, enumfacing.getFrontOffsetZ() * 2);
        IBlockState iblockstate = worldIn.getBlockState(blockpos);
        Block block = iblockstate.getBlock();
        int pullable = 0;

        if (block != Blocks.PISTON_EXTENSION) pullable = 16;

        worldIn.addBlockEvent(pos, blockIn, eventID, eventParam | pullable);
    }

    @Inject(method = "eventReceived", at = @At("HEAD"))
    private void setEventParam(IBlockState state, World worldIn, BlockPos pos, int id, int param, CallbackInfoReturnable<Integer> cir)
    {
        this.mixinEventParam = param;
    }
    
    @ModifyVariable(method = "eventReceived", name = "flag1", index = 11, at = @At(value = "LOAD", ordinal = 0))
    private boolean didServerDrop(boolean flag1)
    {
        if ((this.mixinEventParam & 16) == 0)
        {
            flag1 = true;
        }
        
        return flag1;
    }
}
