package fi.dy.masa.malilib.gui.button;

import java.util.ArrayList;
import java.util.List;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.IConfigTable;
import fi.dy.masa.malilib.config.options.table.TableRow;
import fi.dy.masa.malilib.config.options.table.type.*;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTableEdit;
import fi.dy.masa.malilib.gui.interfaces.IConfigGui;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;

import net.minecraft.client.gui.Click;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

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
		List<String> list = new ArrayList<>();

//		boolean addDivider = false;
		for (TableRow row : this.config.getTable())
		{
			StringBuilder sb = new StringBuilder();
			boolean addDividerEntry = false;

//			if (addDivider)
//			{
//				sb.append("; ");
//			}

			for (Entry entryPart : row.list())
			{
				if (addDividerEntry)
				{
					sb.append(", ");
				}
				// honestly this is starting to become impossible :sob:
				if (entryPart.getType() == EntryTypes.STRING)
				{
                    assert entryPart instanceof StringEntry;
					sb.append(((StringEntry) entryPart).getValue());
				}
				if (entryPart.getType() == EntryTypes.LABEL)
				{
                    assert entryPart instanceof LabelEntry;
                    sb.append(((LabelEntry) entryPart).getValue().label());
				}
				else if (entryPart.getType() == EntryTypes.INTEGER)
				{
                    assert entryPart instanceof IntegerEntry;
                    sb.append(((IntegerEntry) entryPart).getValue());
				}
				else if (entryPart.getType() == EntryTypes.DOUBLE)
				{
                    assert entryPart instanceof DoubleEntry;
                    sb.append(((DoubleEntry) entryPart).getValue());
				}
				else if (entryPart.getType() == EntryTypes.BOOLEAN)
				{
                    assert entryPart instanceof BooleanEntry;
                    sb.append(((BooleanEntry) entryPart).getValue());
//                } else if (entryPart.getType() == EntryTypes.KEYBIND) {
//                    sb.append(((KeybindEntry) entryPart).getKeybind().getKeysDisplayString());
				}
				else
				{
//					throw new IllegalStateException();
					MaLiLib.debugLog("ConfigButtonTable: entryType Exception. " + entryPart.getAsJsonObject().toString());
				}

				addDividerEntry = true;
			}

			list.add(sb.toString());
//			addDivider = true;
		}

		this.displayString = StringUtils.getClampedDisplayStringRenderlen(list, this.width - 10, "{", "}");
	}
}
