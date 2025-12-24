package fi.dy.masa.malilib.gui.button;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fi.dy.masa.malilib.config.IConfigTable;
import fi.dy.masa.malilib.config.options.table.TableRow;
import fi.dy.masa.malilib.config.options.table.type.*;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTableEdit;
import fi.dy.masa.malilib.gui.interfaces.IConfigGui;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.Click;

@ApiStatus.Experimental
public class ConfigButtonTable extends ButtonGeneric
{
	private final IConfigTable config;
	private final IConfigGui configGui;
	@Nullable
	private final IDialogHandler dialogHandler;

	public ConfigButtonTable(int x, int y, int width, int height, IConfigTable config, IConfigGui configGui, @Nullable IDialogHandler dialogHandler)
	{
		super(x, y, width, height, "");

		this.config = config;
		this.configGui = configGui;
		this.dialogHandler = dialogHandler;

		this.updateDisplayString();
	}

	@Override
	protected boolean onMouseClickedImpl(Click click, boolean doubleClick)
	{
		super.onMouseClickedImpl(click, doubleClick);

		if (this.dialogHandler != null)
		{
			this.dialogHandler.openDialog(new GuiTableEdit(this.config, this.configGui, this.dialogHandler, null));
		}
		else
		{
			GuiBase.openGui(new GuiTableEdit(this.config, this.configGui, null, GuiUtils.getCurrentScreen()));
		}

		return true;
	}

	@Override
	public void updateDisplayString()
	{
		if (this.config.getDisplayString() != null)
		{
			this.displayString = this.config.getDisplayString();
			return;
		}
        // alternative way I guess
//        List<String> list = this.config.getTable().stream()
//                .map(row -> row.list().stream()
//                        .map(Entry::asString)
//                        .collect(Collectors.joining(", ")))
//                .toList();

		List<String> list = new ArrayList<>();

		for (TableRow row : this.config.getTable())
		{
            String result = row.list().stream()
                    .map(Entry::asString)
                    .collect(Collectors.joining(", "));
			list.add(result);
		}

		this.displayString = StringUtils.getClampedDisplayStringRenderlen(list, this.width - 20, "{", "}");
	}
}
