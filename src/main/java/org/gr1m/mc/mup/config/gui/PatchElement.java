package org.gr1m.mc.mup.config.gui;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.config.PatchDef;

import javax.annotation.Nullable;

public class PatchElement extends ConfigElement implements IMupConfigElement
{
    private final boolean toggleable;
    private final String name;
    private final String credits;
    private final String sideEffects;
    private final PatchDef patchDef;

    PatchElement(Property prop)
    {
        super(prop);

        this.patchDef = Mup.config.get(prop.getName());

        this.name = (this.patchDef == null) ? prop.getName() : this.patchDef.getDisplayName();
        this.toggleable = (this.patchDef == null) || this.patchDef.isToggleable();
        this.credits = (this.patchDef == null) ? (TextFormatting.ITALIC + "No credits defined") : this.patchDef.getCredits();
        this.sideEffects = (this.patchDef == null) ? null : this.patchDef.getSideEffects();
    }

    @Override
    public Class<? extends GuiConfigEntries.IConfigEntry> getConfigEntryClass()
    {
        return PatchEntry.class;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getCredits()
    {
        return this.credits;
    }

    @Override
    @Nullable
    public String getSideEffects()
    {
        return this.sideEffects;
    }

    @Override
    public boolean isToggleable()
    {
        return this.toggleable;
    }

    @Override
    @Nullable
    public PatchDef getPatchDef() { return this.patchDef; }
}
