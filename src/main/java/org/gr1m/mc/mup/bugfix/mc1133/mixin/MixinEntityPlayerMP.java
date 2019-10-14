package org.gr1m.mc.mup.bugfix.mc1133.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc1133.IEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP extends EntityPlayer
{
    public MixinEntityPlayerMP(World worldIn, GameProfile gameProfileIn)
    {
        super(worldIn, gameProfileIn); // shush
    }
    
    @Redirect(method = "handleFalling", at = @At(value = "NEW", target = "net/minecraft/util/math/BlockPos", ordinal = 0))
    private BlockPos handleFallingSmartly(int x, int y, int z)
    {
        if (Mup.config.mc1133.enabled)
        {
            return ((IEntity) this).findFloorEffectBlockPos(x, y, z);
        }
        else
        {
            return new BlockPos(x, y, z);
        }
    }
    
    // Not sure why this doesn't need a remap...
    @Redirect(method = "handleFalling", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;isAir(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Z", ordinal = 0, remap = false))
    private boolean cancelPlayerAirCheck(Block blockIn, IBlockState stateIn, IBlockAccess worldIn, BlockPos posIn)
    {
        if (Mup.config.mc1133.enabled)
        {
            return false;
        }
        else
        {
            return blockIn.isAir(stateIn, worldIn, posIn);
        }
    }
}
