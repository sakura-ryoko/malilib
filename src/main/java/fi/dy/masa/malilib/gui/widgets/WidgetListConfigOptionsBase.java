package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import fi.dy.masa.malilib.util.KeyCodes;

public abstract class WidgetListConfigOptionsBase<TYPE, WIDGET extends WidgetConfigOptionBase<TYPE>> extends WidgetListBase<TYPE, WIDGET>
{
    protected final List<TextFieldWrapper<? extends GuiTextFieldGeneric>> textFields = new ArrayList<>();
    protected boolean configsModified;
    protected int maxLabelWidth;
    protected int configWidth;

    public WidgetListConfigOptionsBase(int x, int y, int width, int height, int configWidth)
    {
        super(x, y, width, height, null);

        this.configWidth = configWidth;
        this.browserEntryHeight = 22;
    }

    @Override
    protected void reCreateListEntryWidgets()
    {
        this.applyPendingModifications();

        // Check for modifications before re-creating the widgets.
        // This is needed for the keybind settings, as re-creating
        // those widgets wipes the cached initial settings value.
        if (this.configsModified == false)
        {
            this.wereConfigsModified();
        }

        this.textFields.clear();
        super.reCreateListEntryWidgets();
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.clearTextFieldFocus();

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == KeyCodes.KEY_TAB)
        {
            return this.changeTextFieldFocus(Screen.hasShiftDown());
        }
        else
        {
            for (WIDGET widget : this.listWidgets)
            {
                if (widget.onKeyTyped(keyCode, scanCode, modifiers))
                {
                    return true;
                }
            }

            return super.onKeyTyped(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        for (WIDGET widget : this.listWidgets)
        {
            if (widget.onCharTyped(charIn, modifiers))
            {
                return true;
            }
        }

        return super.onCharTyped(charIn, modifiers);
    }

    public void addTextField(TextFieldWrapper<? extends GuiTextFieldGeneric> wrapper)
    {
        this.textFields.add(wrapper);
    }

    protected boolean changeTextFieldFocus(boolean reverse)
    {
        final int size = this.textFields.size();

        if (size > 1)
        {
            int currentIndex = -1;

            for (int i = 0; i < size; ++i)
            {
                GuiTextFieldGeneric textField = this.textFields.get(i).getTextField();

                if (textField.isFocused())
                {
                    currentIndex = i;
                    textField.setFocused(false);
                    break;
                }
            }

            if (currentIndex != -1)
            {
                int newIndex = currentIndex + (reverse ? -1 : 1);

                if (newIndex >= size)
                {
                    newIndex = 0;
                }
                else if (newIndex < 0)
                {
                    newIndex = size - 1;
                }

                this.textFields.get(newIndex).getTextField().setFocused(true);
                this.applyPendingModifications();

                return true;
            }
        }

        return false;
    }

    protected void clearTextFieldFocus()
    {
        this.applyPendingModifications();

        for (int i = 0; i < this.textFields.size(); ++i)
        {
            GuiTextFieldGeneric textField = this.textFields.get(i).getTextField();

            if (textField.isFocused())
            {
                textField.setFocused(false);
                break;
            }
        }
    }

    public void markConfigsModified()
    {
        this.configsModified = true;
    }

    public boolean wereConfigsModified()
    {
        // First check the cached value, this gets updated when scrolling
        // the list and the widgets get re-created.
        if (this.configsModified)
        {
            return true;
        }

        for (WidgetConfigOptionBase<TYPE> widget : this.listWidgets)
        {
            if (widget.wasConfigModified())
            {
                this.configsModified = true;
                return true;
            }
        }

        return false;
    }

    public void clearConfigsModifiedFlag()
    {
        this.configsModified = false;
    }

    public void applyPendingModifications()
    {
        for (WidgetConfigOptionBase<TYPE> widget : this.listWidgets)
        {
            if (widget.hasPendingModifications())
            {
                widget.applyNewValueToConfig();
                // Cache the modified status before scrolling etc. and thus re-creating the widgets
                this.configsModified = true;
            }
        }
    }
}
