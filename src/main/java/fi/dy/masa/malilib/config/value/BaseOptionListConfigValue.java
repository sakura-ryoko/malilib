package fi.dy.masa.malilib.config.value;

import java.util.List;
import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import fi.dy.masa.malilib.util.StringUtils;

/**
 * Post-ReWrite code
 */
public class BaseOptionListConfigValue implements OptionListConfigValue
{
    public static final Codec<BaseOptionListConfigValue> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.STRING.fieldOf("name").forGetter(get -> get.name),
                    PrimitiveCodec.STRING.fieldOf("translationKey").forGetter(get -> get.translationKey)
            ).apply(inst, BaseOptionListConfigValue::new)
    );

    protected final String name;
    protected final String translationKey;

    public BaseOptionListConfigValue(String name, String translationKey)
    {
        this.name = name;
        this.translationKey = translationKey;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    /**
     * Finds the value by the given name from the provided list.
     * If none of the entries match, then the first entry is returned as a fallback.
     */
    public static <T extends OptionListConfigValue> T findValueByName(String name, List<T> values)
    {
        return findValueByName(name, values, values.get(0));
    }

    /**
     * Finds the value by the given name from the provided list.
     * If none of the entries match, then the fallback value is returned.
     */
    public static <T extends OptionListConfigValue> T findValueByName(String name, List<T> values, @Nullable T fallback)
    {
        for (T val : values)
        {
            if (val.getName().equalsIgnoreCase(name))
            {
                return val;
            }
        }

        return fallback;
    }
}
