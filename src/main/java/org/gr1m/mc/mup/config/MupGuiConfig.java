package org.gr1m.mc.mup.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.*;
import org.gr1m.mc.mup.Mup;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MupGuiConfig extends GuiConfig {
    public MupGuiConfig(GuiScreen parent) {
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
        List<IConfigElement> list = new ArrayList<IConfigElement>();
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
        public PatchGuiConfig(GuiScreen parentScreen, List<IConfigElement> configElements, String modID, String configID,
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
            List<IConfigElement> list = new ArrayList<IConfigElement>();
            
            for (Property bugfix : MupConfig.config.getCategory("bug fixes").getOrderedValues())
            {
                list.add(new PatchElement(bugfix));
            }
            
            // Strip "MC-" prefix from bug fix names and sort numerically.
            list.sort((propA, propB) -> {
                return (Integer.parseInt(propA.getName().substring(3)) > Integer.parseInt(propB.getName().substring(3))) ? 1: -1;
            });
            
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
            List<IConfigElement> list = new ArrayList<IConfigElement>();

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
            List<IConfigElement> list = new ArrayList<IConfigElement>();

            for (Property tweak : MupConfig.config.getCategory("tweaks").getOrderedValues())
            {
                list.add(new PatchElement(tweak));
            }

            GuiConfig guiConfig =  new PatchGuiConfig(this.owningScreen, list, this.owningScreen.modID, "tweaks", false, false, "EigenCraft Unofficial Patch");
            guiConfig.titleLine2 = "Tweaks";

            return guiConfig;
        }
    }

    public static class GuiLabelExt extends GuiLabel {
        public GuiLabelExt(FontRenderer fontRendererObj, int labelId, int xIn, int yIn, int widthIn, int heightIn, int colorIn)
        {
            super(fontRendererObj, labelId, xIn, yIn, widthIn, heightIn, colorIn);
        }
        
        public void setWidth(int widthIn) { this.width = widthIn; }
    }
    
    public static class PatchEntries extends GuiConfigEntries {
        private GuiLabelExt header1, header2;
        
        public PatchEntries(GuiConfig parent, Minecraft mc)
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

    public static class PatchEntry extends GuiConfigEntries.ListEntryBase {
        protected final GuiButtonExt enableButton;
        protected final GuiCheckBox loadButton;
        
        protected final boolean beforeLoadValue;
        protected final boolean beforeEnableValue;
        
        protected boolean realEnableValue;
        
        protected boolean currentLoadValue;
        protected boolean currentEnableValue;

        public PatchEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
            super(owningScreen, owningEntryList, configElement);
            
            this.beforeLoadValue = Boolean.valueOf(configElement.getList()[0].toString());
            this.beforeEnableValue = Boolean.valueOf(configElement.getList()[1].toString());
            
            this.currentLoadValue = beforeLoadValue;
            this.currentEnableValue = realEnableValue = beforeEnableValue;

            this.loadButton = new GuiCheckBox(0, owningEntryList.controlX, 0, "Load Patch", currentLoadValue);
            this.loadButton.visible = true;
            this.loadButton.enabled = enabled();
            
            this.enableButton = new GuiButtonExt(0, owningEntryList.controlX + this.loadButton.getButtonWidth() + 10, 0,
                    owningEntryList.controlWidth - this.loadButton.getButtonWidth() - 10, 18, currentEnableValue ? "enabled" : "disabled");
            
            this.enableButton.enabled = enabled() && currentLoadValue && ((IMupConfigElement) this.configElement).isToggleable();
            updateEnableButtonText();
            
            this.toolTip.clear();
            toolTip.add(TextFormatting.GREEN + name);
            String comment = I18n.format(configElement.getLanguageKey() + ".tooltip").replace("\\n", "\n");

            if (!comment.equals(configElement.getLanguageKey() + ".tooltip"))
                toolTip.add(TextFormatting.YELLOW + comment.replace('\n', ' '));
            else if (configElement.getComment() != null && !configElement.getComment().trim().isEmpty())
                toolTip.add(TextFormatting.YELLOW + configElement.getComment().replace('\n', ' '));

            // TODO: Translation
            if ((((IMupConfigElement) this.configElement).getSideEffects()) != null) toolTip.add(TextFormatting.RED + "Side Effects: " + (((IMupConfigElement) this.configElement).getSideEffects()));
            toolTip.add(TextFormatting.WHITE + "Credits: " + (((IMupConfigElement) this.configElement).getCredits()));
            toolTip.add(TextFormatting.AQUA + "[default: " + (Boolean.valueOf(configElement.getDefaults()[0].toString()) ? "Loaded" : "Not Loaded") + ", " + (Boolean.valueOf(configElement.getDefaults()[0].toString()) ? "Enabled" : "Disabled") + "]");
            toolTip.add(TextFormatting.RED + "[Restart Required to Load/Unload!]");
        }

        public void updateEnableButtonText() {
            // Take care to only modify the running configuration if the config is server locked
            boolean actuallyEnabled = (!((IMupConfigElement) this.configElement).getPatchDef().isClientToggleable() && Mup.config.isServerLocked()) ?
                                      ((IMupConfigElement) this.configElement).getPatchDef().isEnabled() : this.currentEnableValue;
            boolean actuallyLoaded  = (!((IMupConfigElement) this.configElement).getPatchDef().isClientToggleable() && Mup.config.isServerLocked()) ?
                                      ((IMupConfigElement) this.configElement).getPatchDef().isServerEnabled() : this.currentLoadValue;
            
            if (((IMupConfigElement) this.configElement).isToggleable())
            {
                this.enableButton.displayString = actuallyEnabled ? "enabled" : "disabled";
                
                if (actuallyEnabled != this.currentEnableValue)
                {
                    enableButton.packedFGColour = GuiUtils.getColorCode('e', true);
                }
                else
                {
                    enableButton.packedFGColour = actuallyEnabled ? GuiUtils.getColorCode('2', true) : GuiUtils.getColorCode('4', true);
                }
            }
            else
            {
                this.enableButton.displayString = actuallyLoaded ? "enabled" : "disabled";

                if (actuallyLoaded != this.currentLoadValue)
                {
                    enableButton.packedFGColour = GuiUtils.getColorCode('e', true);
                }
                else
                {
                    enableButton.packedFGColour = actuallyLoaded ? GuiUtils.getColorCode('9', true) : GuiUtils.getColorCode('4', true);
                }
            }
        }

        public void enableButtonPressed(int slotIndex) {
            if (enabled() && this.enableButton.enabled)
            {
                currentEnableValue = !currentEnableValue;
            }
        }
        
        public void loadButtonPressed(int slotIndex)
        {
            if (enabled())
            {
                if (this.loadButton.isChecked())
                {
                    this.enableButton.enabled = enabled() && (((IMupConfigElement) this.configElement).isToggleable());
                    currentLoadValue = true;
                    currentEnableValue = realEnableValue;
                    updateEnableButtonText();
                }
                else 
                {
                    realEnableValue = currentEnableValue;
                    currentEnableValue = false;
                    this.enableButton.enabled = false;
                    currentLoadValue = false;
                    updateEnableButtonText();
                }
            }
        }

        @Override
        public boolean isDefault() {
            return currentLoadValue   == Boolean.valueOf(configElement.getDefaults()[0].toString()) && 
                   currentEnableValue == Boolean.valueOf(configElement.getDefaults()[1].toString());
        }

        @Override
        public void setToDefault() {
            if (enabled()) {
                currentLoadValue = Boolean.valueOf(configElement.getDefaults()[0].toString());
                this.loadButton.setIsChecked(currentLoadValue);
                
                if (currentLoadValue)
                {
                    currentEnableValue = Boolean.valueOf(configElement.getDefaults()[1].toString());
                }
                else
                {
                    currentEnableValue = false;
                }
                
                updateEnableButtonText();
                this.enableButton.enabled = currentLoadValue && (((IMupConfigElement) this.configElement).isToggleable());
            }
        }

        @Override
        public boolean isChanged() {
            return (currentLoadValue != beforeLoadValue) || (currentEnableValue != beforeEnableValue);
        }

        @Override
        public void undoChanges() {
            if (enabled()) {
                currentLoadValue = beforeLoadValue;
                currentEnableValue = beforeEnableValue;
                
                this.loadButton.setIsChecked(currentLoadValue);
                updateEnableButtonText();
            }
        }

        @Override
        public boolean saveConfigElement() {
            if (enabled() && isChanged()) {
                configElement.set(new Boolean[] {currentLoadValue, currentEnableValue});
                return (currentLoadValue != beforeLoadValue); // MC Restart Required
            }
            return false;
        }

        @Override
        public Boolean getCurrentValue() {
            return null;
        }

        @Override
        public Boolean[] getCurrentValues() {
            return new Boolean[]{currentLoadValue, currentEnableValue};
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partial)
        {
            super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected, partial);

            this.loadButton.x = this.owningScreen.entryList.controlX;
            this.loadButton.y = y + ((slotHeight - this.loadButton.height) / 2) + 1;
            this.loadButton.drawButton(this.mc, mouseX, mouseY, partial);

            this.enableButton.width = this.owningEntryList.controlWidth - this.loadButton.getButtonWidth() - 10;
            this.enableButton.x = this.owningScreen.entryList.controlX + this.loadButton.getButtonWidth() + 10;
            this.enableButton.y = y;
            this.enableButton.drawButton(this.mc, mouseX, mouseY, partial);
        }

        @Override
        public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY)
        {
            if (this.enableButton.mousePressed(this.mc, x, y))
            {
                enableButton.playPressSound(mc.getSoundHandler());
                enableButtonPressed(index);
                updateEnableButtonText();
                return true;
            }
            else if (this.loadButton.mousePressed(this.mc, x, y))
            {
                loadButton.playPressSound(mc.getSoundHandler());
                loadButtonPressed(index);
                return true;
            }
            else
                return super.mousePressed(index, x, y, mouseEvent, relativeX, relativeY);
        }

        @Override
        public void mouseReleased(int index, int x, int y, int mouseEvent, int relativeX, int relativeY)
        {
            super.mouseReleased(index, x, y, mouseEvent, relativeX, relativeY);
            this.enableButton.mouseReleased(x, y);
            this.loadButton.mouseReleased(x, y);
        }
        
        @Override
        public boolean enabled()
        {
            return !Mup.config.isServerLocked() || ((IMupConfigElement) this.configElement).getPatchDef().isClientToggleable();
        }

        @Override
        public void keyTyped(char eventChar, int eventKey)
        {}

        @Override
        public void updateCursorCounter()
        {}

        @Override
        public void mouseClicked(int x, int y, int mouseEvent)
        {}
    }
    
    public static class PatchElement extends ConfigElement implements IMupConfigElement
    {
        private final Property patchProp;
        private final boolean toggleable;
        private final String name;
        private final String credits;
        private final String sideEffects;
        private final PatchDef patchDef;
        
        public PatchElement(Property prop)
        {
            super(prop);
            patchProp = prop;

            this.patchDef = Mup.config.get(patchProp.getName());

            this.name = (this.patchDef == null) ? patchProp.getName() : this.patchDef.getDisplayName();
            this.toggleable = (this.patchDef == null) || this.patchDef.isToggleable();
            this.credits = (this.patchDef == null) ? (TextFormatting.ITALIC + "No credits defined") : this.patchDef.getCredits();
            this.sideEffects = (this.patchDef == null) ? null : this.patchDef.getSideEffects();
        }

        @Override
        public Class<? extends GuiConfigEntries.IConfigEntry> getConfigEntryClass()
        {
            return MupGuiConfig.PatchEntry.class;
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
}