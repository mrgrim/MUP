package org.gr1m.mc.mup.bugfix.mc125157.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.PlayerMenuObject;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerMenuObject.class)
public abstract class MixinPlayerMenuObject {
    @Shadow
    @Final
    private GameProfile profile;

    @Redirect(method = "renderIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V"))
    private void redirectbindTexture(TextureManager textureManager, ResourceLocation resource) {
        if (Mup.config.mc125157.enabled)
        {
            final Minecraft mc = Minecraft.getMinecraft();
            final NetworkPlayerInfo npi = mc.player.connection.getPlayerInfo(this.profile.getName());
            if (npi != null)
            {
                mc.getTextureManager().bindTexture(npi.getLocationSkin());
            }
        }
        else
        {
            textureManager.bindTexture(resource);
        }
    }
}
