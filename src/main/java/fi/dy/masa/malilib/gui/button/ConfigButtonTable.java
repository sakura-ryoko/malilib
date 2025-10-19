package fi.dy.masa.malilib.gui.button;

import fi.dy.masa.malilib.config.IConfigTable;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTableEdit;
import fi.dy.masa.malilib.gui.interfaces.IConfigGui;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.gui.Click;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        for (List<Object> entry : this.config.getTable()) {
            if (addDivider) {
                sb.append("; ");
            }
            boolean addDividerEntry = false;
            for (Object entryPart : entry) {
                if (addDividerEntry) {
                    sb.append(", ");
                }
                sb.append(entryPart.toString());
                addDividerEntry = true;
            }
                addDivider = true;
            }
            sb.append("}");

            this.displayString = sb.toString();
        }
    }
