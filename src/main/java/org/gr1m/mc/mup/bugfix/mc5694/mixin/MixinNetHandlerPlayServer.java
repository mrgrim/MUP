package org.gr1m.mc.mup.bugfix.mc5694.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import org.gr1m.mc.mup.bugfix.mc5694.network.CPacketInstaMine;
import org.gr1m.mc.mup.bugfix.mc5694.INetHandlerPlayServer;
import org.gr1m.mc.mup.bugfix.mc5694.IPlayerInteractionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer implements INetHandlerPlayServer {
    @Shadow
    public EntityPlayerMP player;
    
    @Shadow
    @Final
    private MinecraftServer server;
    
    public void handleInstaMine(CPacketInstaMine packetIn) {
        WorldServer worldserver = this.server.getWorld(this.player.dimension);
        BlockPos blockpos = packetIn.getPos();

        double d0 = this.player.posX - ((double) blockpos.getX() + 0.5D);
        double d1 = this.player.posY - ((double) blockpos.getY() + 0.5D) + 1.5D;
        double d2 = this.player.posZ - ((double) blockpos.getZ() + 0.5D);
        double d3 = d0 * d0 + d1 * d1 + d2 * d2;

        double dist = player.interactionManager.getBlockReachDistance() + 1;
        dist *= dist;

        if (d3 <= dist && blockpos.getY() < this.server.getBuildLimit())
        {
            if (!this.server.isBlockProtected(worldserver, blockpos, this.player) && worldserver.getWorldBorder().contains(blockpos))
            {
                ((IPlayerInteractionManager) this.player.interactionManager).setClientInstaMined(true);
                this.player.interactionManager.onBlockClicked(blockpos, packetIn.getFacing());
                ((IPlayerInteractionManager) this.player.interactionManager).setClientInstaMined(false);
            }
            else
            {
                this.player.connection.sendPacket(new SPacketBlockChange(worldserver, blockpos));
            }
        }
    }
}
