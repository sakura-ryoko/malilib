package fi.dy.masa.malilib.config.options;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.*;
import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.gui.button.*;
import fi.dy.masa.malilib.gui.interfaces.IConfigInfoProvider;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.widgets.WidgetBlockStateIcon;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.wrappers.TextFieldType;
import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.world.level.block.state.BlockState;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.game.BlockUtils;

@ApiStatus.Experimental
public class ConfigBlockState extends ConfigBase<ConfigBlockState> implements IConfigBlockState
{
	public static final Codec<ConfigBlockState> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					                    PrimitiveCodec.STRING.fieldOf("name").forGetter(ConfigBase::getName),
					                    BlockState.CODEC.fieldOf("defaultValue").forGetter(get -> get.defaultValue),
					                    BlockState.CODEC.fieldOf("value").forGetter(get -> get.value),
					                    BlockState.CODEC.fieldOf("previousValue").forGetter(get -> get.previousValue),
					                    PrimitiveCodec.STRING.fieldOf("comment").forGetter(get -> get.comment),
					                    PrimitiveCodec.STRING.fieldOf("prettyName").forGetter(get -> get.prettyName),
					                    PrimitiveCodec.STRING.fieldOf("translatedName").forGetter(get -> get.translatedName)
			                    )
			                    .apply(instance, ConfigBlockState::new)
	);
	private final BlockState defaultValue;
	private BlockState value;
	private BlockState previousValue;

	public ConfigBlockState(String name, BlockState defaultValue)
	{
		this(name, defaultValue, name+" Comment?", StringUtils.splitCamelCase(name), name);
	}

	public ConfigBlockState(String name, BlockState defaultValue, String comment)
	{
		this(name, defaultValue, comment, StringUtils.splitCamelCase(name), name);
	}

	public ConfigBlockState(String name, BlockState defaultValue, String comment, String prettyName)
	{
		this(name, defaultValue, comment, prettyName, name);
	}

	public ConfigBlockState(String name, BlockState defaultValue, String comment, String prettyName, String translatedName)
	{
		super(ConfigType.BLOCK_STATE, name, comment, prettyName, translatedName);
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.updateLastBlockStateValue();
	}

	public ConfigBlockState(String name, BlockState defaultValue, BlockState value, BlockState previousValue, String comment, String prettyName, String translatedName)
	{
		this(name, defaultValue, comment, prettyName, translatedName);
		this.value = value;
		this.updateLastBlockStateValue();
	}

	@Override
	public BlockState getBlockStateValue()
	{
		return this.value;
	}

	@Override
	public BlockState getDefaultBlockStateValue()
	{
		return this.defaultValue;
	}

	@Override
	public BlockState getLastBlockStateValue()
	{
		return this.previousValue;
	}

	@Override
	public void setBlockStateValue(BlockState value)
	{
		this.updateLastBlockStateValue();
		this.value = value;

		if (this.previousValue.equals(this.value) == false)
		{
			this.onValueChanged();
		}
	}

	@Override
	public String getDefaultStringValue()
	{
		return BlockStateParser.serialize(this.defaultValue);
	}

	@Override
	public void setValueFromString(String value)
	{
		BlockUtils.getBlockStateFromString(value).ifPresentOrElse(this::setBlockStateValue, this::resetToDefault);
	}

	@Override
	public String getStringValue()
	{
		return BlockStateParser.serialize(this.value);
	}

	@Override
	public void updateLastBlockStateValue()
	{
		this.previousValue = this.value;
	}

	@Override
	public void resetToDefault()
	{
		this.setBlockStateValue(this.defaultValue);
	}

	@Override
	public boolean isModified()
	{
		return this.value.equals(this.defaultValue) == false;
	}

	@Override
	public boolean isModified(String newValue)
	{
		Optional<BlockState> opt = BlockUtils.getBlockStateFromString(newValue);
		return opt.map(bs -> !bs.equals(this.defaultValue)).orElse(false);
	}

	@Override
	public void setValueFromJsonElement(JsonElement element)
	{
		final BlockState oldValue = this.getBlockStateValue();

		try
		{
			AtomicReference<BlockState> temp = new AtomicReference<>();
			BlockState.CODEC.decode(JsonOps.INSTANCE, element)
			                .ifSuccess(res -> temp.set(res.getFirst()))
			                .ifError(err ->
					                         MaLiLib.LOGGER.error("ConfigBlockState: Exception reading from JSON; {}", err.error())
			                );

			this.value = temp.get();

			if (!this.value.equals(oldValue) || this.isDirty())
			{
				this.markClean();

				if (!Objects.equals(this.getLastBlockStateValue(), this.getBlockStateValue()))
				{
                    MaLiLib.LOGGER.error("[BLOCK_STATE/{}]: setValueFromJsonElement(): LV: [{}], OV: [{}], NV: [{}]", this.getName(),
                                         BlockStateParser.serialize(this.getLastBlockStateValue()),
                                         BlockStateParser.serialize(oldValue),
                                         BlockStateParser.serialize(this.getBlockStateValue())
                    );

					this.onValueChanged();
				}
			}
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
		}
	}

	@Override
	public JsonElement getAsJsonElement()
	{
		return BlockState.CODEC.encodeStart(JsonOps.INSTANCE, this.value).result().orElse(new JsonObject());
	}
	
	@Override public void addConfigOption(int x, int y, int labelWidth, int configWidth, WidgetConfigOption configOption) {
		
		y += 1;
		int configHeight = 20;
		
		String configName = getConfigGuiDisplayName();
		
		configOption.addLabel(x, y + 7, labelWidth, 8, 0xFFFFFFFF, configName);
		
		String comment;
		IConfigInfoProvider infoProvider = configOption.host.getHoverInfoProvider();
		
		if (infoProvider != null)
		{
			comment = infoProvider.getHoverInfo(this);
		}
		else
		{
			comment = getComment();
		}
		
		if (comment != null)
		{
			configOption.addConfigComment(x, y + 5, labelWidth, 12, comment);
		}
		
		x += labelWidth + 10;
		
		int resetX = x + configWidth + 2;
		
		configWidth -= 22; // adjust the width to match other configs due to the block icon display
		configOption.colorDisplayPosX = x + configWidth + 2;
		configOption.addWidget(new WidgetBlockStateIcon(configOption.colorDisplayPosX, y + 1, 18, 18, this));
		
		TextFieldType.STRING.setMaxLength(configOption.maxTextfieldTextLength);
		
		configOption.addConfigTextFieldEntry(x, y, resetX, configWidth, configHeight, this, TextFieldType.BLOCK_STATE);
		
		if (this instanceof IConfigSlider)
		{
			IGuiIcon icon = ((IConfigSlider) this).shouldUseSlider() ? MaLiLibIcons.BTN_TXTFIELD : MaLiLibIcons.BTN_SLIDER;
			ButtonGeneric toggleBtn = new ButtonGeneric(configOption.colorDisplayPosX, y + 2, icon);
			configOption.addButton(toggleBtn, new WidgetConfigOption.ListenerSliderToggle((IConfigSlider) this));
		}
	}
}
