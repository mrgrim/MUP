package org.gr1m.mc.mup.bugfix.mc118710.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import org.gr1m.mc.mup.bugfix.mc118710.IEntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer implements IEntityPlayerSP {
    
    @Shadow
    private void onUpdateWalkingPlayer() { }
    
    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile)
    {
        super(worldIn, playerProfile);
    }
    
    public void updateWalkingPlayer()
    {
        this.onUpdateWalkingPlayer();
    }
}
