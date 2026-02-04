package fi.dy.masa.malilib.config.options;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ExtraCodecs;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigStringList;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ImmutableCopy;

public class ConfigStringList extends ConfigBase<ConfigStringList> implements IConfigStringList
{
    public static final Codec<ConfigStringList> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.STRING.fieldOf("name").forGetter(ConfigBase::getName),
                    ExtraCodecs.compactListCodec(PrimitiveCodec.STRING).fieldOf("defaultValue").forGetter(get -> get.defaultValue.stream().toList()),
                    ExtraCodecs.compactListCodec(PrimitiveCodec.STRING).fieldOf("values").forGetter(get -> get.strings),
                    PrimitiveCodec.STRING.fieldOf("comment").forGetter(get -> get.comment),
                    PrimitiveCodec.STRING.fieldOf("prettyName").forGetter(get -> get.prettyName),
                    PrimitiveCodec.STRING.fieldOf("translatedName").forGetter(get -> get.translatedName)
            ).apply(inst, ConfigStringList::new)
    );
    private final ImmutableList<@NotNull String> defaultValue;
    private final List<String> strings = new ArrayList<>();
    private final List<String> lastStrings = new ArrayList<>();

    public ConfigStringList(String name, ImmutableList<@NotNull String> defaultValue)
    {
        this(name, defaultValue, name+" Comment?", StringUtils.splitCamelCase(name), name);
    }

    public ConfigStringList(String name, ImmutableList<@NotNull String> defaultValue, String comment)
    {
        this(name, defaultValue, comment, StringUtils.splitCamelCase(name), name);
    }

    public ConfigStringList(String name, ImmutableList<@NotNull String> defaultValue, String comment, String prettyName)
    {
        this(name, defaultValue, comment, prettyName, name);
    }

    public ConfigStringList(String name, ImmutableList<@NotNull String> defaultValue, String comment, String prettyName, String translatedName)
    {
        super(ConfigType.STRING_LIST, name, comment, prettyName, translatedName);

        this.defaultValue = defaultValue;
        this.strings.addAll(defaultValue);
        this.updateLastStringListValue();
    }

    private ConfigStringList(String name, List<String> defaultValue, List<String> values, String comment, String prettyName, String translatedName)
    {
        this(name, ImmutableList.copyOf(defaultValue), comment, prettyName, translatedName);
        this.strings.addAll(values);
    }

    @Override
    public List<String> getStrings()
    {
        return this.strings;
    }

    @Override
    public ImmutableList<@NotNull String> getDefaultStrings()
    {
        return this.defaultValue;
    }

    @Override
    public void setStrings(List<String> strings)
    {
        if (this.strings.equals(strings) == false)
        {
            this.updateLastStringListValue();
            this.strings.clear();
            this.strings.addAll(strings);
            this.setModified();
        }
    }

    @Override
    public void setModified()
    {
        this.markClean();
        this.onValueChanged();
    }

    @Override
    public void resetToDefault()
    {
        this.setStrings(this.defaultValue);
    }

    @Override
    public boolean isModified()
    {
        return this.strings.equals(this.defaultValue) == false;
    }

    @Override
    public List<String> getLastStringListValue()
    {
        return this.lastStrings;
    }

    @Override
    public void updateLastStringListValue()
    {
        this.lastStrings.clear();
        this.lastStrings.addAll(ImmutableCopy.of(this.strings).toList());
    }

	private void addString(String str)
	{
		this.strings.add(str);
	}

    @Override
    public JsonElement getAsJsonElement()
    {
        JsonArray arr = new JsonArray();

        for (String str : this.strings)
        {
            arr.add(new JsonPrimitive(str));
        }

        return arr;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
		ImmutableList<String> oldList = ImmutableCopy.of(this.strings).toList();
        this.strings.clear();

        try
        {
            if (element.isJsonArray())
            {
                JsonArray arr = element.getAsJsonArray();
                final int count = arr.size();

                for (int i = 0; i < count; ++i)
                {
                    String temp = arr.get(i).getAsString();

                    if (temp != null)
                    {
                        this.addString(temp);
                    }
                }

				if (!oldList.equals(this.strings) || this.isDirty())
				{
                    this.markClean();

                    if (!this.getLastStringListValue().equals(this.getStrings()))
                    {
//                        MaLiLib.LOGGER.error("[STRING-LIST/{}]: setValueFromJsonElement(): LV: [{}], OV: [{}], NV: [{}]", this.getName(),
//                                             this.getLastStringListValue().size(),
//                                             oldList.size(),
//                                             this.getStrings().size()
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
