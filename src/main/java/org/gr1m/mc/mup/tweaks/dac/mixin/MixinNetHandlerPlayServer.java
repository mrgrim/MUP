package org.gr1m.mc.mup.tweaks.dac.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gr1m.mc.mup.Mup;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer
{
    @Shadow
    public EntityPlayerMP player;
    
    @Shadow
    @Final
    private MinecraftServer server;
    
    @Shadow
    @Final
    private static Logger LOGGER;
    
    // Handle Player movement case
    @Inject(method = "processPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;isInvulnerableDimensionChange()Z", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD, slice = @Slice(to = @At(value = "CONSTANT", args = "stringValue={} moved too quickly! {},{},{}")))
    private void checkIfPlayerMovedTooQuickly(CPacketPlayer packetIn, CallbackInfo ci, WorldServer worldserver, double d0, double d1, double d2, double d3, double d4, double d5,
                                        double d6, float f, float f1, double d7, double d8, double d9, double d10, double d11, int i)
    {
        if (Mup.config.dac.enabled)
        {
            if (!this.player.isInvulnerableDimensionChange() && (!this.player.getServerWorld().getGameRules().getBoolean("disableElytraMovementCheck") || !this.player.isElytraFlying()))
            {
                float f2 = this.player.isElytraFlying() ? 300.0F : 100.0F;

                if (d11 - d10 > (double) (f2 * (float) i) && (!this.server.isSinglePlayer() || !this.server.getServerOwner().equals(this.player.getName())))
                {
                    LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName(), Double.valueOf(d7), Double.valueOf(d8), Double.valueOf(d9));
                }
            }
        }
    }
    
    @Redirect(method = "processPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;isInvulnerableDimensionChange()Z", ordinal = 0),
              slice = @Slice(to = @At(value = "CONSTANT", args = "stringValue={} moved too quickly! {},{},{}")))
    private boolean cancelPlayerFastMovementCheck(EntityPlayerMP player)
    {
        return Mup.config.dac.enabled || player.isInvulnerableDimensionChange();
    }
    
    // Handle vehicle movement case
    @Inject(method = "processVehicleMove", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;motionZ:D", ordinal = 1, shift = At.Shift.BY, by = 4),
            slice = @Slice(to = @At(value = "CONSTANT", args = "stringValue={} (vehicle of {}) moved too quickly! {},{},{}")),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void checkIfVehicleMovedTooQuickly(CPacketVehicleMove packetIn, CallbackInfo ci, Entity entity, WorldServer worldserver, double d0, double d1, double d2, double d3,
                                               double d4, double d5, float f, float f1, double d6, double d7, double d8, double d9)
    {
        if (Mup.config.dac.enabled)
        {
            double d10 = d6 * d6 + d7 * d7 + d8 * d8;

            if (d10 - d9 > 100.0D && (!this.server.isSinglePlayer() || !this.server.getServerOwner().equals(entity.getName())))
            {
                LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getName(), this.player.getName(), Double.valueOf(d6), Double.valueOf(d7), Double.valueOf(d8));
            }
        }
    }
    
    @ModifyVariable(method = "processVehicleMove", index = 26, name = "d10", at = @At(value = "STORE", opcode = Opcodes.DSTORE, ordinal = 0),
                    slice = @Slice(to = @At(value = "CONSTANT", args = "stringValue={} (vehicle of {}) moved too quickly! {},{},{}")))
    private double cancelVehicleFastMovementCheck(double d10)
    {
        if (Mup.config.dac.enabled)
        {
            return 0.0D;
        }
        else
        {
            return d10;
        }
    }
}
