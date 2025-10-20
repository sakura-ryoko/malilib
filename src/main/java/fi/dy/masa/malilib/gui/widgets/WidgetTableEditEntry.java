package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.config.IConfigTable;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerTextField;
import fi.dy.masa.malilib.config.options.ConfigTable;
import fi.dy.masa.malilib.gui.GuiTextFieldDouble;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.GuiTextFieldInteger;
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

import java.util.ArrayList;
import java.util.List;

public class WidgetTableEditEntry extends WidgetConfigOptionBase<ConfigTable.Entry> {
    protected final WidgetListTableEdit parent;
    protected final ConfigTable.Entry defaultValue;
    protected final int listIndex;
    protected final boolean isOdd;
    private final List<Class<?>> types;

    private final List<TextFieldWrapper<? extends GuiTextFieldGeneric>> textFields =  new ArrayList<>();

    protected ConfigTable.Entry initialValue;
    private final List<String> lastAppliedValues = new ArrayList<>();

    public WidgetTableEditEntry(int x, int y, int width, int height,
                                int listIndex, boolean isOdd, ConfigTable.Entry initialValue, ConfigTable.Entry defaultValue,
                                WidgetListTableEdit parent, List<Class<?>> types) {
        super(x, y, width, height, parent, initialValue, listIndex);

        this.listIndex = listIndex;
        this.isOdd = isOdd;
        this.defaultValue = defaultValue;
        this.initialValue = initialValue;
        this.parent = parent;
        this.types = types;
        int textFieldX = x + 5;
        int by = y + 4;
        int bOff = 18;

        if (!this.isDummy()) {
            int offset = 0;
            int bx = x + width - 30;
            if (this.parent.config.showEntryNumbers()) {
                this.addLabel(x + 2, y + 6, 20, 12, 0xC0C0C0C0, String.format("%3d:", listIndex + 1));
                textFieldX += 15;
            }

            if (this.parent.getConfig().allowNewEntry()) {
                this.addListActionButton(bx - offset, by, ButtonType.ADD);
                offset += bOff;

                this.addListActionButton(bx - offset, by, ButtonType.REMOVE);
                offset += bOff;
            }

            if (this.canBeMoved(true)) {
                this.addListActionButton(bx - offset, by, ButtonType.MOVE_DOWN);
            }

            offset += bOff;

            if (this.canBeMoved(false)) {
                this.addListActionButton(bx - offset, by, ButtonType.MOVE_UP);
            }
            offset += bOff;
            int totalTextFieldWidth = (bx - offset + 9) - textFieldX;
            bx = this.addTextFields(textFieldX, y + 1, bx - offset + 10, totalTextFieldWidth, 20, initialValue, types);
        } else {
            this.addListActionButton(textFieldX, by, ButtonType.ADD);
        }
    }

    protected boolean isDummy() {
        return this.listIndex < 0;
    }

    protected void addListActionButton(int x, int y, ButtonType type) {
        ButtonGeneric button = new ButtonGeneric(x, y, type.getIcon(), type.getDisplayName());
        ListenerListActions listener = new ListenerListActions(type, this);
        this.addButton(button, listener);
    }

    protected int addTextFields(int x, int y, int resetX, int configWidth, int configHeight, ConfigTable.Entry initialValue, List<Class<?>> types) {
        ButtonGeneric resetButton = this.createResetButton(resetX, y);
        ChangeListenerTextField listenerChange = new ChangeListenerTextField(resetButton, this.defaultValue, this);
        ListenerResetConfig listenerReset = new ListenerResetConfig(resetButton, this);

        boolean resetEnabled = false;

        configWidth -= resetButton.getWidth();

        for (int i = 0; i < types.size(); i++) {
            Class<?> type = types.get(i);
            Object value = initialValue.list.get(i);

            GuiTextFieldGeneric tf = switch (value) {
                case String ignored when type == String.class ->
                        new GuiTextFieldGeneric(x + i * (configWidth / types.size()) + 2, y + 1, configWidth / types.size() - 4, configHeight - 3, this.textRenderer);
                case Integer ignored when type == Integer.class ->
                        new GuiTextFieldInteger(x + i * (configWidth / types.size()) + 2, y + 1, configWidth / types.size() - 4, configHeight - 3, this.textRenderer);
                case Double ignored when type == Double.class ->
                        new GuiTextFieldDouble (x + i * (configWidth / types.size()) + 2, y + 1, configWidth / types.size() - 4, configHeight - 3, this.textRenderer);
                default ->
                        throw new IllegalStateException("Unsupported type: " + type.getName() + " with value: " + value.getClass());
            };
            tf.setMaxLength(this.maxTextfieldTextLength);
            tf.setText(value.toString());
            TextFieldWrapper<? extends GuiTextFieldGeneric> wrapper = new TextFieldWrapper<>(tf, listenerChange);
            this.parent.addTextField(wrapper);
            this.textFields.add(wrapper);

            resetEnabled = resetEnabled || !value.toString().equals(this.defaultValue.list.get(i).toString());
        }

        this.addButton(resetButton, listenerReset);

        resetButton.setEnabled(resetEnabled);

        return resetButton.getX() + resetButton.getWidth() + 4;
    }

    protected ButtonGeneric createResetButton(int x, int y) {
        String labelReset = StringUtils.translate("malilib.gui.button.reset.caps");
        ButtonGeneric resetButton = new ButtonGeneric(x, y, -1, 20, labelReset);

        resetButton.setX(x - resetButton.getWidth());
        return resetButton;
    }

    @Override
    public boolean wasConfigModified() {
        if (this.isDummy()) {
            return false;
        }

        for (int i = 0; i < this.textFields.size(); i++) {
            TextFieldWrapper<? extends GuiTextFieldGeneric> tfw = this.textFields.get(i);
            Class<?> type = this.types.get(i);
            String text = tfw.getTextField().getText();
            Object initial = this.initialValue.list.get(i);

            if (type == String.class || type == Integer.class || type == Double.class) {
                if (!text.equals(String.valueOf(initial))) {
                    return true;
                }
            } else {
                throw new IllegalStateException("Unsupported type: " + type.getName());
            }
        }

        return false;
    }

    @Override
    public void applyNewValueToConfig() {
        if (!this.isDummy()) {
            IConfigTable config = this.parent.getConfig();
            List<ConfigTable.Entry> list = config.getTable();

            if (list.size() > this.listIndex) {
                ConfigTable.Entry temp = new ConfigTable.Entry();
                lastAppliedValues.clear();
                for (int i = 0; i < this.textFields.size(); i++) {
                    TextFieldWrapper<? extends GuiTextFieldGeneric> tfw = this.textFields.get(i);
                    Class<?> type = this.types.get(i);
                    String text = tfw.getTextField().getText();
                    lastAppliedValues.add(text);
                    if (type == String.class) {
                        temp.list.add(text);
                    } else if (type == Integer.class) {
                        temp.list.add(Integer.parseInt(text));
                    } else if (type == Double.class) {
                        temp.list.add(Double.parseDouble(text));
                    } else {
                        throw new IllegalStateException("Unsupported type: " + type.getName());
                    }
                }

                list.set(this.listIndex, temp);
                config.setModified();
            }
        }
    }

    private void insertEntryBefore() {
        List<ConfigTable.Entry> list = this.parent.getConfig().getTable();
        final int size = list.size();
        int index = this.listIndex < 0 ? size : (Math.min(this.listIndex, size));
        list.add(index, ConfigTable.getDummy(types));
        this.parent.getConfig().setModified();
        this.parent.refreshEntries();
        this.parent.markConfigsModified();
    }

    private void removeEntry() {
        List<ConfigTable.Entry> list = this.parent.getConfig().getTable();
        final int size = list.size();

        if (this.listIndex >= 0 && this.listIndex < size) {
            list.remove(this.listIndex);
            this.parent.getConfig().setModified();
            this.parent.refreshEntries();
            this.parent.markConfigsModified();
        }
    }

    private void moveEntry(boolean down) {
        List<ConfigTable.Entry> list = this.parent.getConfig().getTable();
        final int size = list.size();

        if (this.listIndex >= 0 && this.listIndex < size) {
            ConfigTable.Entry tmp;
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
        final int size = this.parent.getConfig().getTable().size();
        return (this.listIndex >= 0 && this.listIndex < size) &&
                ((down && this.listIndex < (size - 1)) || (!down && this.listIndex > 0));
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, boolean selected) {
        super.render(drawContext, mouseX, mouseY, selected);

        if (this.isOdd) {
            RenderUtils.drawRect(drawContext, this.x, this.y, this.width, this.height, 0x20FFFFFF);
        } else {
            RenderUtils.drawRect(drawContext, this.x, this.y, this.width, this.height, 0x30FFFFFF);
        }

        this.drawSubWidgets(drawContext, mouseX, mouseY);

        for (TextFieldWrapper<? extends GuiTextFieldGeneric> wrapper : this.textFields) {
            if (wrapper != null) wrapper.getTextField().render(drawContext, mouseX, mouseY, 0f);
        }
        super.render(drawContext, mouseX, mouseY, selected);
    }

    public static class ChangeListenerTextField extends ConfigOptionChangeListenerTextField {
        protected final ConfigTable.Entry defaultValue;
        private final WidgetTableEditEntry parent;

        public ChangeListenerTextField(ButtonBase buttonReset, ConfigTable.Entry defaultValue, WidgetTableEditEntry parent) {
            super(null, null, buttonReset);

            this.parent = parent;
            this.defaultValue = defaultValue;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric ignored) {
            for (int i = 0; i < this.parent.types.size(); i++) {
                TextFieldWrapper<? extends GuiTextFieldGeneric> wrapper = this.parent.textFields.get(i);
                String defaultText = this.defaultValue.list.get(i).toString();

                if (!wrapper.getTextField().getText().equals(defaultText)) {
                    this.buttonReset.setEnabled(true);
                    return false;
                }
            }
            this.buttonReset.setEnabled(false);
            return false;
        }
    }

    private static class ListenerResetConfig implements IButtonActionListener {
        private final WidgetTableEditEntry parent;
        private final ButtonGeneric buttonReset;

        public ListenerResetConfig(ButtonGeneric buttonReset, WidgetTableEditEntry parent) {
            this.buttonReset = buttonReset;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            for (int i = 0; i < this.parent.types.size(); i++) {
                TextFieldWrapper<? extends GuiTextFieldGeneric> wrapper = this.parent.textFields.get(i);
                String defaultText = this.parent.defaultValue.list.get(i).toString();
                wrapper.getTextField().setText(defaultText);
            }
            this.buttonReset.setEnabled(false);
        }
    }

    private static class ListenerListActions implements IButtonActionListener {
        private final ButtonType type;
        private final WidgetTableEditEntry parent;

        public ListenerListActions(ButtonType type, WidgetTableEditEntry parent) {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == ButtonType.ADD) {
                this.parent.insertEntryBefore();
            } else if (this.type == ButtonType.REMOVE) {
                this.parent.removeEntry();
            } else {
                this.parent.moveEntry(this.type == ButtonType.MOVE_DOWN);
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
        for (int i = 0; i < this.textFields.size(); i++) {
            TextFieldWrapper<? extends GuiTextFieldGeneric> tfw = this.textFields.get(i);
            String text = tfw.getTextField().getText();
            String lastApplied = i < this.lastAppliedValues.size() ? this.lastAppliedValues.get(i) : null;

            if (!text.equals(lastApplied)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean onMouseClickedImpl(Click click, boolean doubleClick) {
        if (super.onMouseClickedImpl(click, doubleClick)) {
            return true;
        }

        boolean ret = false;

        for (TextFieldWrapper<? extends GuiTextFieldGeneric> tfw : this.textFields) {
            if (tfw != null) {
                ret |= tfw.getTextField().mouseClicked(click, doubleClick);
            }
        }
        return ret;
    }

    @Override
    public boolean onKeyTypedImpl(KeyInput input) {
        for (TextFieldWrapper<? extends GuiTextFieldGeneric> tfw : this.textFields) {
            if (tfw != null && tfw.getTextField().isFocused()) {
                if (input.key() == KeyCodes.KEY_ENTER) {
                    this.applyNewValueToConfig();
                    return true;
                } else {
                    return tfw.onKeyTyped(input);
                }
            }
        }
        return false;
    }

    @Override
    protected boolean onCharTypedImpl(CharInput input) {
        for (TextFieldWrapper<? extends GuiTextFieldGeneric> tfw : this.textFields) {
            if (tfw != null && tfw.onCharTyped(input)) {
                return true;
            }
        }

        return super.onCharTypedImpl(input);
    }
}
