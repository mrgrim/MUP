package org.gr1m.mc.mup.mc4.mixin;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import org.gr1m.mc.mup.mc4.INetHandlerPlayClient;
import org.gr1m.mc.mup.mc4.network.SPacketNewEntityLookMove;
import org.gr1m.mc.mup.mc4.network.SPacketNewEntityRelMove;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient implements INetHandlerPlayClient {
    @Shadow
    private WorldClient world;
    
    public void handleNewEntityRelMove(SPacketNewEntityRelMove packetIn)
    {
        Entity entity = packetIn.getEntity(this.world);

        if (entity != null)
        {
            double x = entity.posX + packetIn.getX();
            double y = entity.posY + packetIn.getY();
            double z = entity.posZ + packetIn.getZ();
            EntityTracker.updateServerPosition(entity, x, y, z);

            if (!entity.canPassengerSteer())
            {
                entity.setPositionAndRotationDirect(x, y, z, entity.rotationYaw, entity.rotationPitch, 3, false);
                entity.onGround = packetIn.getOnGround();
            }
        }
    }

    public void handleNewEntityLookMove(SPacketNewEntityLookMove packetIn)
    {
        Entity entity = packetIn.getEntity(this.world);

        if (entity != null)
        {
            double x = entity.posX + packetIn.getX();
            double y = entity.posY + packetIn.getY();
            double z = entity.posZ + packetIn.getZ();
            EntityTracker.updateServerPosition(entity, x, y, z);

            if (!entity.canPassengerSteer())
            {
                float yaw = (float)(packetIn.getYaw() * 360) / 256.0F;
                float pitch = (float)(packetIn.getPitch() * 360) / 256.0F;
                
                entity.setPositionAndRotationDirect(x, y, z, yaw, pitch, 3, false);
                entity.onGround = packetIn.getOnGround();
            }
        }
    }
}
