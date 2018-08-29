package org.gr1m.mc.mup.bugfix.mc118710.mixin;

import net.minecraft.network.NetHandlerPlayServer;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer {
    @ModifyConstant(method = "processPlayer", constant = @Constant(intValue = 5, ordinal = 0))
    private int modifyMaxPlayerMovementPacketsPerTick(int maxPackets)
    {
        if (maxPackets == 5 && Mup.config.mc118710.enabled)
            return 10;
        else
            return maxPackets;
    }
}
