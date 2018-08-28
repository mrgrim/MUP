package org.gr1m.mc.mup.tweaks.hud.mixin;

import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.util.text.ITextComponent;
import org.gr1m.mc.mup.tweaks.hud.ISPacketPlayerListHeaderFooter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SPacketPlayerListHeaderFooter.class)
public class MixinSPacketPlayerListHeaderFooter implements ISPacketPlayerListHeaderFooter
{
    @Shadow
    private ITextComponent header;

    @Shadow
    private ITextComponent footer;

    public void setHeader(ITextComponent headerIn)
    {
        this.header = headerIn;
    }

    public void setFooter(ITextComponent footerIn)
    {
        this.footer = footerIn;
    }
}
