package org.gr1m.mc.mup.mc118710.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.gr1m.mc.mup.mc118710.IEntityPlayerSP;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {
    @Shadow
    @Final
    private Minecraft mc;
    
    @Inject(slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getTutorial()Lnet/minecraft/client/tutorial/Tutorial;", ordinal = 1),
                           to   = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;getMaterial()Lnet/minecraft/block/material/Material;", ordinal = 0)),
            method = "clickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/NetHandlerPlayClient;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0))
    private void updatePlayerPositionBeforeDigging(BlockPos loc, EnumFacing face, CallbackInfoReturnable<Boolean> ci)
    {
        ((IEntityPlayerSP)this.mc.player).updateWalkingPlayer();
    }
}
