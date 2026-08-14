package fi.dy.masa.malilib.gui.widgets;

import java.util.Collection;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import fi.dy.masa.malilib.config.IConfigBlockStateList;
import fi.dy.masa.malilib.gui.GuiBlockStateListEdit;

public class WidgetListBlockStateListEdit extends WidgetListConfigOptionsBase<BlockState, WidgetListBlockStateListEditEntry>
{
	protected final BlockState air = Blocks.AIR.defaultBlockState();
	protected final IConfigBlockStateList config;

	public WidgetListBlockStateListEdit(int x, int y, int width, int height, int configWidth, GuiBlockStateListEdit parent)
	{
		super(x, y, width, height, configWidth);

		this.config = parent.getConfig();
	}

	protected IConfigBlockStateList getConfig()
	{
		return this.config;
	}

	@Override
	protected Collection<BlockState> getAllEntries()
	{
		return this.config.getBlockStates();
	}

	@Override
	protected void reCreateListEntryWidgets()
	{
		// Add a dummy entry that allows adding the first actual string to the list
		if (this.listContents.size() == 0)
		{
			this.listWidgets.clear();
			this.maxVisibleBrowserEntries = 1;

			int x = this.posX + 2;
			int y = this.posY + 4 + this.browserEntriesOffsetY;

			this.listWidgets.add(this.createListEntryWidget(x, y, -1, false, this.air));
			this.scrollBar.setMaxValue(0);
		}
		else
		{
			super.reCreateListEntryWidgets();
		}
	}

	@Override
	protected WidgetListBlockStateListEditEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, BlockState entry)
	{
		IConfigBlockStateList config = this.config;

		if (listIndex >= 0 && listIndex < config.getBlockStates().size())
		{
			BlockState defaultValue = config.getDefaultBlockStates().size() > listIndex
			                          ? config.getDefaultBlockStates().get(listIndex)
			                          : this.air;

			return new WidgetListBlockStateListEditEntry(x, y, this.browserEntryWidth, this.browserEntryHeight,
			                                             listIndex, isOdd,
			                                             config.getBlockStates().get(listIndex), defaultValue,
			                                             this);
		}
		else
		{
			return new WidgetListBlockStateListEditEntry(x, y, this.browserEntryWidth, this.browserEntryHeight,
			                                             listIndex, isOdd,
			                                             this.air, this.air,
			                                             this);
		}
	}
}
