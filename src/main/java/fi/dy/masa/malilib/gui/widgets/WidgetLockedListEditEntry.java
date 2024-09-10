package fi.dy.masa.malilib.gui.widgets;

import java.util.List;

import net.minecraft.client.gui.DrawContext;

import fi.dy.masa.malilib.config.IConfigLockedList;
import fi.dy.masa.malilib.config.IConfigLockedListEntry;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerTextField;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetLockedListEditEntry extends WidgetConfigOptionBase<String>
{
    protected final WidgetListLockedListEdit parent;
    protected final IConfigLockedListEntry defaultValue;
    protected final int listIndex;
    protected final boolean isOdd;

    public WidgetLockedListEditEntry(int x, int y, int width, int height,
                                     int listIndex, boolean isOdd, IConfigLockedListEntry initialValue, IConfigLockedListEntry defaultValue, WidgetListLockedListEdit parent)
    {
        super(x, y, width, height, parent, initialValue != null ? initialValue.getDisplayName() : "", listIndex);

        this.listIndex = listIndex;
        this.isOdd = isOdd;
        this.defaultValue = defaultValue;
        this.lastAppliedValue = initialValue != null ? initialValue.getDisplayName() : "";
        this.initialStringValue = initialValue != null ? initialValue.getDisplayName() : "";
        this.parent = parent;

        int textFieldX = x + 20;
        int textFieldWidth = width - 160;
        int resetX = textFieldX + textFieldWidth + 2;
        int by = y + 4;
        int bx = textFieldX;
        int bOff = 18;

        if (this.isDummy() == false)
        {
            this.addLabel(x + 2, y + 6, 20, 12, 0xC0C0C0C0, String.format("%3d:", listIndex + 1));
            bx = this.addTextField(textFieldX, y + 1, resetX, textFieldWidth, 20, initialValue);

            if (this.canBeMoved(true))
            {
                this.addListActionButton(bx, by, ButtonType.MOVE_DOWN);
            }

            bx += bOff;

            if (this.canBeMoved(false))
            {
                this.addListActionButton(bx, by, ButtonType.MOVE_UP);
                bx += bOff;
            }
        }
    }

    protected boolean isDummy()
    {
        return this.listIndex < 0;
    }

    protected void addListActionButton(int x, int y, ButtonType type)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, type.getIcon(), type.getDisplayName());
        ListenerListActions listener = new ListenerListActions(type, this);
        this.addButton(button, listener);
    }

    protected int addTextField(int x, int y, int resetX, int configWidth, int configHeight, IConfigLockedListEntry initialValue)
    {
        GuiTextFieldGeneric field = this.createTextField(x, y + 1, configWidth - 4, configHeight - 3);
        field.setMaxLength(this.maxTextfieldTextLength);
        field.setText(initialValue != null ? initialValue.getDisplayName() : "");

        ButtonGeneric resetButton = this.createResetButton(resetX, y, field);
        ChangeListenerTextField listenerChange = new ChangeListenerTextField(field, resetButton, this.defaultValue);
        ListenerResetConfig listenerReset = new ListenerResetConfig(resetButton, this);

        this.addTextField(field, listenerChange);
        this.addButton(resetButton, listenerReset);

        return resetButton.x + resetButton.getWidth() + 4;
    }

    protected ButtonGeneric createResetButton(int x, int y, GuiTextFieldGeneric textField)
    {
        String labelReset = StringUtils.translate("malilib.gui.button.reset.caps");
        ButtonGeneric resetButton = new ButtonGeneric(x, y, -1, 20, labelReset);
        resetButton.setEnabled(textField.getText().equals(this.defaultValue.getStringValue()) == false && textField.getText().equals(this.defaultValue.getDisplayName()) == false);

        return resetButton;
    }

    @Override
    public boolean wasConfigModified()
    {
        return this.isDummy() == false && this.textField.getTextField().getText().equals(this.initialStringValue) == false;
    }

    @Override
    public void applyNewValueToConfig()
    {
        if (this.isDummy() == false)
        {
            IConfigLockedList config = this.parent.getConfig();
            List<IConfigLockedListEntry> list = config.getEntries();
            String value = this.textField.getTextField().getText();

            if (list.size() > this.listIndex)
            {
                list.set(this.listIndex, config.getEntry(value));
                this.lastAppliedValue = value;
            }
        }
    }

    private void moveEntry(boolean down)
    {
        List<IConfigLockedListEntry> list = this.parent.getConfig().getEntries();
        final int size = list.size();

        if (this.listIndex >= 0 && this.listIndex < size)
        {
            IConfigLockedListEntry tmp;
            int index1 = this.listIndex;
            int index2 = -1;

            if (down && this.listIndex < (size - 1))
            {
                index2 = index1 + 1;
            }
            else if (down == false && this.listIndex > 0)
            {
                index2 = index1 - 1;
            }

            if (index2 >= 0)
            {
                this.parent.markConfigsModified();
                this.parent.applyPendingModifications();

                tmp = list.get(index1);
                list.set(index1, list.get(index2));
                list.set(index2, tmp);
                this.parent.refreshEntries();
            }
        }
    }

    private boolean canBeMoved(boolean down)
    {
        final int size = this.parent.getConfig().getEntries().size();
        return (this.listIndex >= 0 && this.listIndex < size) &&
                ((down && this.listIndex < (size - 1)) || (down == false && this.listIndex > 0));
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected, DrawContext drawContext)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        if (this.isOdd)
        {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x20FFFFFF);
        }
        // Draw a slightly lighter background for even entries
        else
        {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x30FFFFFF);
        }

        this.drawSubWidgets(mouseX, mouseY, drawContext);
        this.drawTextFields(mouseX, mouseY, drawContext);
        super.render(mouseX, mouseY, selected, drawContext);
    }

    public static class ChangeListenerTextField extends ConfigOptionChangeListenerTextField
    {
        protected final IConfigLockedListEntry defaultValue;

        public ChangeListenerTextField(GuiTextFieldGeneric textField, ButtonBase buttonReset, IConfigLockedListEntry defaultValue)
        {
            super(null, textField, buttonReset);

            this.defaultValue = defaultValue;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField)
        {
            this.buttonReset.setEnabled(this.textField.getText().equals(this.defaultValue.getStringValue()) == false && this.textField.getText().equals(this.defaultValue.getDisplayName()) == false);
            return false;
        }
    }

    @Override
    public boolean onKeyTypedImpl(int keyCode, int scanCode, int modifiers)
    {
        return false;
    }

    @Override
    protected boolean onCharTypedImpl(char charIn, int modifiers)
    {
        return false;
    }

    private static class ListenerResetConfig implements IButtonActionListener
    {
        private final WidgetLockedListEditEntry parent;
        private final ButtonGeneric buttonReset;

        public ListenerResetConfig(ButtonGeneric buttonReset, WidgetLockedListEditEntry parent)
        {
            this.buttonReset = buttonReset;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            this.parent.textField.getTextField().setText(this.parent.defaultValue.getDisplayName());
            this.buttonReset.setEnabled(this.parent.textField.getTextField().getText().equals(this.parent.defaultValue.getStringValue()) == false && this.parent.textField.getTextField().getText().equals(this.parent.defaultValue.getDisplayName()) == false);
        }
    }

    private static class ListenerListActions implements IButtonActionListener
    {
        private final ButtonType type;
        private final WidgetLockedListEditEntry parent;

        public ListenerListActions(ButtonType type, WidgetLockedListEditEntry parent)
        {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            this.parent.moveEntry(this.type == ButtonType.MOVE_DOWN);
        }
    }

    private enum ButtonType
    {
        MOVE_UP     (MaLiLibIcons.ARROW_UP,     "malilib.gui.button.hovertext.move_up"),
        MOVE_DOWN   (MaLiLibIcons.ARROW_DOWN,   "malilib.gui.button.hovertext.move_down");

        private final MaLiLibIcons icon;
        private final String hoverTextkey;

        ButtonType(MaLiLibIcons icon, String hoverTextkey)
        {
            this.icon = icon;
            this.hoverTextkey = hoverTextkey;
        }

        public IGuiIcon getIcon()
        {
            return this.icon;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.hoverTextkey);
        }
    }
}
