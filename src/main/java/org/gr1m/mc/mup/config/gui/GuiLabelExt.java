package org.gr1m.mc.mup.config.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiLabel;

// Exists solely to allow changing the width post-instantiation.
// This prevents bugged rendering when the window is resized

class GuiLabelExt extends GuiLabel
{
    GuiLabelExt(FontRenderer fontRendererObj, int labelId, int xIn, int yIn, int widthIn, int heightIn, int colorIn)
    {
        super(fontRendererObj, labelId, xIn, yIn, widthIn, heightIn, colorIn);
    }

    void setWidth(int widthIn) { this.width = widthIn; }
}
