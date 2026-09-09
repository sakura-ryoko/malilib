package fi.dy.masa.malilib.gui;

import java.util.*;
import javax.annotation.Nullable;

import fi.dy.masa.malilib.gui.interfaces.ISliderCallback;
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.gui.widgets.WidgetSlider;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.IConfigBlockState;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.wrappers.TextFieldType;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.input.ScanCodes;

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

	private BlockState blockState;

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
	}

	@Override
	public void initGui()
	{
		super.initGui();

		int leftPaneWidth = MathUtils.max(this.modelSize, this.maxLength) + 24;
		int maxPropLabelWidth = 120;

		// this is bad but I can't be asked to fix it... proper way would be to return the heights in each method
		int propSize = this.blockState.getProperties().size();
		int modelSizeLines = (this.modelSize + (this.elementHeight + 2) + (this.buttonHeight + 4));
		int blockNameLines = ((this.elementHeight + 2) * 3) + (modelSizeLines + 6);
		int propLines = ((this.elementHeight + 2) * 3) * propSize;

		int largerSide = MathUtils.max(blockNameLines + 6, propLines + 10);
		int totalWidth = leftPaneWidth + maxPropLabelWidth + 30;
		totalWidth = MathUtils.clamp(totalWidth, 240, GuiUtils.getScaledWindowWidth() - 40);

		this.setTotalWidthAndHeight(totalWidth, (largerSide + 6));      // adjWidth, (largerSide + 6)
		this.setLeftSideWidth(leftPaneWidth);   //  + 2
		this.centerOnScreen();

		int x = this.dialogLeft + 10;
		int y = this.dialogTop + (this.elementHeight) + 2 + 6;
		int xCenter = this.dialogCenter + 10;
		int yCenter = y;

		// Block Name / Entry Box
		this.addBlockName(x, y);
		y += (this.elementHeight * 2) + 6;

		// Model Box Label
		int yScaled = this.getScaledCenterY() - (this.modelSize / 2) - this.elementHeight - 2;

		if (y > yScaled)
		{
			yScaled = y;
		}

		yScaled += this.addBlockStateDisplay(x, yScaled);

		// Model Pos
		this.modelX = (this.dialogLeftSideCenter - (this.modelSize / 2)); //  + 14
		this.modelY = yScaled;

		// Buttons (Centered on Left Pane)
		int yAdj = this.dialogBottom - this.buttonHeight - 4;

		var resetButton = this.createResetButton(0, yAdj, this.buttonHeight);
		resetButton.setX((this.dialogLeftSideCenter - resetButton.getWidth() / 2));

		// Properties List / Entry Boxes
		this.addBlockProperties(xCenter, yCenter);
	}

	private void reInitProperties() {
        GuiTextFieldGeneric oldWidget = this.textFieldBlockName;
		this.init();
		this.textFieldBlockName.setFocused(oldWidget.isFocused());
		this.textFieldBlockName.setCursorPosition(oldWidget.getCursorPosition());
//		this.textFieldBlockName.setHighlightPos(oldWidget.highl);
		this.setFocused(this.textFieldBlockName);
	}

	private void addBlockName(int x, int y)
	{
		String str = StringUtils.translate("malilib.gui.label.block_state_editor.block_name");
		this.addLabel(x, y, this.getStringWidth(str), this.elementHeight, COLOR_WHITE, str);
		y += this.elementHeight + 2;

		this.textFieldBlockName = new GuiTextFieldGeneric(x, y, this.maxLength + 2, this.elementHeight, this.font);
		this.textFieldBlockName.setValue(BuiltInRegistries.BLOCK.getKey(this.blockState.getBlock()).toString());
		this.textFieldBlockName.setMaxLength(this.maxLength);
		this.addTextField(this.textFieldBlockName, new BlockNameTextFieldListener(this), TextFieldType.BLOCK_ID);
	}

	private int addBlockStateDisplay(int x, int y)
	{
		String str = StringUtils.translate("malilib.gui.label.block_state_editor.block_display");
		int width = this.getStringWidth(str);
		int xAdj = x + (width / 2) + 2; //  + 14
		this.addLabel(xAdj, y, width, this.elementHeight, COLOR_WHITE, str);
		return this.elementHeight + 2;
	}

	private void addBlockProperties(int x, int y)
	{
		int count = 1;
		String str = StringUtils.translate("malilib.gui.label.block_state_editor.block_properties");
		this.addLabel(x, y, this.getStringWidth(str), this.elementHeight, COLOR_WHITE, str);
		y += this.elementHeight + 2;

		for (Property<?> prop : this.blockState.getProperties())
		{
			this.addEachBlockProperty(x, y, prop, count++);
			y += (this.elementHeight * 3) + 2;
		}
	}

	private <T extends Comparable<T>> void addEachBlockProperty(int x, int y, Property<@NonNull T> prop, int index)
	{
		String name = prop.getName();
		String str = StringUtils.translate("malilib.gui.label.block_state_editor.property.name", index, name);
		int propWidth = this.getStringWidth(str);
		this.addLabel(x, y, propWidth, this.elementHeight, COLOR_WHITE, str);
		y += this.elementHeight + 2;

		if (prop instanceof IntegerProperty integerProperty) {
			WidgetSlider slider = getSlider(x, y, prop.getPossibleValues().indexOf(this.blockState.getValue(prop)), integerProperty);
			this.addWidget(slider);
		} else {
			List<T> validValues = prop.getPossibleValues();
			WidgetDropDownList<T> dropDown = new WidgetDropDownList<>(x, y, this.maxLength+2, this.elementHeight + 2, 500, 10, validValues);
			dropDown.setSelectedEntryChangeCallback(t -> {
				this.blockState = this.blockState.setValue(prop, t);
			});
			T value = this.blockState.getValue(prop);
			dropDown.setSelectedEntry(value);
			this.addWidget(dropDown);
		}
	}

	private @NonNull WidgetSlider getSlider(int x, int y, int initialIndex, IntegerProperty integerProperty)
	{
		List<Integer> validValues = integerProperty.getPossibleValues();
		return new WidgetSlider(x, y, this.maxLength + 2, this.elementHeight + 6, new ISliderCallback()
		{
			private int valueIndex = initialIndex;

			@Override
			public int getMaxSteps()
			{
				return validValues.size() - 1;
			}

			@Override
			public double getValueRelative()
			{
				if (validValues.size() <= 1)
					return 0.0;

				return (double) valueIndex / (validValues.size() - 1);
			}

			@Override
			public void setValueRelative(double relativeValue)
			{
				if (validValues.size() <= 1) {
					valueIndex = 0;
					return;
				}

				valueIndex = (int) Math.round(
						relativeValue * (validValues.size() - 1)
				);
				valueIndex = Math.clamp(valueIndex, 0, validValues.size() - 1);

				blockState = blockState.setValue(integerProperty, validValues.get(valueIndex));
			}

			@Override
			public String getFormattedDisplayValue()
			{
				return String.valueOf(validValues.get(valueIndex));
			}
		});
	}

	private ButtonGeneric createResetButton(int x, int y, int height)
	{
        ButtonGeneric button = new ButtonGeneric(x, y, -1, height, ButtonType.RESET.getDisplayName());
		return this.addButton(button, (_, _) -> {
			this.blockState = this.config.getBlockStateValue();
			this.init();
		});
	}

	@Override
	public void removed()
	{
		try
		{
			this.config.setBlockStateValue(this.blockState);
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.error("GuiBlockStateEditor: Exception saving config; {}", e.getLocalizedMessage());
		}

		super.removed();
	}

	@Override
	public void extractRenderState(@NotNull GuiGraphicsExtractor ctx, int mouseX, int mouseY, float partialTicks)
	{
		if (this.getParent() != null)
		{
			// TODO sakura: decide whether or not this (0, 0) is better, it effectively
			//  removes the "white outline" from buttons in the parent screen when you
			//  hover over them. Would need to apply this to all GUIs that render their
			//  parent
			this.getParent().extractRenderState(ctx, 0, 0, partialTicks);
		}

		super.extractRenderState(ctx, mouseX, mouseY, partialTicks);
		this.drawBlockStateInGui(GuiContext.fromGuiGraphics(ctx));
	}

	private void drawBlockStateInGui(GuiContext ctx)
	{
		RenderUtils.renderModelInGui(ctx, this.modelX + 1, this.modelY + 1, this.modelSize, this.blockState, 0.75F, 0.0F, 0.125f * Mth.PI, ((Util.getMillis() % 16000f) / 16000f) * Mth.TWO_PI, Mth.PI);
//		RenderUtils.renderModelInGui(ctx, this.modelX + 1, this.modelY + 1, this.modelSize, this.blockState, 0.75F, 0.0F, 0, 0, 0);
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
		if (input.key() == ScanCodes.SCAN_ESCAPE && this.dialogHandler != null)
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

			if (id != null)
			{
				Optional<Block> block = BuiltInRegistries.BLOCK.getOptional(id);
				if (block.isPresent()) {
					this.gui.blockState = block.get().defaultBlockState().withPropertiesOf(this.gui.blockState);
					this.gui.reInitProperties();
					return true;
				}
			}

			return false;
		}
	}

	private enum ButtonType
	{
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
