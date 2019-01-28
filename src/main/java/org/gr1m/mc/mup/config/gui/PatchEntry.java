package org.gr1m.mc.mup.config.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.*;
import org.gr1m.mc.mup.Mup;

public class PatchEntry extends GuiConfigEntries.ListEntryBase {
    private final GuiButtonExt enableButton;
    private final GuiCheckBoxExt loadButton;
    private final GuiButtonImage wrenchButton;

    private final boolean beforeLoadValue;
    private final boolean beforeEnableValue;

    private boolean realEnableValue;

    private boolean currentLoadValue;
    private boolean currentEnableValue;

    public PatchEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
    {
        super(owningScreen, owningEntryList, configElement);

        this.beforeLoadValue = Boolean.valueOf(configElement.getList()[0].toString());
        this.beforeEnableValue = Boolean.valueOf(configElement.getList()[1].toString());

        this.currentLoadValue = beforeLoadValue;
        this.currentEnableValue = realEnableValue = beforeEnableValue;

        this.loadButton = new GuiCheckBoxExt(0, owningEntryList.controlX, 2, "Load Patch", currentLoadValue);
        this.loadButton.visible = true;
        this.loadButton.enabled = this.enabled();

        this.enableButton = new GuiButtonExt(0, owningEntryList.controlX + this.loadButton.getButtonWidth() + 10, 0,
                                             owningEntryList.controlWidth - this.loadButton.getButtonWidth() - 10, 18, currentEnableValue ? "enabled" : "disabled");
        this.enableButton.visible = true;
        this.enableButton.enabled = this.enabled() && currentLoadValue && ((IMupConfigElement) this.configElement).isToggleable();
        updateEnableButtonText();

        if (((IMupConfigElement) configElement).getPatchDef().customConfig != null)
        {
            this.enableButton.setWidth(this.enableButton.getButtonWidth() - 18);
            
            this.wrenchButton = new GuiButtonImage(owningEntryList.controlX + this.loadButton.getButtonWidth() + 10 + this.enableButton.getButtonWidth(), 0);
            this.wrenchButton.visible = true;
            this.wrenchButton.enabled = this.enabled();
        }
        else
        {
            this.wrenchButton = null;
        }

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

    private void updateEnableButtonText() {
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

    private void enableButtonPressed() {
        if (enabled() && this.enableButton.enabled)
        {
            currentEnableValue = !currentEnableValue;
        }
    }

    private void loadButtonPressed()
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
        
        if (this.wrenchButton != null)
        {
            this.enableButton.setWidth(this.enableButton.getButtonWidth() - 18);
            
            this.wrenchButton.x = this.owningScreen.entryList.controlX + this.loadButton.getButtonWidth() + 10 + this.enableButton.getButtonWidth();
            this.wrenchButton.y = y;
            this.wrenchButton.drawButton(this.mc, mouseX, mouseY, partial);
        }

        this.enableButton.drawButton(this.mc, mouseX, mouseY, partial);
    }

    @Override
    public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY)
    {
        if (this.enableButton.mousePressed(this.mc, x, y))
        {
            enableButton.playPressSound(mc.getSoundHandler());
            enableButtonPressed();
            updateEnableButtonText();
            return true;
        }
        else if (this.loadButton.mousePressed(this.mc, x, y))
        {
            loadButton.playPressSound(mc.getSoundHandler());
            loadButtonPressed();
            return true;
        }
        else if (this.wrenchButton != null && this.wrenchButton.mousePressed(this.mc, x, y))
        {
            loadButton.playPressSound(mc.getSoundHandler());
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
