package org.gr1m.mc.mup.bugfix.mc5694.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.bugfix.mc5694.IPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInteractionManager.class)
public class MixinPlayerInteractionManager implements IPlayerInteractionManager {
    @Shadow
    public EntityPlayerMP player;
    
    @Shadow
    public World world;
    
    @Shadow
    private BlockPos destroyPos;
    
    private boolean clientInstaMined = false;
    
    public void setClientInstaMined(boolean instaMined)
    {
        this.clientInstaMined = instaMined;
    }
    
    @Inject(slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerInteractionManager;tryHarvestBlock(Lnet/minecraft/util/math/BlockPos;)Z", ordinal = 1)),
            method = "onBlockClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;sendBlockBreakProgress(ILnet/minecraft/util/math/BlockPos;I)V"))
    private void sendBlockStateOnFailedInstaMine(BlockPos pos, EnumFacing side, CallbackInfo ci)
    {
        if (this.clientInstaMined && Mup.config.mc5694.enabled)
        {
            this.player.connection.sendPacket(new SPacketBlockChange(this.world, this.destroyPos));
        }
    }
    
    @Redirect(method = "onBlockClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerInteractionManager;tryHarvestBlock(Lnet/minecraft/util/math/BlockPos;)Z", ordinal = 1))
    private boolean tryHarvestBlockHandleFailure(PlayerInteractionManager that, BlockPos pos)
    {
        if (!that.tryHarvestBlock(pos) && Mup.config.mc5694.enabled)
        {
            that.player.connection.sendPacket(new SPacketBlockChange(that.world, pos));
            return false;
        }
        
        return true;
    }
}
