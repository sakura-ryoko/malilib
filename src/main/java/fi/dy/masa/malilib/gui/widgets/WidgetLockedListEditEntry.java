package fi.dy.masa.malilib.gui.widgets;

import java.util.List;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import fi.dy.masa.malilib.config.IConfigLockedList;
import fi.dy.masa.malilib.config.IConfigLockedListEntry;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerTextField;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.wrappers.TextFieldType;
import fi.dy.masa.malilib.render.GuiContext;
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
        field.setValue(initialValue != null ? initialValue.getDisplayName() : "");

        ButtonGeneric resetButton = this.createResetButton(resetX, y, field);
        ChangeListenerTextField listenerChange = new ChangeListenerTextField(field, resetButton, this.defaultValue);
        ListenerResetConfig listenerReset = new ListenerResetConfig(resetButton, this);

        this.addTextField(field, listenerChange, TextFieldType.STRING.setMaxLength(this.maxTextfieldTextLength));
        this.addButton(resetButton, listenerReset);

        return resetButton.x + resetButton.getWidth() + 4;
    }

    protected ButtonGeneric createResetButton(int x, int y, GuiTextFieldGeneric textField)
    {
        String labelReset = StringUtils.translate("malilib.gui.button.reset.caps");
        ButtonGeneric resetButton = new ButtonGeneric(x, y, -1, 20, labelReset);
        resetButton.setEnabled(textField.getValue().equals(this.defaultValue.getStringValue()) == false && textField.getValue().equals(this.defaultValue.getDisplayName()) == false);

        return resetButton;
    }

    @Override
    public boolean wasConfigModified()
    {
        return this.isDummy() == false && this.textField.textField().getValue().equals(this.initialStringValue) == false;
    }

    @Override
    public void applyNewValueToConfig()
    {
        if (this.isDummy() == false && this.textField != null)
        {
            IConfigLockedList config = this.parent.getConfig();
            List<IConfigLockedListEntry> list = config.getEntries();
            String value = this.textField.textField().getValue();

            if (list.size() > this.listIndex)
            {
                list.set(this.listIndex, config.getEntry(value));
                this.lastAppliedValue = value;
                config.markDirty();
                config.setModified();
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
                this.parent.getConfig().markDirty();
                this.parent.getConfig().setModified();
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
    public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
//        super.render(ctx, mouseX, mouseY, selected);

        if (this.isOdd)
        {
            RenderUtils.drawRect(ctx, this.x, this.y, this.width, this.height, 0x20FFFFFF);
        }
        // Draw a slightly lighter background for even entries
        else
        {
            RenderUtils.drawRect(ctx, this.x, this.y, this.width, this.height, 0x30FFFFFF);
        }

        this.drawSubWidgets(ctx, mouseX, mouseY);
        this.drawTextFields(ctx, mouseX, mouseY);
        super.render(ctx, mouseX, mouseY, selected);
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
            this.buttonReset.setEnabled(this.textField.getValue().equals(this.defaultValue.getStringValue()) == false && this.textField.getValue().equals(this.defaultValue.getDisplayName()) == false);
            return false;
        }
    }

    @Override
    public boolean onKeyTypedImpl(KeyEvent input)
    {
        return false;
    }

    @Override
    protected boolean onCharTypedImpl(CharacterEvent input)
    {
        return false;
    }

	private record ListenerResetConfig(ButtonGeneric buttonReset,
	                                   WidgetLockedListEditEntry parent) implements IButtonActionListener
	{
		@Override
		public void actionPerformedWithButton(ButtonBase button, int mouseButton)
		{
			this.parent.textField.textField().setValue(this.parent.defaultValue.getDisplayName());
			this.buttonReset.setEnabled(this.parent.textField.textField().getValue().equals(this.parent.defaultValue.getStringValue()) == false && this.parent.textField.textField().getValue().equals(this.parent.defaultValue.getDisplayName()) == false);
		}
	}

	private record ListenerListActions(ButtonType type,
	                                   WidgetLockedListEditEntry parent) implements IButtonActionListener
	{
		@Override
		public void actionPerformedWithButton(ButtonBase button, int mouseButton)
		{
			this.parent.moveEntry(this.type == ButtonType.MOVE_DOWN);
		}
	}

    protected enum ButtonType
    {
        MOVE_UP     (MaLiLibIcons.ARROW_UP,     "malilib.gui.button.hovertext.move_up"),
        MOVE_DOWN   (MaLiLibIcons.ARROW_DOWN,   "malilib.gui.button.hovertext.move_down");

        private final MaLiLibIcons icon;
        private final String hoverTextKey;

        ButtonType(MaLiLibIcons icon, String hoverTextKey)
        {
            this.icon = icon;
            this.hoverTextKey = hoverTextKey;
        }

        public IGuiIcon getIcon()
        {
            return this.icon;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.hoverTextKey);
        }
    }
}
