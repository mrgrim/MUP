package org.gr1m.mc.mup.mc118710.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import org.gr1m.mc.mup.mc118710.IEntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends AbstractClientPlayer implements IEntityPlayerSP {
    
    @Shadow
    private void onUpdateWalkingPlayer() { }
    
    MixinEntityPlayerSP(World worldIn, GameProfile playerProfile)
    {
        super(worldIn, playerProfile);
    }
    
    public void updateWalkingPlayer()
    {
        this.onUpdateWalkingPlayer();
    }
}
