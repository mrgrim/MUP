package org.gr1m.mc.mup.bugfix.mc5694.mixin;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.gr1m.mc.mup.bugfix.mc5694.network.CPacketInstaMine;
import org.gr1m.mc.mup.bugfix.mc5694.network.MC5694PacketHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {
    @Shadow
    @Final
    private Minecraft mc;
    
    @Redirect(slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;onHitBlock(Lnet/minecraft/client/multiplayer/WorldClient;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;F)V", ordinal = 1),
                             to   = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;getMaterial()Lnet/minecraft/block/material/Material;", ordinal = 0)),
              method = "clickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/NetHandlerPlayClient;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0))
    private void sendStartDiggingPacket(final NetHandlerPlayClient connection, Packet<?> packetIn)
    {
        // TODO: Optimize redundant calls to getBlockState and getPlayerRelativeBlockHardness 
        
        BlockPos pos = ((CPacketPlayerDigging)packetIn).getPosition();
        EnumFacing facing = ((CPacketPlayerDigging)packetIn).getFacing();
        IBlockState iblockstate = this.mc.world.getBlockState(pos);
        
        if (iblockstate.getMaterial() != Material.AIR && iblockstate.getPlayerRelativeBlockHardness(this.mc.player, this.mc.player.world, pos) >= 1.0F)
        {
            MC5694PacketHandler.INSTANCE.sendToServer(new CPacketInstaMine(pos, facing));
        }
        else
        {
            connection.sendPacket(packetIn);
        }
    }
    
}
