package org.gr1m.mc.mup.config.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.gr1m.mc.mup.Mup;

public class PatchEntries extends GuiConfigEntries
{
    private GuiLabelExt header1, header2;

    PatchEntries(GuiConfig parent, Minecraft mc)
    {
        super(parent, mc);

        if (Mup.config.isServerLocked())
        {
            this.setHasListHeader(true, ((mc.fontRenderer.FONT_HEIGHT + 1) * 2) + 2);

            this.header1 = new GuiLabelExt(this.mc.fontRenderer, 0, 0, 0, this.width, mc.fontRenderer.FONT_HEIGHT, GuiUtils.getColorCode('4', true));
            this.header1.setCentered();
            this.header1.addLine("Server is Managing Configuration");

            this.header2 = new GuiLabelExt(this.mc.fontRenderer, 0, 0, 0, this.width, mc.fontRenderer.FONT_HEIGHT, GuiUtils.getColorCode('e', true));
            this.header2.setCentered();
            this.header2.addLine("Only Selected Client Side Options will be Available");
        }
    }

    @Override
    protected void drawListHeader(int insideLeft, int insideTop, Tessellator tessellatorIn)
    {
        if (Mup.config.isServerLocked())
        {
            this.header1.setWidth(this.width);
            this.header1.x = insideLeft;
            this.header1.y = insideTop + 1;

            this.header1.drawLabel(this.mc, this.mouseX, this.mouseY);

            this.header2.setWidth(this.width);
            this.header2.x = insideLeft;
            this.header2.y = insideTop + 1 + this.mc.fontRenderer.FONT_HEIGHT + 1;

            this.header2.drawLabel(this.mc, this.mouseX, this.mouseY);
        }
    }
}

