package fi.dy.masa.malilib.config.options;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import net.minecraft.world.level.block.state.BlockState;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBlockStateList;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ImmutableCopy;
import fi.dy.masa.malilib.util.data.json.JsonUtils;

public class ConfigBlockStateList extends ConfigBase<ConfigBlockStateList> implements IConfigBlockStateList
{
	private final ImmutableList<@NotNull BlockState> defaultStates;
	private final List<BlockState> states = new ArrayList<>();
	private final List<BlockState> lastStates = new ArrayList<>();

	public ConfigBlockStateList(String name, ImmutableList<@NotNull BlockState> defaultStates)
	{
		this(name, defaultStates, "", StringUtils.splitCamelCase(name), name);
	}

	public ConfigBlockStateList(String name, ImmutableList<@NotNull BlockState> defaultStates, String comment)
	{
		this(name, defaultStates, comment, StringUtils.splitCamelCase(name), name);
	}

	public ConfigBlockStateList(String name, ImmutableList<@NotNull BlockState> defaultStates, String comment, String prettyName)
	{
		this(name, defaultStates, comment, prettyName, name);
	}

	public ConfigBlockStateList(String name, ImmutableList<@NotNull BlockState> defaultStates, String comment, String prettyName, String translatedName)
	{
		super(ConfigType.BLOCK_STATE_LIST, name, comment, prettyName, translatedName);
		this.defaultStates = defaultStates;
		this.states.addAll(defaultStates);
		this.lastStates.addAll(defaultStates);
	}

	@Override
	public List<BlockState> getBlockStates()
	{
		return this.states;
	}

	@Override
	public ImmutableList<BlockState> getDefaultBlockStates()
	{
		return this.defaultStates;
	}

	@Override
	public void setBlockStates(List<BlockState> states)
	{
		if (!this.states.equals(states))
		{
			this.updateLastBlockStateListValue();
			this.states.clear();
			this.states.addAll(states);
			this.onValueChanged();
		}
	}

	@Override
	public void setModified()
	{
		this.markClean();
		this.onValueChanged();
	}

	@Override
	public boolean isModified()
	{
		return !this.states.equals(this.defaultStates);
	}

	@Override
	public void resetToDefault()
	{
		this.setBlockStates(ImmutableCopy.of(this.defaultStates).toList());
	}

	private void addBlockState(BlockState state)
	{
		this.states.add(state);
	}

	@Override
	public List<BlockState> getLastBlockStateListValue()
	{
		return this.lastStates;
	}

	@Override
	public void updateLastBlockStateListValue()
	{
		this.lastStates.clear();
		this.lastStates.addAll(ImmutableCopy.of(this.states).toList());
	}

	@Override
	public JsonElement getAsJsonElement()
	{
		JsonArray arr = new JsonArray();

		for (BlockState state : this.states)
		{
			JsonObject obj = JsonUtils.getBlockStateAsObject(state);

			if (!obj.isEmpty())
			{
				arr.add(obj);
			}
		}

		return arr;
	}

	@Override
	public void setValueFromJsonElement(JsonElement element)
	{
		ImmutableList<BlockState> oldList = ImmutableCopy.of(this.states).toList();
		this.states.clear();

		try
		{
			if (element.isJsonArray())
			{
				JsonArray arr = element.getAsJsonArray();
				final int count = arr.size();

				for (int i = 0; i < count; ++i)
				{
					JsonElement entry = arr.get(i);
					BlockState temp;

					temp = JsonUtils.getAsBlockState(entry, null).orElse(null);

					if (temp != null)
					{
						this.addBlockState(temp);
					}
				}

				if (!oldList.equals(this.states) || this.isDirty())
				{
					this.markClean();

					if (!this.getLastBlockStateListValue().equals(this.getBlockStates()))
					{
//                        MaLiLib.LOGGER.error("[BLOCK-STATE-LIST/{}]: setValueFromJsonElement(): LV: [{}], OV: [{}], NV: [{}]", this.getName(),
//                                             this.getLastBlockStateListValue().size(),
//                                             oldList.size(),
//                                             this.getBlockStates().size()
//                        );

						this.setModified();
					}
				}
			}
			else
			{
				MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
			}
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
		}
	}
}
