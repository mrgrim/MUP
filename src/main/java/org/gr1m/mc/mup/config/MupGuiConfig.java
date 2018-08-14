package org.gr1m.mc.mup.config;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.*;
import org.gr1m.mc.mup.Mup;

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
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<IConfigElement>();
        list.add(new DummyConfigElement.DummyCategoryElement("Bug Fixes", "foo", MupGuiConfig.BugFixes.class));
        return list;
    }

    @Override
    public void initGui() {
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
            
            GuiConfig guiConfig =  new GuiConfig(this.owningScreen, list, this.owningScreen.modID, "bug fixes", false, false, "EigenCraft Unofficial Patch");
            guiConfig.titleLine2 = "Bug Fixes";
            
            return guiConfig;
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
            
            this.enableButton.enabled = enabled() && currentLoadValue;
            updateEnableButtonText();
            
            this.toolTip.clear();
            toolTip.add(TextFormatting.GREEN + name);
            String comment = I18n.format(configElement.getLanguageKey() + ".tooltip").replace("\\n", "\n");

            if (!comment.equals(configElement.getLanguageKey() + ".tooltip"))
                toolTip.add(TextFormatting.YELLOW + comment.replace('\n', ' '));
            else if (configElement.getComment() != null && !configElement.getComment().trim().isEmpty())
                toolTip.add(TextFormatting.YELLOW + configElement.getComment().replace('\n', ' '));
            else
                toolTip.add(TextFormatting.RED + "No tooltip defined.");

            // TODO: Translation
            toolTip.add(TextFormatting.AQUA + "[default: " + (Boolean.valueOf(configElement.getDefaults()[0].toString()) ? "Loaded" : "Not Loaded") + ", " + (Boolean.valueOf(configElement.getDefaults()[0].toString()) ? "Enabled" : "Disabled") + "]");
            toolTip.add(TextFormatting.RED + "[Restart Required to Load/Unload!]");
        }

        public void updateEnableButtonText() {
            this.enableButton.displayString = currentEnableValue ? "enabled" : "disabled";
            enableButton.packedFGColour = currentEnableValue ? GuiUtils.getColorCode('2', true) : GuiUtils.getColorCode('4', true);
        }

        public void enableButtonPressed(int slotIndex) {
            if (enabled())
                currentEnableValue = !currentEnableValue;
        }
        
        public void loadButtonPressed(int slotIndex)
        {
            if (enabled())
            {
                if (this.loadButton.isChecked())
                {
                    this.enableButton.enabled = enabled();
                    currentLoadValue = true;
                    currentEnableValue = realEnableValue;
                    updateEnableButtonText();
                }
                else 
                {
                    realEnableValue = currentEnableValue;
                    currentEnableValue = false;
                    this.enableButton.enabled = false;
                    updateEnableButtonText();
                    currentLoadValue = false;
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
                this.enableButton.enabled = currentLoadValue;
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
            
            this.enableButton.width = this.owningEntryList.controlWidth - this.loadButton.getButtonWidth() - 10;
            this.enableButton.x = this.owningScreen.entryList.controlX + this.loadButton.getButtonWidth() + 10;
            this.enableButton.y = y;
            this.enableButton.enabled = enabled() && currentLoadValue;
            this.enableButton.drawButton(this.mc, mouseX, mouseY, partial);

            this.loadButton.x = this.owningScreen.entryList.controlX;
            this.loadButton.y = y + ((slotHeight - this.loadButton.height) / 2) + 1;
            this.enableButton.enabled = enabled();
            this.loadButton.drawButton(this.mc, mouseX, mouseY, partial);
        }

        @Override
        public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY)
        {
            if (this.enableButton.mousePressed(this.mc, x, y) && this.enableButton.enabled)
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
        public void keyTyped(char eventChar, int eventKey)
        {}

        @Override
        public void updateCursorCounter()
        {}

        @Override
        public void mouseClicked(int x, int y, int mouseEvent)
        {}
    }
    
    public static class PatchElement extends ConfigElement {
        final Property patchProp;
        
        public PatchElement(Property prop)
        {
            super(prop);
            patchProp = prop;
        }

        @Override
        public Class<? extends GuiConfigEntries.IConfigEntry> getConfigEntryClass()
        {
            return MupGuiConfig.PatchEntry.class;
        }
        
        @Override
        public String getName()
        {
            MupConfig.BugDef bugDef = Mup.config.get(patchProp.getName());
            
            return (bugDef == null) ? patchProp.getName() : bugDef.getDisplayName();
        }
    }
}