package fi.dy.masa.malilib.config.options;

import java.util.Collection;
import java.util.HashSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigOptionValues;
import fi.dy.masa.malilib.config.IStringRepresentable;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.util.ListUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ImmutableCopy;

/**
 * Meant to be a Wrapper around the Post-Rewrite OptionListConfig, and make it work
 */
public class ConfigOptionValues<T extends OptionListConfigValue> extends ConfigBase<ConfigOptionValues<T>> implements IConfigOptionValues<T>, IStringRepresentable
{
	protected final T defaultValue;
	protected final ImmutableList<T> allValues;
	protected ImmutableSet<T> allowedValues;
	private T value;
	private T lastValue;

	public ConfigOptionValues(String name, T defaultValue, ImmutableList<T> allValues)
	{
		this(name, defaultValue, allValues, name + " Comment?", StringUtils.splitCamelCase(name), name);
	}

	public ConfigOptionValues(String name, T defaultValue, ImmutableList<T> allValues, String comment)
	{
		this(name, defaultValue, allValues, comment, StringUtils.splitCamelCase(name), name);
	}

	public ConfigOptionValues(String name, T defaultValue, ImmutableList<T> allValues, String comment, String prettyName)
	{
		this(name, defaultValue, allValues, comment, prettyName, name);
	}

	public ConfigOptionValues(String name, T defaultValue, ImmutableList<T> allValues, String comment, String prettyName, String translatedName)
	{
		super(ConfigType.OPTION_VALUES, name, comment, prettyName, translatedName);

		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.allValues = ImmutableCopy.of(allValues).toList();
		this.allowedValues = ImmutableCopy.of(allValues).toSet();
		this.updateLastOptionValue();
	}

	@Override
	public T getOptionValue()
	{
		return this.value;
	}

	@Override
	public T getDefaultOptionValue()
	{
		return this.defaultValue;
	}

	@Override
	public ImmutableList<T> getAllValues()
	{
		return this.allValues;
	}

	@Override
	public ImmutableSet<T> getAllowedValues()
	{
		return this.allowedValues;
	}

	@Override
	public void setAllowedValues(Collection<T> allowedValues)
	{
		ImmutableSet<T> allValuesSet = ImmutableCopy.of(this.allValues).toSet();
		ImmutableSet.Builder<T> builder = ImmutableSet.builder();

		for (T value : allowedValues)
		{
			if (allValuesSet.contains(value))
			{
				builder.add(value);
			}
		}

		this.allowedValues = builder.build();

		if (this.allowedValues.contains(this.getOptionValue()) == false)
		{
			this.cycleValue(false);
		}
	}

	@Override
	public void addAllowedValues(Collection<T> newAllowedValues)
	{
		HashSet<T> allowedValuesSet = new HashSet<>(this.allowedValues);
		allowedValuesSet.addAll(newAllowedValues);
		this.setAllowedValues(allowedValuesSet);
	}

	@Override
	public void removeAllowedValues(Collection<T> nonAllowedValues)
	{
		HashSet<T> allowedValuesSet = new HashSet<>(this.allowedValues);
		allowedValuesSet.removeAll(nonAllowedValues);
		this.setAllowedValues(allowedValuesSet);
	}

	@Override
	public void setOptionValue(T value)
	{
		if (this.allowedValues.contains(value))
		{
			this.updateLastOptionValue();
			T oldValue = this.value;
			this.value = value;

			if (oldValue != this.value)
			{
				this.onValueChanged();
			}
		}
	}

	@Override
	public void setOptionValueFromString(String value)
	{
		this.setValueFromString(value);
	}

	@Override
	public void cycleValue(boolean reverse)
	{
		this.setOptionValue(ListUtils.getNextEntry(this.allValues, this.value, reverse, this.allValues::contains));
	}

	@Override
	public T getLastOptionValue()
	{
		return this.lastValue;
	}

	@Override
	public void updateLastOptionValue()
	{
		this.lastValue = this.value;
	}

	@Override
	public boolean isModified()
	{
		return this.value != this.defaultValue;
	}

	@Override
	public boolean isModified(String newValue)
	{
		try
		{
			return BaseOptionListConfigValue.findValueByName(newValue, this.allValues) != this.defaultValue;
		}
		catch (Exception ignored) { }

		return true;
	}

	@Override
	public void resetToDefault()
	{
		this.setOptionValue(this.defaultValue);
	}

	@Override
	public String getStringValue()
	{
		return this.value.getName();
	}

	@Override
	public String getDefaultStringValue()
	{
		return this.defaultValue.getName();
	}

	@Override
	public void setValueFromString(String value)
	{
		this.setOptionValue(BaseOptionListConfigValue.findValueByName(value, this.allValues));
	}

	@Override
	public void setValueFromJsonElement(JsonElement element)
	{
		final T oldValue = this.value;

		try
		{
			if (element.isJsonPrimitive())
			{
				String temp = element.getAsString();

				try
				{
					this.value = temp != null ? BaseOptionListConfigValue.findValueByName(temp, this.allValues) : this.defaultValue;
				}
				catch (Exception ignored)
				{
					this.value = this.defaultValue;
				}
			}
			else
			{
				MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
			}

			if (!this.value.equals(oldValue) || this.isDirty())
			{
				this.markClean();

				if (!this.getLastOptionValue().equals(this.getOptionValue()))
				{
//                    MaLiLib.LOGGER.error("[OPTION/{}]: setValueFromJsonElement(): LV: [{}], OV: [{}], NV: [{}]", this.getName(),
//                                         this.getLastOptionListValue().getStringValue(),
//                                         oldValue.getStringValue(),
//                                         this.getOptionListValue().getStringValue()
//                    );

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
		return new JsonPrimitive(this.getStringValue());
	}
}
