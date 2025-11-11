package fi.dy.masa.malilib.gui.widgets;

import java.util.List;
import net.minecraft.client.gui.DrawContext;
import fi.dy.masa.malilib.config.IConfigColorList;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerTextField;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.Color4f;

public class WidgetColorListEditEntry extends WidgetConfigOptionBase<Color4f>
{
    protected final WidgetColorListEdit parent;
    protected final Color4f defaultValue;
    protected final int listIndex;
    protected final boolean isOdd;

    public WidgetColorListEditEntry(int x, int y, int width, int height, int listIndex, boolean isOdd, Color4f initialValue, Color4f defaultValue, WidgetColorListEdit parent)
    {
        super(x, y, width, height, parent, initialValue, listIndex);

        this.listIndex = listIndex;
        this.isOdd = isOdd;
        this.defaultValue = defaultValue;
        this.lastAppliedValue = initialValue.toString();
        this.initialStringValue = initialValue.toString();
        this.parent = parent;

        int textFieldX = x + 20;
        int textFieldWidth = width - 160 - 22;
        int resetX = textFieldX + textFieldWidth + 2 + 22;
        int by = y + 4;
        int bx = textFieldX;
        int bOff = 18;

        if (!this.isDummy())
        {
            this.addLabel(x + 2, y + 6, 20, 12, 0xC0C0C0C0, String.format("%3d:", listIndex + 1));
            bx = this.addTextField(textFieldX, y + 1, resetX, textFieldWidth, 20, initialValue.toString());

            this.addWidget(new WidgetColorIndicator(textFieldX + textFieldWidth + 2, y + 1, 19, 19, initialValue, this::applyNewValueToConfig));

            this.addListActionButton(bx, by, ButtonType.ADD);
            bx += bOff;

            this.addListActionButton(bx, by, ButtonType.REMOVE);
            bx += bOff;

            if (this.canBeMoved(true)) {
                this.addListActionButton(bx, by, ButtonType.MOVE_DOWN);
            }

            bx += bOff;

            if (this.canBeMoved(false))
            {
                this.addListActionButton(bx, by, ButtonType.MOVE_UP);
                bx += bOff;
            }
        }
        else
        {
            this.addListActionButton(bx, by, ButtonType.ADD);
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

    protected int addTextField(int x, int y, int resetX, int configWidth, int configHeight, String initialValue)
    {
        GuiTextFieldGeneric field = this.createTextField(x, y + 1, configWidth - 4, configHeight - 3);
        field.setMaxLength(this.maxTextfieldTextLength);
        field.setText(initialValue);

        ButtonGeneric resetButton = this.createResetButton(resetX, y, field);
        ChangeListenerTextField listenerChange = new ChangeListenerTextField(field, resetButton, this.defaultValue.toString());
        ListenerResetConfig listenerReset = new ListenerResetConfig(resetButton, this);

        this.addTextField(field, listenerChange);
        this.addButton(resetButton, listenerReset);

        return resetButton.getX() + resetButton.getWidth() + 4;
    }

    protected ButtonGeneric createResetButton(int x, int y, GuiTextFieldGeneric textField)
    {
        String labelReset = StringUtils.translate("malilib.gui.button.reset.caps");
        ButtonGeneric resetButton = new ButtonGeneric(x, y, -1, 20, labelReset);
        resetButton.setEnabled(!textField.getText().equals(this.defaultValue));

        return resetButton;
    }

    @Override
    public boolean wasConfigModified()
    {
        return !this.isDummy() && !this.textField.getTextField().getText().equals(this.initialStringValue);
    }

    @Override
    public void applyNewValueToConfig()
    {
        applyNewValueToConfig(StringUtils.getColor(this.textField.getTextField().getText(), Color4f.ZERO.intValue));
    }

    protected void applyNewValueToConfig(int color)
    {
        if (!this.isDummy())
        {
            IConfigColorList config = this.parent.getConfig();
            List<Color4f> list = config.getColors();

            Color4f value = Color4f.fromColor(color);

            if (list.size() > this.listIndex)
            {
                list.set(this.listIndex, value);
                this.lastAppliedValue = value.toString();
                config.setModified();
            }
        }
    }

    private void insertEntryBefore()
    {
        List<Color4f> list = this.parent.getConfig().getColors();
        final int size = list.size();
        int index = this.listIndex < 0 ? size : (Math.min(this.listIndex, size));
        list.add(index, Color4f.ZERO);
        this.parent.getConfig().setModified();
        this.parent.refreshEntries();
        this.parent.markConfigsModified();
    }

    private void removeEntry()
    {
        List<Color4f> list = this.parent.getConfig().getColors();
        final int size = list.size();

        if (this.listIndex >= 0 && this.listIndex < size)
        {
            list.remove(this.listIndex);
            this.parent.getConfig().setModified();
            this.parent.refreshEntries();
            this.parent.markConfigsModified();
        }
    }

    private void moveEntry(boolean down)
    {
        List<Color4f> list = this.parent.getConfig().getColors();
        final int size = list.size();

        if (this.listIndex >= 0 && this.listIndex < size)
        {
            Color4f tmp;
            int index1 = this.listIndex;
            int index2 = -1;

            if (down && this.listIndex < (size - 1))
            {
                index2 = index1 + 1;
            }
            else if (!down && this.listIndex > 0)
            {
                index2 = index1 - 1;
            }

            if (index2 >= 0)
            {
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
        final int size = this.parent.getConfig().getColors().size();
        return (this.listIndex >= 0 && this.listIndex < size) &&
                ((down && this.listIndex < (size - 1)) || (!down && this.listIndex > 0));
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.render(drawContext, mouseX, mouseY, selected);
//        RenderUtils.color(1f, 1f, 1f, 1f);

        if (this.isOdd)
        {
            RenderUtils.drawRect(drawContext, this.x, this.y, this.width, this.height, 0x20FFFFFF);
        }
        // Draw a slightly lighter background for even entries
        else
        {
            RenderUtils.drawRect(drawContext, this.x, this.y, this.width, this.height, 0x30FFFFFF);
        }

        this.drawSubWidgets(drawContext, mouseX, mouseY);
        this.drawTextFields(drawContext, mouseX, mouseY);
        super.render(drawContext, mouseX, mouseY, selected);
    }

    public static class ChangeListenerTextField extends ConfigOptionChangeListenerTextField
    {
        protected final String defaultValue;

        public ChangeListenerTextField(GuiTextFieldGeneric textField, ButtonBase buttonReset, String defaultValue)
        {
            super(null, textField, buttonReset);

            this.defaultValue = defaultValue;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField)
        {
            this.buttonReset.setEnabled(!this.textField.getText().equals(this.defaultValue));
            return false;
        }
    }

    private static class ListenerResetConfig implements IButtonActionListener
    {
        private final WidgetColorListEditEntry parent;
        private final ButtonGeneric buttonReset;

        public ListenerResetConfig(ButtonGeneric buttonReset, WidgetColorListEditEntry parent)
        {
            this.buttonReset = buttonReset;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            this.parent.textField.getTextField().setText(this.parent.defaultValue.toString());
            this.parent.parent.applyPendingModifications();
            this.buttonReset.setEnabled(!this.parent.textField.getTextField().getText().equals(this.parent.defaultValue.toString()));
            this.parent.parent.refreshEntries();
        }
    }

    private static class ListenerListActions implements IButtonActionListener
    {
        private final ButtonType type;
        private final WidgetColorListEditEntry parent;

        public ListenerListActions(ButtonType type, WidgetColorListEditEntry parent)
        {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            if (this.type == ButtonType.ADD)
            {
                this.parent.insertEntryBefore();
            }
            else if (this.type == ButtonType.REMOVE)
            {
                this.parent.removeEntry();
            }
            else
            {
                this.parent.moveEntry(this.type == ButtonType.MOVE_DOWN);
            }
        }
    }

    private enum ButtonType
    {
        ADD(MaLiLibIcons.PLUS, "malilib.gui.button.hovertext.add"),
        REMOVE(MaLiLibIcons.MINUS, "malilib.gui.button.hovertext.remove"),
        MOVE_UP(MaLiLibIcons.ARROW_UP, "malilib.gui.button.hovertext.move_up"),
        MOVE_DOWN(MaLiLibIcons.ARROW_DOWN, "malilib.gui.button.hovertext.move_down");

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
