package org.gr1m.mc.mup.bugfix.mc88959.mixin;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.block.BlockPistonBase.EXTENDED;

@Mixin(BlockPistonBase.class)
public abstract class MixinBlockPistonBase
{
	@Inject(method = "checkForMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addBlockEvent(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;II)V", ordinal = 1))
	private void onPistonDepower(World worldIn, BlockPos pos, IBlockState state, CallbackInfo ci)
	{
		if (Mup.config.mc88959.enabled)
		{
			worldIn.setBlockState(pos, state.withProperty(EXTENDED, false), 2);
		}
	}
}
