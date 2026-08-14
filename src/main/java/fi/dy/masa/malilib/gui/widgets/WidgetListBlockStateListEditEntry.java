package fi.dy.masa.malilib.gui.widgets;

import java.util.List;
import java.util.Objects;

import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import fi.dy.masa.malilib.config.IConfigBlockStateList;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerTextField;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.wrappers.TextFieldType;
import fi.dy.masa.malilib.interfaces.IBlockStateConsumer;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.game.BlockUtils;

public class WidgetListBlockStateListEditEntry extends WidgetConfigOptionBase<BlockState>
{
	protected final WidgetListBlockStateListEdit parent;
	protected final BlockState defaultState;
	protected final String defaultValueString;
	protected final int listIndex;
	protected final boolean isOdd;

	public WidgetListBlockStateListEditEntry(int x, int y, int width, int height, int listIndex, boolean isOdd,
	                                         BlockState initialValue, BlockState defaultState,
	                                         WidgetListBlockStateListEdit parent)
	{
		super(x, y, width, height, parent, initialValue, listIndex);

		this.listIndex = listIndex;
		this.isOdd = isOdd;
		this.defaultState = defaultState;
		this.defaultValueString = stateToString(defaultState);
		this.initialStringValue = stateToString(initialValue);
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
			bx = this.addTextField(textFieldX, y + 1, resetX, textFieldWidth, 20, this.initialStringValue);

			this.addWidget(new WidgetBlockStateIcon(textFieldX + textFieldWidth + 2, y + 1, 18, 18, String.format("%s_%d", this.parent.config.getName(), listIndex), initialValue, new ListenerConfigChange(this)));

			this.addListActionButton(bx, by, ButtonType.ADD);
			bx += bOff;

			this.addListActionButton(bx, by, ButtonType.REMOVE);
			bx += bOff;

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
		else
		{
			this.addListActionButton(bx, by, ButtonType.ADD);
		}
	}

	protected static String stateToString(BlockState value)
	{
		return BlockStateParser.serialize(Objects.requireNonNullElseGet(value, Blocks.AIR::defaultBlockState));
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
		field.setValue(initialValue);

		ButtonGeneric resetButton = this.createResetButton(resetX, y, field);
		ChangeListenerTextField listenerChange = new ChangeListenerTextField(field, resetButton, this.defaultValueString);
		ListenerResetConfig listenerReset = new ListenerResetConfig(resetButton, this);

		this.addTextField(field, listenerChange, TextFieldType.STRING.setMaxLength(this.maxTextfieldTextLength));
		this.addButton(resetButton, listenerReset);

		return resetButton.getX() + resetButton.getWidth() + 4;
	}

	protected ButtonGeneric createResetButton(int x, int y, GuiTextFieldGeneric textField)
	{
		String labelReset = StringUtils.translate("malilib.gui.button.reset.caps");
		ButtonGeneric resetButton = new ButtonGeneric(x, y, -1, 20, labelReset);
		resetButton.setEnabled(!textField.getValue().equals(this.defaultValueString));

		return resetButton;
	}

	@Override
	public boolean wasConfigModified()
	{
		return !this.isDummy() && !this.textField.textField().getValue().equals(this.initialStringValue);
	}

	@Override
	public void applyNewValueToConfig()
	{
		BlockState value = this.entry;

		if (this.textField != null && !this.textField.textField().getValue().isEmpty())
		{
			final String textValue = this.textField.textField().getValue();
			value = BlockUtils.getBlockStateFromString(textValue).orElse(this.defaultState);
		}

		this.applyNewValueToConfig(value);
	}

	protected void applyNewValueToConfig(BlockState value)
	{
		if (!this.isDummy())
		{
			IConfigBlockStateList config = this.parent.getConfig();
			List<BlockState> list = config.getBlockStates();

			if (list.size() > this.listIndex)
			{
				list.set(this.listIndex, value);
				this.lastAppliedValue = stateToString(value);
				config.setBlockStates(list);
				config.markDirty();
				config.setModified();
			}
		}
	}

	private void insertEntryBefore()
	{
		List<BlockState> list = this.parent.getConfig().getBlockStates();
		final int size = list.size();
		int index = this.listIndex < 0 ? size : (Math.min(this.listIndex, size));
		list.add(index, this.defaultState);
		this.parent.getConfig().markDirty();
		this.parent.getConfig().setModified();
		this.parent.refreshEntries();
		this.parent.markConfigsModified();
	}

	private void removeEntry()
	{
		List<BlockState> list = this.parent.getConfig().getBlockStates();
		final int size = list.size();

		if (this.listIndex >= 0 && this.listIndex < size)
		{
			list.remove(this.listIndex);
			this.parent.getConfig().markDirty();
			this.parent.getConfig().setModified();
			this.parent.refreshEntries();
			this.parent.markConfigsModified();
		}
	}

	private void moveEntry(boolean down)
	{
		List<BlockState> list = this.parent.getConfig().getBlockStates();
		final int size = list.size();

		if (this.listIndex >= 0 && this.listIndex < size)
		{
			BlockState tmp;
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
		final int size = this.parent.getConfig().getBlockStates().size();
		return (this.listIndex >= 0 && this.listIndex < size) &&
				((down && this.listIndex < (size - 1)) || (!down && this.listIndex > 0));
	}

	@Override
	public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected)
	{
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
		protected final String defaultValue;

		public ChangeListenerTextField(GuiTextFieldGeneric textField, ButtonBase buttonReset, String defaultValue)
		{
			super(null, textField, buttonReset);

			this.defaultValue = defaultValue;
		}

		@Override
		public boolean onTextChange(GuiTextFieldGeneric textField)
		{
			this.buttonReset.setEnabled(!this.textField.getValue().equals(this.defaultValue));
			return false;
		}
	}

	protected record ListenerConfigChange(WidgetListBlockStateListEditEntry parent)
			implements IBlockStateConsumer
	{
		@Override
		public void setState(BlockState state)
		{
			if (this.parent.textField != null)
			{
				this.parent.textField.textField().setValue(stateToString(state));
			}

			this.parent.applyNewValueToConfig(state);
		}
	}

	private record ListenerResetConfig(ButtonGeneric buttonReset, WidgetListBlockStateListEditEntry parent)
			implements IButtonActionListener
	{
		@Override
		public void actionPerformedWithButton(ButtonBase button, int mouseButton)
		{
			if (this.parent.textField != null)
			{
				this.parent.textField.textField().setValue(this.parent.defaultValueString);
				this.parent.parent.applyPendingModifications();
				this.buttonReset.setEnabled(!this.parent.textField.textField().getValue().equals(this.parent.defaultValueString));
			}

			this.parent.parent.refreshEntries();
		}
	}

	private record ListenerListActions(ButtonType type, WidgetListBlockStateListEditEntry parent)
			implements IButtonActionListener
	{
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

	protected enum ButtonType
	{
		ADD         (MaLiLibIcons.PLUS,         "malilib.gui.button.hovertext.add"),
		REMOVE      (MaLiLibIcons.MINUS,        "malilib.gui.button.hovertext.remove"),
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
