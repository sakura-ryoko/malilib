package fi.dy.masa.malilib.gui;

import java.util.*;
import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.IConfigBlockState;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.wrappers.TextFieldType;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.KeyCodes;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.StringUtils;

@ApiStatus.Experimental
public class GuiBlockStateEditor extends GuiDialogSplitBase
{
	protected final IConfigBlockState config;
	protected final String configName;
	protected final @Nullable IDialogHandler dialogHandler;
	protected int titleWidth;
	protected int modelSize;
	protected int modelX;
	protected int modelY;
	protected int maxLength;
	protected int elementHeight;
	protected int buttonHeight;
	protected GuiTextFieldGeneric textFieldBlockName;
	protected HashMap<String, GuiTextFieldGeneric> textFieldBlockProps = new HashMap<>();

	private BlockState blockState;
	private Block block;
	private Collection<Property<?>> props;
	private Identifier tempBlockId = null;
	private final HashMap<String, String> tempProps = new HashMap<>();

	public GuiBlockStateEditor(IConfigBlockState config, String name, @Nullable IDialogHandler dialogHandler, Screen parent)
	{
		this.config = config;
		this.configName = name;
		this.dialogHandler = dialogHandler;

		if (this.dialogHandler == null)
		{
			this.setParent(parent);
		}

		this.title = GuiBase.TXT_BOLD + StringUtils.translate("malilib.gui.title.block_state_editor", this.configName) + GuiBase.TXT_RST;
		this.titleWidth = this.getStringWidth(this.title);
		this.elementHeight = 12;
		this.modelSize = 64;
		this.buttonHeight = 20;
		this.maxLength = MathUtils.max(this.modelSize - 12, 128);
		this.blockState = this.config.getBlockStateValue();
		this.refreshBlockState();
	}

	private <T extends Comparable<T>> void refreshBlockState()
	{
		this.textFieldBlockProps.clear();
		this.tempProps.clear();
		this.block = this.blockState.getBlock();
		this.props = this.blockState.getProperties();
		this.tempBlockId = BuiltInRegistries.BLOCK.getKey(this.block);

		for (Property<?> prop : this.props)
		{
			@SuppressWarnings("unchecked")
			Property<T> entry = (Property<T>) prop;
			String name = prop.getName();
			T value = this.blockState.getValue(entry);
			String valStr = value.toString().toLowerCase();
			this.tempProps.put(name, valStr);
		}

		final int propSize = this.props.size();
		final int modelSizeLines = (this.modelSize + (this.elementHeight + 2) + (this.buttonHeight + 4));
		final int blockNameLines = ((this.elementHeight + 2) * 3) + (modelSizeLines + 6);
		final int propLines = ((this.elementHeight + 2) * 4) * propSize;
		final int largerSide = MathUtils.max(blockNameLines + 6, propLines + 10);
		final int adjWidth = MathUtils.max(MathUtils.max((this.maxLength * 2) + 6, (this.titleWidth * 2) + 6), 256) + 8;

		// (this.elementHeight + 2)
		this.setTotalWidthAndHeight(adjWidth, (largerSide + 6));
		this.setLeftSideWidth(MathUtils.max(this.modelSize, this.maxLength) + 2);
		this.centerOnScreen();
		this.init(this.dialogTotalWidth, this.dialogTotalHeight);
	}

	private <T extends Comparable<T>> void trySaveBlockState() throws Exception
	{
		Optional<Block> opt = BuiltInRegistries.BLOCK.getOptional(this.tempBlockId);

		if (opt.isPresent())
		{
			this.block = opt.get();
		}
		else
		{
			throw new InvalidPropertiesFormatException(String.format("Block %s not found", this.tempBlockId.toString()));
		}

		this.blockState = this.block.defaultBlockState();
		this.props = this.blockState.getProperties();
		CompoundTag tag = new CompoundTag();
		CompoundTag tagProps = new CompoundTag();

		tag.putString("Name", BuiltInRegistries.BLOCK.getKey(this.block).toString());

		for (Property<?> prop : this.props)
		{
			@SuppressWarnings("unchecked")
			Property<T> entry = (Property<T>) prop;
			final String name = prop.getName();
			final String tmp = this.tempProps.get(name);

			if (tmp != null && !tmp.isEmpty())
			{
				Optional<T> newVal = entry.getValue(tmp);
				newVal.ifPresent(value -> tagProps.putString(name, value.toString().toLowerCase()));
			}
			else
			{
				T value = this.blockState.getValue(entry);
				tagProps.putString(name, value.toString().toLowerCase());
			}
		}

		tag.put("Properties", tagProps);        // Ugly method, but if it works? ~_^ (Unlike this.blockstate.setValue() ?)

		if (MaLiLibReference.DEBUG_MODE)
		{
			MaLiLib.LOGGER.error("trySaveBlockState: nbt: {}", tag.toString());
		}

		this.blockState = BlockState.CODEC.parse(NbtOps.INSTANCE, tag).getPartialOrThrow();
		this.refreshBlockState();
	}

	@Override
	public void initGui()
	{
		this.clearElements();

		int x = this.dialogLeft + 10;
		int y = this.dialogTop + (this.elementHeight) + 2 + 6;
		final int xCenter = this.dialogCenter + 10;
		final int yCenter = y;

		// todo Left Side
		// Block Name / Entry Box
		this.addBlockName(x, y);
		y += (this.elementHeight * 2) + 6;

		// Model Box Label
		int yScaled = this.getScaledCenterY() - (this.modelSize / 2) - this.elementHeight - 2;

		if (y > yScaled)
		{
			yScaled = y;
		}

		this.addBlockStateDisplay(x, yScaled);
		yScaled += (this.elementHeight) + 2;

		// Model Pos
		this.modelX = (this.dialogLeftSideCenter - (this.modelSize / 2)) + 14;
		this.modelY = yScaled;

		// Buttons (Centered on Left Pane)
		final int buttonALen = this.getStringWidth(ButtonType.UPDATE.getDisplayName()) + 10;
		final int buttonBLen = this.getStringWidth(ButtonType.RESET.getDisplayName()) + 10;
		final int totalButtonWidth = (buttonALen + 2 + buttonBLen + 2);
		int xAdj = (this.dialogLeftSideCenter - ((totalButtonWidth) / 2)) + 14;
		int yAdj = this.dialogBottom - this.buttonHeight - 4;

		xAdj += this.createButton(xAdj, yAdj, this.buttonHeight, ButtonType.UPDATE);
		xAdj += this.createButton(xAdj, yAdj, this.buttonHeight, ButtonType.RESET);

		// todo Right Side
		// Properties List / Entry Boxes
		this.addBlockProperties(xCenter, yCenter);
	}

	private void addBlockName(int x, int y)
	{
		final String str = StringUtils.translate("malilib.gui.label.block_state_editor.block_name");
		this.addLabel(x, y, this.getStringWidth(str), this.elementHeight, COLOR_WHITE, str);
		y += this.elementHeight + 2;

		this.textFieldBlockName = new GuiTextFieldGeneric(x, y, this.maxLength + 2, this.elementHeight, this.font);
		this.textFieldBlockName.setValue(this.tempBlockId.toString());
		this.textFieldBlockName.setMaxLength(this.maxLength);
		this.addTextField(this.textFieldBlockName, new BlockNameTextFieldListener(this), TextFieldType.BLOCK_ID);
		y += this.elementHeight + 2;
	}

	private void addBlockStateDisplay(int x, int y)
	{
		final String str = StringUtils.translate("malilib.gui.label.block_state_editor.block_display");
		final int width = this.getStringWidth(str);
		final int xAdj = (this.dialogLeftSideCenter - (width / 2)) + 14;
		this.addLabel(xAdj, y, width, this.elementHeight, COLOR_WHITE, str);
		y += this.elementHeight + 2;
	}

	private void addBlockProperties(int x, int y)
	{
		int count = 1;
		final String str = StringUtils.translate("malilib.gui.label.block_state_editor.block_properties");
		this.addLabel(x, y, this.getStringWidth(str), this.elementHeight, COLOR_WHITE, str);
		y += this.elementHeight + 2;

		for (Property<?> prop : this.props)
		{
			this.addEachBlockProperty(x, y, prop, count++);
			y += (this.elementHeight * 4) + 2;
		}
	}

	private <T extends Comparable<T>> void addEachBlockProperty(int x, int y, Property<@NonNull T> prop, int index)
	{
		final String name = prop.getName();
		final List<T> validValues = prop.getPossibleValues();
		String str = StringUtils.translate("malilib.gui.label.block_state_editor.property.name", index, name);
		final int propWidth = this.getStringWidth(str);
		this.addLabel(x, y, propWidth, this.elementHeight, COLOR_WHITE, str);
		y += this.elementHeight + 2;

		List<String> validValueStrings = new ArrayList<>();

		for (T value : validValues)
		{
			validValueStrings.add(value.toString().toLowerCase());
		}

		// Truncate large lists of Integers, such as '1 - 15'
		if (prop instanceof IntegerProperty)
		{
			String adjStr = String.format("[%s - %s]", validValueStrings.getFirst(), validValueStrings.getLast());
			str = StringUtils.translate("malilib.gui.label.block_state_editor.property.potenial_values", adjStr);
		}
		else
		{
			str = StringUtils.translate("malilib.gui.label.block_state_editor.property.potenial_values",
			                            StringUtils.getClampedDisplayStringRenderlen(validValueStrings, (this.dialogRightSideWidth - propWidth) - 30, "[ ", " ]"));
		}

		this.addLabel(x, y, this.getStringWidth(str), this.elementHeight, COLOR_WHITE, str);
		y += this.elementHeight + 2;
		this.textFieldBlockProps.put(name, new GuiTextFieldGeneric(x, y, this.maxLength + 2, this.elementHeight, this.font));

		T value = null;
		String valStr = "";

		try
		{
			value = this.blockState.getValue(prop);
		}
		catch (Exception ignored) {}

		if (value != null)
		{
			valStr = value.toString().toLowerCase();
		}

		this.textFieldBlockProps.get(name).setValue(valStr);
		this.textFieldBlockProps.get(name).setMaxLength(this.maxLength);
		this.addTextField(this.textFieldBlockProps.get(name), new PropertyValueTextFieldListener(this, name), TextFieldType.VALID_STRING.setValidStrings(validValueStrings));
		y += this.elementHeight + 2;
	}

	private int createButton(int x, int y, int height, ButtonType type)
	{
		final String text = type.getDisplayName();
		ButtonGeneric button = new ButtonGeneric(x, y, -1, height, text);
		this.addButton(button, this.createActionListener(type));
		return button.getWidth() + 2;
	}

	@Override
	public void removed()
	{
		try
		{
			this.trySaveBlockState();
			this.config.setBlockStateValue(this.blockState);
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.error("GuiBlockStateEditor: Exception saving config; {}", e.getLocalizedMessage());
		}

		super.removed();
	}

	@Override
	public void render(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTicks)
	{
		if (this.getParent() != null)
		{
			this.getParent().render(ctx, mouseX, mouseY, partialTicks);
		}

		super.render(ctx, mouseX, mouseY, partialTicks);
		this.drawBlockStateInGui(GuiContext.fromGuiGraphics(ctx), mouseX, mouseY);
	}

	private void drawBlockStateInGui(GuiContext ctx, int mouseX, int mouseY)
	{
		RenderUtils.renderModelInGui(ctx, this.modelX + 1, this.modelY + 1, this.modelSize, this.blockState, 0.75F, 0.0F);
	}

	@Override
	protected void drawScreenBackground(GuiContext ctx, int mouseX, int mouseY)
	{
		// Background
		RenderUtils.drawOutlinedBox(ctx, this.dialogLeft, this.dialogTop, this.dialogTotalWidth, this.dialogTotalHeight, 0xFF000000, COLOR_HORIZONTAL_BAR);
		this.drawDividerBars(ctx, mouseX, mouseY);

		// Model Display Box
		RenderUtils.drawOutlinedBox(ctx, this.modelX, this.modelY, (this.modelSize), (this.modelSize), 0xFF202020, COLOR_HORIZONTAL_BAR);
	}

	@Override
	protected void drawTitle(GuiContext ctx, int mouseX, int mouseY, float partialTicks)
	{
		this.drawStringWithShadow(ctx, this.title, this.dialogLeft + 10, this.dialogTop + 6, COLOR_WHITE);
	}

	@Override
	public boolean keyPressed(@NotNull KeyEvent input)
	{
		return this.onKeyTyped(input);
	}

	@Override
	public boolean onKeyTyped(KeyEvent input)
	{
		if (input.key() == KeyCodes.KEY_ESCAPE && this.dialogHandler != null)
		{
			this.dialogHandler.closeDialog();
			return true;
		}
		else
		{
			return super.onKeyTyped(input);
		}
	}

	protected record BlockNameTextFieldListener(GuiBlockStateEditor gui)
			implements ITextFieldListener<GuiTextFieldGeneric>
	{
		@Override
		public boolean onTextChange(GuiTextFieldGeneric textField)
		{
			Identifier id = Identifier.tryParse(textField.getValue());

			if (id != null && BuiltInRegistries.BLOCK.getOptional(id).isPresent())
			{
				this.gui.tempBlockId = id;
				return true;
			}

			return false;
		}
	}

	protected record PropertyValueTextFieldListener(GuiBlockStateEditor gui, String name)
			implements ITextFieldListener<GuiTextFieldGeneric>
	{
		@Override
		public boolean onTextChange(GuiTextFieldGeneric textField)
		{
			final String val = textField.getValue();

			for (Property<?> prop : this.gui().props)
			{
				if (prop.getName().equals(this.name()) && prop.getValue(val).isPresent())
				{
					this.gui().tempProps.put(this.name(), val.toLowerCase());
					return true;
				}
			}

			return false;
		}
	}

	private ButtonListener createActionListener(ButtonType type)
	{
		return new ButtonListener(type, this);
	}

	private record ButtonListener(ButtonType type, GuiBlockStateEditor gui)
			implements IButtonActionListener
	{
		@Override
		public void actionPerformedWithButton(ButtonBase button, int mouseButton)
		{
			if (this.type() == ButtonType.UPDATE)
			{
				try
				{
					this.gui().trySaveBlockState();
				}
				catch (Exception e)
				{
					MaLiLib.LOGGER.error("GuiBlockStateEditor: Exception saving block state; {}", e.getLocalizedMessage());
				}
			}
			else if (this.type() == ButtonType.RESET)
			{
				this.gui().blockState = this.gui().config.getBlockStateValue();
				this.gui().refreshBlockState();
			}
		}
	}

	private enum ButtonType
	{
		UPDATE          ("malilib.gui.button.update"),
		RESET           ("malilib.gui.button.reset"),
		;

		private final String labelKey;

		ButtonType(String labelKey)
		{
			this.labelKey = labelKey;
		}

		public String getDisplayName()
		{
			return StringUtils.translate(this.labelKey);
		}
	}
}
