package fi.dy.masa.malilib.gui.widgets;


import fi.dy.masa.malilib.config.IConfigStringMap;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerTextField;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.KeyCodes;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.util.Pair;

import java.util.List;

public class WidgetStringMapEditEntry extends WidgetConfigOptionBase<Pair<String, String>> {
    protected final WidgetListStringMapEdit parent;
    protected final Pair<String, String> defaultValue;
    protected final int listIndex;
    protected final boolean isOdd;
    private TextFieldWrapper<? extends GuiTextFieldGeneric> textFieldKey;
    private TextFieldWrapper<? extends GuiTextFieldGeneric> textFieldValue;

    protected Pair<String, String> initialValue;
    private String lastAppliedVKey;
    private String lastAppliedVValue;

    public WidgetStringMapEditEntry(int x, int y, int width, int height,
                                    int listIndex, boolean isOdd, Pair<String, String> initialValue, Pair<String, String> defaultValue, WidgetListStringMapEdit parent) {
        super(x, y, width, height, parent, initialValue, listIndex);

        this.listIndex = listIndex;
        this.isOdd = isOdd;
        this.defaultValue = defaultValue;
        this.initialValue = initialValue;
        this.parent = parent;
        int textFieldX = x + 20;
        int textFieldWidth = width - 160;
        int resetX = textFieldX + textFieldWidth + 2;
        int by = y + 4;
        int bx = textFieldX;
        int bOff = 18;

        if (!this.isDummy()) {
            this.addLabel(x + 2, y + 6, 20, 12, 0xC0C0C0C0, String.format("%3d:", listIndex + 1));
            bx = this.addTextFields(textFieldX, y + 1, resetX, textFieldWidth, 20, initialValue);

            this.addListActionButton(bx, by, WidgetStringMapEditEntry.ButtonType.ADD);
            bx += bOff;

            this.addListActionButton(bx, by, WidgetStringMapEditEntry.ButtonType.REMOVE);
            bx += bOff;

            if (this.canBeMoved(true)) {
                this.addListActionButton(bx, by, WidgetStringMapEditEntry.ButtonType.MOVE_DOWN);
            }

            bx += bOff;

            if (this.canBeMoved(false)) {
                this.addListActionButton(bx, by, WidgetStringMapEditEntry.ButtonType.MOVE_UP);
                bx += bOff;
            }
        } else {
            this.addListActionButton(bx, by, WidgetStringMapEditEntry.ButtonType.ADD);
        }
    }

    protected boolean isDummy() {
        return this.listIndex < 0;
    }

    protected void addListActionButton(int x, int y, WidgetStringMapEditEntry.ButtonType type) {
        ButtonGeneric button = new ButtonGeneric(x, y, type.getIcon(), type.getDisplayName());
        WidgetStringMapEditEntry.ListenerListActions listener = new WidgetStringMapEditEntry.ListenerListActions(type, this);
        this.addButton(button, listener);
    }

    protected int addTextFields(int x, int y, int resetX, int configWidth, int configHeight, Pair<String, String> initialValue) {
        GuiTextFieldGeneric fieldKey = this.createTextField(x, y + 1, configWidth / 2 - 4, configHeight - 3);
        fieldKey.setMaxLength(this.maxTextfieldTextLength);
        fieldKey.setText(initialValue.getLeft());
        GuiTextFieldGeneric fieldValue = this.createTextField(x + configWidth / 2, y + 1, configWidth / 2 - 4, configHeight - 3);
        fieldValue.setMaxLength(this.maxTextfieldTextLength);
        fieldValue.setText(initialValue.getRight());

        ButtonGeneric resetButton = this.createResetButton(resetX, y);
        WidgetStringMapEditEntry.ChangeListenerTextField listenerChange = new WidgetStringMapEditEntry.ChangeListenerTextField(fieldKey, resetButton, this.defaultValue, this);
        WidgetStringMapEditEntry.ListenerResetConfig listenerReset = new WidgetStringMapEditEntry.ListenerResetConfig(resetButton, this);


        TextFieldWrapper<? extends GuiTextFieldGeneric> wrapper = new TextFieldWrapper<>(fieldKey, listenerChange);
        this.textFieldKey = wrapper;
        this.parent.addTextField(wrapper);

        TextFieldWrapper<? extends GuiTextFieldGeneric> wrapper2 = new TextFieldWrapper<>(fieldValue, listenerChange);
        this.textFieldValue = wrapper2;
        this.parent.addTextField(wrapper2);
        this.addButton(resetButton, listenerReset);

        String key = textFieldKey.getTextField().getText();
        String value = textFieldValue.getTextField().getText();
        resetButton.setEnabled(!key.equals(defaultValue.getLeft()) || !value.equals(defaultValue.getRight()));

        return resetButton.getX() + resetButton.getWidth() + 4;
    }

    protected ButtonGeneric createResetButton(int x, int y) {
        String labelReset = StringUtils.translate("malilib.gui.button.reset.caps");
        ButtonGeneric resetButton = new ButtonGeneric(x, y, -1, 20, labelReset);

        return resetButton;
    }

    @Override
    public boolean wasConfigModified() {
        return !this.isDummy() &&
                (!this.textFieldKey.getTextField().getText().equals(this.initialValue.getLeft()) ||
                !this.textFieldValue.getTextField().getText().equals(this.initialValue.getRight()));
    }

    @Override
    public void applyNewValueToConfig() {
        if (!this.isDummy()) {
            IConfigStringMap config = this.parent.getConfig();
            List<Pair<String, String>> list = config.getMap();
            String key = this.textFieldKey.getTextField().getText();
            String value = this.textFieldValue.getTextField().getText();

            if (list.size() > this.listIndex) {
                list.set(this.listIndex, new Pair<>(key, value));

                lastAppliedVKey = key;
                lastAppliedVValue = value;
                config.setModified();
            }
        }
    }

    private void insertEntryBefore() {
        List<Pair<String, String>> list = this.parent.getConfig().getMap();
        final int size = list.size();
        int index = this.listIndex < 0 ? size : (Math.min(this.listIndex, size));
        list.add(index, new Pair<>("", ""));
        this.parent.getConfig().setModified();
        this.parent.refreshEntries();
        this.parent.markConfigsModified();
    }

    private void removeEntry() {
        List<Pair<String, String>> list = this.parent.getConfig().getMap();
        final int size = list.size();

        if (this.listIndex >= 0 && this.listIndex < size) {
            list.remove(this.listIndex);
            this.parent.getConfig().setModified();
            this.parent.refreshEntries();
            this.parent.markConfigsModified();
        }
    }

    private void moveEntry(boolean down) {
        List<Pair<String, String>> list = this.parent.getConfig().getMap();
        final int size = list.size();

        if (this.listIndex >= 0 && this.listIndex < size) {
            Pair<String, String> tmp;
            int index1 = this.listIndex;
            int index2 = -1;

            if (down && this.listIndex < (size - 1)) {
                index2 = index1 + 1;
            } else if (!down && this.listIndex > 0) {
                index2 = index1 - 1;
            }

            if (index2 >= 0) {
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

    private boolean canBeMoved(boolean down) {
        final int size = this.parent.getConfig().getMap().size();
        return (this.listIndex >= 0 && this.listIndex < size) &&
                ((down && this.listIndex < (size - 1)) || (!down && this.listIndex > 0));
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, boolean selected) {
        super.render(drawContext, mouseX, mouseY, selected);
//        RenderUtils.color(1f, 1f, 1f, 1f);

        if (this.isOdd) {
            RenderUtils.drawRect(drawContext, this.x, this.y, this.width, this.height, 0x20FFFFFF);
        } else {
            RenderUtils.drawRect(drawContext, this.x, this.y, this.width, this.height, 0x30FFFFFF);
        }

        this.drawSubWidgets(drawContext, mouseX, mouseY);

        if (textFieldKey != null && textFieldValue != null) {
            this.textFieldKey.getTextField().render(drawContext, mouseX, mouseY, 0f);
            this.textFieldValue.getTextField().render(drawContext, mouseX, mouseY, 0f);
        }
        super.render(drawContext, mouseX, mouseY, selected);
    }

    public static class ChangeListenerTextField extends ConfigOptionChangeListenerTextField {
        protected final Pair<String, String> defaultValue;
        private final WidgetStringMapEditEntry parent;

        public ChangeListenerTextField(GuiTextFieldGeneric textField, ButtonBase buttonReset, Pair<String, String> defaultValue, WidgetStringMapEditEntry parent) {
            super(null, textField, buttonReset);

            this.parent = parent;
            this.defaultValue = defaultValue;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField) {
            String key = parent.textFieldKey.getTextField().getText();
            String value = parent.textFieldValue.getTextField().getText();
            this.buttonReset.setEnabled(!key.equals(defaultValue.getLeft()) || !value.equals(defaultValue.getRight()));
            return false;
        }
    }

    private static class ListenerResetConfig implements IButtonActionListener {
        private final WidgetStringMapEditEntry parent;
        private final ButtonGeneric buttonReset;

        public ListenerResetConfig(ButtonGeneric buttonReset, WidgetStringMapEditEntry parent) {
            this.buttonReset = buttonReset;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            this.parent.textFieldKey.getTextField().setText(this.parent.defaultValue.getLeft());
            this.parent.textFieldValue.getTextField().setText(this.parent.defaultValue.getRight());
            this.buttonReset.setEnabled(false);
        }
    }

    private static class ListenerListActions implements IButtonActionListener {
        private final WidgetStringMapEditEntry.ButtonType type;
        private final WidgetStringMapEditEntry parent;

        public ListenerListActions(WidgetStringMapEditEntry.ButtonType type, WidgetStringMapEditEntry parent) {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == WidgetStringMapEditEntry.ButtonType.ADD) {
                this.parent.insertEntryBefore();
            } else if (this.type == WidgetStringMapEditEntry.ButtonType.REMOVE) {
                this.parent.removeEntry();
            } else {
                this.parent.moveEntry(this.type == WidgetStringMapEditEntry.ButtonType.MOVE_DOWN);
            }
        }
    }

    private enum ButtonType {
        ADD(MaLiLibIcons.PLUS, "malilib.gui.button.hovertext.add"),
        REMOVE(MaLiLibIcons.MINUS, "malilib.gui.button.hovertext.remove"),
        MOVE_UP(MaLiLibIcons.ARROW_UP, "malilib.gui.button.hovertext.move_up"),
        MOVE_DOWN(MaLiLibIcons.ARROW_DOWN, "malilib.gui.button.hovertext.move_down");

        private final MaLiLibIcons icon;
        private final String hoverTextkey;

        ButtonType(MaLiLibIcons icon, String hoverTextkey) {
            this.icon = icon;
            this.hoverTextkey = hoverTextkey;
        }

        public IGuiIcon getIcon() {
            return this.icon;
        }

        public String getDisplayName() {
            return StringUtils.translate(this.hoverTextkey);
        }
    }

    @Override
    public boolean hasPendingModifications() {
        if (this.textFieldKey != null && this.textFieldValue != null) {
            return !this.textFieldKey.getTextField().getText().equals(this.lastAppliedVKey) ||
                    !this.textFieldValue.getTextField().getText().equals(this.lastAppliedVValue);
        }
        return false;
    }

    @Override
    protected boolean onMouseClickedImpl(Click click, boolean doubleClick) {
        if (super.onMouseClickedImpl(click, doubleClick)) {
            return true;
        }

        boolean ret = false;

        if (this.textFieldKey != null) {
            ret |= this.textFieldKey.getTextField().mouseClicked(click, doubleClick);
        }
        if (this.textFieldValue != null) {
            ret |= this.textFieldValue.getTextField().mouseClicked(click, doubleClick);
        }

        return ret;
    }

    @Override
    public boolean onKeyTypedImpl(KeyInput input) {
        if (this.textFieldKey != null && this.textFieldKey.isFocused()) {
            if (input.key() == KeyCodes.KEY_ENTER) {
                this.applyNewValueToConfig();
                return true;
            } else {
                return this.textFieldKey.onKeyTyped(input);
            }
        }
        if (this.textFieldValue != null && this.textFieldValue.isFocused()) {
            if (input.key() == KeyCodes.KEY_ENTER) {
                this.applyNewValueToConfig();
                return true;
            } else {
                return this.textFieldValue.onKeyTyped(input);
            }
        }

        return false;
    }

    @Override
    protected boolean onCharTypedImpl(CharInput input) {
        if (this.textFieldKey != null && this.textFieldKey.onCharTyped(input)) {
            return true;
        }
        if (this.textFieldValue != null && this.textFieldValue.onCharTyped(input)) {
            return true;
        }

        return super.onCharTypedImpl(input);
    }
}
