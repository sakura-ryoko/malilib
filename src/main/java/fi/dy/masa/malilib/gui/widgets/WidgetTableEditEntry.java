package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.config.IConfigTable;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerTextField;
import fi.dy.masa.malilib.config.options.table.ConfigTable;
import fi.dy.masa.malilib.config.options.table.TableRow;
import fi.dy.masa.malilib.config.options.table.type.*;
import fi.dy.masa.malilib.gui.GuiTextFieldDouble;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.GuiTextFieldInteger;
import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
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

import java.util.ArrayList;
import java.util.List;

public class WidgetTableEditEntry extends WidgetConfigOptionBase<TableRow> {
    protected final WidgetListTableEdit parent;
    protected final TableRow defaultValue;
    protected final int listIndex;
    protected final boolean isOdd;
    private final List<EntryTypes> types;
    private final List<Entry> entries;

    private final List<TextFieldWrapper<? extends GuiTextFieldGeneric>> textFields =  new ArrayList<>();

    // You're probably going to hate me for this, but...
    private final List<Pair<ConfigButtonKeybind, WidgetKeybindSettings>> keybindWidgets = new ArrayList<>();

    protected TableRow initialValue;
    private List<String> lastAppliedValues = new ArrayList<>();

    public WidgetTableEditEntry(int x, int y, int width, int height,
                                int listIndex, boolean isOdd, TableRow initialValue, TableRow defaultValue,
                                WidgetListTableEdit parent, List<EntryTypes> types) {
        super(x, y, width, height, parent, initialValue, listIndex);

        this.listIndex = listIndex;
        this.isOdd = isOdd;
        this.defaultValue = defaultValue;

        this.initialValue = initialValue;
        this.entries = initialValue.list;

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
            bx = this.addWidgets(textFieldX, y + 2, bx - offset + 10, totalTextFieldWidth, 22, initialValue, types);
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

    protected int addWidgets(int x, int y, int resetX, int configWidth, int configHeight, TableRow initialValue, List<EntryTypes> types) {
        ButtonGeneric resetButton = this.createResetButton(resetX, y);
        ChangeListenerTextField listenerChange = new ChangeListenerTextField(resetButton, this.defaultValue, this);
        ListenerResetConfig listenerReset = new ListenerResetConfig(resetButton, this);

        boolean resetEnabled = false;

        configWidth -= resetButton.getWidth();

        for (int i = 0; i < types.size(); i++) {
            EntryTypes type = types.get(i);
            Entry value = initialValue.list.get(i);

            if (type == EntryTypes.STRING || type == EntryTypes.INTEGER || type == EntryTypes.DOUBLE) {
                GuiTextFieldGeneric tf = switch (value) {
                    // why the hell is either the reset button or the text widgets Y/height off by half a pixel???
                    case StringEntry ignored when type == EntryTypes.STRING ->
                            new GuiTextFieldGeneric(x + i * (configWidth / types.size()) + 2, y, configWidth / types.size() - 4, configHeight - 3, this.textRenderer);
                    case IntegerEntry ignored when type == EntryTypes.INTEGER ->
                            new GuiTextFieldInteger(x + i * (configWidth / types.size()) + 2, y, configWidth / types.size() - 4, configHeight - 3, this.textRenderer);
                    case DoubleEntry ignored when type == EntryTypes.DOUBLE ->
                            new GuiTextFieldDouble(x + i * (configWidth / types.size()) + 2, y, configWidth / types.size() - 4, configHeight - 3, this.textRenderer);
                    default ->
                            throw new IllegalStateException("Unsupported type: " + type.name() + " with value: " + value.getType().name());
                };
                tf.setMaxLength(this.maxTextfieldTextLength);
                tf.setText(Entry.getString(value));
                TextFieldWrapper<? extends GuiTextFieldGeneric> wrapper = new TextFieldWrapper<>(tf, listenerChange);
                this.parent.addTextField(wrapper);
                this.textFields.add(wrapper);
                this.keybindWidgets.add(null);
            } else if (type == EntryTypes.KEYBIND) {
                KeybindEntry keybindEntry = (KeybindEntry) value;
                ConfigButtonKeybind keybindButton = new ConfigButtonKeybind(
                        x + i * (configWidth / types.size()) + 2,
                        y,
                        configWidth / types.size() - 10 - (configHeight - 6),
                        configHeight -3,
                        keybindEntry.getKeybind(),
                        null);

                WidgetKeybindSettings settingsWidget = new WidgetKeybindSettings(
                        x + (i + 1) * (configWidth / types.size()) - 22,
                        y,
                        20,
                        20,
                        keybindEntry.getKeybind(),
                        "",
                        null,
                        null);
                keybindButton.updateDisplayString();
                this.keybindWidgets.add(new Pair<>(keybindButton, settingsWidget));
                this.subWidgets.add(keybindButton);
                this.subWidgets.add(settingsWidget);
                this.textFields.add(null);
            } else {
                throw new IllegalStateException("Unsupported type: " + type.name());
            }

            resetEnabled = resetEnabled || value.wasConfigModified(this.defaultValue.get(i));
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

        for (int i = 0; i < this.entries.size(); i++) {
            Entry entry = this.entries.get(i);
            if (entry.wasConfigModified(this.initialValue.list.get(i))) {
                return true;
            }
        }
        return false;

//        for (int i = 0; i < this.textFields.size(); i++) {
//            TextFieldWrapper<? extends GuiTextFieldGeneric> tfw = this.textFields.get(i);
//            EntryTypes type = this.types.get(i);
//            String text = tfw.getTextField().getText();
//            Object initial = this.initialValue.list.get(i);
//
//            if (!text.equals(String.valueOf(initial))) {
//                return true;
//            }
//        }
//
//        return false;
    }

    @Override
    public void applyNewValueToConfig() {
        if (!this.isDummy()) {
            IConfigTable config = this.parent.getConfig();
            List<TableRow> list = config.getTable();

            if (list.size() > this.listIndex) {
                TableRow temp = new TableRow();
                lastAppliedValues.clear();

                for (int i = 0; i < this.entries.size(); i++) {
                    Entry entry = this.entries.get(i);
                    EntryTypes type = entry.getType();
                    if (type == EntryTypes.STRING || type == EntryTypes.INTEGER || type == EntryTypes.DOUBLE) {
                        TextFieldWrapper<? extends GuiTextFieldGeneric> tfw = this.textFields.get(i);
                        String text = tfw.getTextField().getText();
                        lastAppliedValues.add(text);
                        if (type == EntryTypes.STRING) {
                            temp.list.add(StringEntry.of(text));
                        } else if (type == EntryTypes.INTEGER) {
                            temp.list.add(IntegerEntry.of(Integer.parseInt(text)));
                        } else if (type == EntryTypes.DOUBLE) {
                            temp.list.add(DoubleEntry.of(Double.parseDouble(text)));
                        }
                    } else if (type == EntryTypes.KEYBIND) {
                        assert this.entries.get(i) instanceof KeybindEntry;
                        KeybindEntry keybindEntry = (KeybindEntry) this.entries.get(i);
                        temp.list.add(keybindEntry);
                        lastAppliedValues.add(keybindEntry.getStringValue());
                    }
                }

                for (int i = 0; i < this.textFields.size(); i++) {
                }

                list.set(this.listIndex, temp);
                config.setModified();
            }
        }
    }

    private void insertEntryBefore() {
        List<TableRow> list = this.parent.getConfig().getTable();
        final int size = list.size();
        int index = this.listIndex < 0 ? size : (Math.min(this.listIndex, size));
        list.add(index, ConfigTable.getDummy(types));
        this.parent.getConfig().setModified();
        this.parent.refreshEntries();
        this.parent.markConfigsModified();
    }

    private void removeEntry() {
        List<TableRow> list = this.parent.getConfig().getTable();
        final int size = list.size();

        if (this.listIndex >= 0 && this.listIndex < size) {
            list.remove(this.listIndex);
            this.parent.getConfig().setModified();
            this.parent.refreshEntries();
            this.parent.markConfigsModified();
        }
    }

    private void moveEntry(boolean down) {
        List<TableRow> list = this.parent.getConfig().getTable();
        final int size = list.size();

        if (this.listIndex >= 0 && this.listIndex < size) {
            TableRow tmp;
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
        for (Pair<ConfigButtonKeybind, WidgetKeybindSettings> pair : this.keybindWidgets) {
            if (pair != null) {
                ConfigButtonKeybind button = pair.getLeft();
                WidgetKeybindSettings settings = pair.getRight();
                if (button != null) button.render(drawContext, mouseX, mouseY, selected);
                if (settings != null) settings.render(drawContext, mouseX, mouseY, selected);
            }
        }
        super.render(drawContext, mouseX, mouseY, selected);
    }

    @Override
    public boolean onMouseClicked(Click click, boolean doubleClick) {
        for (Pair<ConfigButtonKeybind, WidgetKeybindSettings> pair : this.keybindWidgets) {
            if (pair != null) {
                ConfigButtonKeybind button = pair.getLeft();
                if (button != null) {
                    if (button.isMouseOver((int) click.x(), (int) click.y())) {
                        boolean selectedPre = button.isSelected();
                        button.onMouseClicked(click, doubleClick);

                        if (!selectedPre) {
                            button.onSelected();
                        }

                        System.out.println(pair.getRight().keybind.getStringValue());

                        return true;
                    } else if (button.isSelected()) {
                        button.onClearSelection();

                        return true;
                    }
                }
            }
        }
        return super.onMouseClicked(click, doubleClick);
    }

    @Override
    public boolean onKeyTyped(KeyInput input) {
        for (Pair<ConfigButtonKeybind, WidgetKeybindSettings> pair : this.keybindWidgets) {
            if (pair != null) {
                ConfigButtonKeybind button = pair.getLeft();
                if (button != null) {
                    if (button.isSelected()) {
                        button.onKeyPressed(input.key());

                        if (input.key() == KeyCodes.KEY_ESCAPE) {
                            button.onClearSelection();
                        }
                        this.parent.getConfig().setModified();
                        this.parent.markConfigsModified();

                        // need to set enable the reset button here

                        return true;
                    }
                }
            }
        }
        return super.onKeyTyped(input);
    }

    public static class ChangeListenerTextField extends ConfigOptionChangeListenerTextField {
        protected final TableRow defaultValue;
        private final WidgetTableEditEntry parent;

        public ChangeListenerTextField(ButtonBase buttonReset, TableRow defaultValue, WidgetTableEditEntry parent) {
            super(null, null, buttonReset);

            this.parent = parent;
            this.defaultValue = defaultValue;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric ignored) {
            for (int i = 0; i < this.parent.types.size(); i++) {
                TextFieldWrapper<? extends GuiTextFieldGeneric> wrapper = this.parent.textFields.get(i);
                if (wrapper == null) {
                    continue;
                }
                String defaultText = Entry.getString(this.defaultValue.list.get(i));

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
            for (int i = 0; i < this.parent.textFields.size(); i++) {
                TextFieldWrapper<? extends GuiTextFieldGeneric> wrapper = this.parent.textFields.get(i);
                if (wrapper != null) {
                    String defaultText = Entry.getString(this.parent.defaultValue.list.get(i));
                    wrapper.getTextField().setText(defaultText);
                } else {
                    Entry entry = this.parent.entry.get(i);
                    if (entry instanceof KeybindEntry keybindEntry) {
                        KeybindEntry defaultValue = (KeybindEntry) this.parent.defaultValue.list.get(i);
                        keybindEntry.getKeybind().setValueFromString(defaultValue.getKeybind().getStringValue());
                        keybindEntry.getKeybind().setSettings(defaultValue.getKeybind().getSettings());
                        this.parent.keybindWidgets.get(i).getLeft().updateDisplayString();
                    }
                }
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
        if (this.textFields.isEmpty()) {
            assert this.isDummy();
            return false;
        }
        for (int i = 0; i < this.entries.size(); i++) {
            Entry entry = this.entries.get(i);
            String lastApplied = i < this.lastAppliedValues.size() ? this.lastAppliedValues.get(i) : null;

            if (entry.getType() != EntryTypes.KEYBIND) {
                TextFieldWrapper<? extends GuiTextFieldGeneric> tfw = this.textFields.get(i);
                String text = tfw.getTextField().getText();

                if (!text.equals(lastApplied)) {
                    return true;
                }
            } else {
                assert this.entries.get(i) instanceof KeybindEntry;
                KeybindEntry entry1 = (KeybindEntry) this.entries.get(i);

                if (!entry1.getStringValue().equals(lastApplied)) {
                    return true;
                }
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
