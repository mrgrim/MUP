package org.gr1m.mc.mup.config.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.*;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.config.MupConfig;

import java.util.ArrayList;
import java.util.List;

public class MupGuiConfig extends GuiConfig {
    MupGuiConfig(GuiScreen parent) {
        super(parent,
                getConfigElements(),
                Mup.MODID,
                false,
                false,
                "EigenCraft Unofficial Patch");
        titleLine2 = "General Configuration";
        this.entryList = new PatchEntries(this, this.mc);
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();
        list.add(new DummyConfigElement.DummyCategoryElement("Bug Fixes", "bugfixes", MupGuiConfig.BugFixes.class));
        list.add(new DummyConfigElement.DummyCategoryElement("Optimizations", "optimizations", MupGuiConfig.Optimizations.class));
        list.add(new DummyConfigElement.DummyCategoryElement("Tweaks", "tweaks", MupGuiConfig.Tweaks.class));
        return list;
    }

    @Override
    public void initGui() {
        if (this.entryList == null || this.needsRefresh)
        {
            this.entryList = new PatchEntries(this, this.mc);
            this.needsRefresh = false;
        }

        // You can add buttons and initialize fields here
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // You can do things like create animations, draw additional elements, etc. here
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        // You can process any additional buttons you may have added here
        super.actionPerformed(button);
    }

    public static class PatchGuiConfig extends GuiConfig {
        PatchGuiConfig(GuiScreen parentScreen, List<IConfigElement> configElements, String modID, String configID,
                       boolean allRequireWorldRestart, boolean allRequireMcRestart, String title)
        {
            super(parentScreen, configElements, modID, configID, allRequireWorldRestart, allRequireMcRestart, title);

            this.entryList = new PatchEntries(this, this.mc);
        }

        @Override
        public void initGui() {
            if (this.entryList == null || this.needsRefresh)
            {
                this.entryList = new PatchEntries(this, this.mc);
                this.needsRefresh = false;
            }

            // You can add buttons and initialize fields here
            super.initGui();
        }
    }

    public static class BugFixes extends GuiConfigEntries.CategoryEntry
    {
        public BugFixes(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
        {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            List<IConfigElement> list = new ArrayList<>();
            
            for (Property bugfix : MupConfig.config.getCategory("bug fixes").getOrderedValues())
            {
                list.add(new PatchElement(bugfix));
            }
            
            // Strip "MC-" prefix from bug fix names and sort numerically.
            list.sort((propA, propB) -> (Integer.parseInt(propA.getName().substring(3)) > Integer.parseInt(propB.getName().substring(3))) ? 1 : -1);
            
            GuiConfig guiConfig =  new PatchGuiConfig(this.owningScreen, list, this.owningScreen.modID, "bug fixes", false, false, "EigenCraft Unofficial Patch");
            guiConfig.titleLine2 = "Bug Fixes";
            
            return guiConfig;
        }
    }
    
    public static class Optimizations extends GuiConfigEntries.CategoryEntry
    {
        public Optimizations(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
        {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            List<IConfigElement> list = new ArrayList<>();

            for (Property bugfix : MupConfig.config.getCategory("optimizations").getOrderedValues())
            {
                list.add(new PatchElement(bugfix));
            }

            GuiConfig guiConfig =  new PatchGuiConfig(this.owningScreen, list, this.owningScreen.modID, "optimizations", false, false, "EigenCraft Unofficial Patch");
            guiConfig.titleLine2 = "Optimizations";

            return guiConfig;
        }
    }

    public static class Tweaks extends GuiConfigEntries.CategoryEntry
    {
        public Tweaks(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
        {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            List<IConfigElement> list = new ArrayList<>();

            for (Property tweak : MupConfig.config.getCategory("tweaks").getOrderedValues())
            {
                list.add(new PatchElement(tweak));
            }

            GuiConfig guiConfig =  new PatchGuiConfig(this.owningScreen, list, this.owningScreen.modID, "tweaks", false, false, "EigenCraft Unofficial Patch");
            guiConfig.titleLine2 = "Tweaks";

            return guiConfig;
        }
    }
}