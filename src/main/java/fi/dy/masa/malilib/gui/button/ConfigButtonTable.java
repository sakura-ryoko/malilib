package fi.dy.masa.malilib.gui.button;

import fi.dy.masa.malilib.config.IConfigTable;
import fi.dy.masa.malilib.config.options.table.TableRow;
import fi.dy.masa.malilib.config.options.table.type.Entry;
import fi.dy.masa.malilib.config.options.table.type.EntryTypes;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTableEdit;
import fi.dy.masa.malilib.gui.interfaces.IConfigGui;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.gui.Click;
import org.jetbrains.annotations.Nullable;

public class ConfigButtonTable extends ButtonGeneric {
    private final IConfigTable config;
    private final IConfigGui configGui;
    @Nullable
    private final IDialogHandler dialogHandler;

    public ConfigButtonTable(int x, int y, int width, int height, IConfigTable config, IConfigGui configGui, @Nullable IDialogHandler dialogHandler) {
        super(x, y, width, height, "");

        this.config = config;
        this.configGui = configGui;
        this.dialogHandler = dialogHandler;

        this.updateDisplayString();
    }

    @Override
    protected boolean onMouseClickedImpl(Click click, boolean doubleClick) {
        super.onMouseClickedImpl(click, doubleClick);

        if (this.dialogHandler != null) {
            this.dialogHandler.openDialog(new GuiTableEdit(this.config, this.configGui, this.dialogHandler, null));
        } else {
            GuiBase.openGui(new GuiTableEdit(this.config, this.configGui, null, GuiUtils.getCurrentScreen()));
        }

        return true;
    }

    @Override
    public void updateDisplayString() {
        if (this.config.getDisplayString() != null) {
            this.displayString = this.config.getDisplayString();
            return;
        }
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        boolean addDivider = false;
        for (TableRow row : this.config.getTable()) {
            if (addDivider) {
                sb.append("; ");
            }
            boolean addDividerEntry = false;
            for (Entry entryPart : row.list) {
                if (addDividerEntry) {
                    sb.append(", ");
                }
                if (entryPart.getType() == EntryTypes.STRING) {
                    sb.append(((fi.dy.masa.malilib.config.options.table.type.StringEntry) entryPart).getValue());
                } else if (entryPart.getType() == EntryTypes.INTEGER) {
                    sb.append(((fi.dy.masa.malilib.config.options.table.type.IntegerEntry) entryPart).getValue());
                } else if (entryPart.getType() == EntryTypes.DOUBLE) {
                    sb.append(((fi.dy.masa.malilib.config.options.table.type.DoubleEntry) entryPart).getValue());
                } else {
                    throw new IllegalStateException();
                }
                addDividerEntry = true;
            }
                addDivider = true;
            }
            sb.append("}");

            this.displayString = sb.toString();
        }
    }
