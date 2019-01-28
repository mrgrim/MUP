package org.gr1m.mc.mup.config.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiButtonImage extends GuiButtonExt
{


    public GuiButtonImage(int x, int y) {
        super(0, x, y, 18, 18, "C");
    }

    @Override
    public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(MupGuiConfig.ICON_WRENCH);
        drawScaledCustomSizeModalRect(this.x + 3, this.y + 3, 0, 0, 16, 16, Math.min(16, this.width - 6), Math.min(16, this.height - 6), 16, 16);
    }

}