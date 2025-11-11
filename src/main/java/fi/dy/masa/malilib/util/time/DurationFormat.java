package fi.dy.masa.malilib.util.time;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.StringIdentifiable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.time.formatter.DurationFmt;

/**
 * Ported from CoreLib by Sakura Ryoko
 */
public enum DurationFormat implements IConfigOptionListEntry, StringIdentifiable
{
    REGULAR      ("regular",      DurationFmtType.REGULAR,      "malilib.gui.label.duration_format.regular"),
    PRETTY       ("pretty",       DurationFmtType.PRETTY,       "malilib.gui.label.duration_format.pretty"),
    ISO_EXTENDED ("iso_extended", DurationFmtType.ISO_EXTENDED, "malilib.gui.label.duration_format.iso_extended"),
    FORMATTED    ("formatted",    DurationFmtType.FORMATTED,    "malilib.gui.label.duration_format.formatted"),
    ;

    public static final StringIdentifiable.EnumCodec<DurationFormat> CODEC = StringIdentifiable.createCodec(DurationFormat::values);
    public static final ImmutableList<DurationFormat> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final DurationFmtType<?> type;
    private final String translationKey;

    DurationFormat(String name, DurationFmtType<?> type, String translationKey)
    {
        this.configString = name;
        this.type = type;
        this.translationKey = translationKey;
    }

    @Override
    public @Nonnull String asString()
    {
        return this.configString;
    }

    @Override
    public String getStringValue()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    public DurationFmtType<?> getType()
    {
        return this.type;
    }

    private @Nullable DurationFmt init()
    {
        return this.type.init(this);
    }

    public String format(long duration)
    {
        return this.format(duration, null);
    }

    public String format(long duration, @Nullable String fmt)
    {
        DurationFmt formatter = this.init();

        if (formatter != null)
        {
            if (duration < 1)
            {
                return StringUtils.translate("malilib.gui.label.duration_format.error.invalid", duration);
            }

            return formatter.format(duration, fmt);
        }

        return "";
    }

    public String getFormatString()
    {
        DurationFmt formatter = this.init();

        if (formatter != null)
        {
            return formatter.getFormatString();
        }

        return "";
    }

    @Nullable
    @Override
    public DurationFormat fromString(String value)
    {
        return fromStringStatic(value);
    }

    public static DurationFormat fromStringStatic(String value)
    {
        for (DurationFormat val : VALUES)
        {
            if (value.compareToIgnoreCase(val.getStringValue()) == 0)
            {
                return val;
            }
        }

        return null;
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward)
    {
        int id = this.ordinal();

        if (forward)
        {
            if (++id >= values().length)
            {
                id = 0;
            }
        }
        else
        {
            if (--id < 0)
            {
                id = values().length - 1;
            }
        }

        return values()[id % values().length];
    }

    @Override
    public String toString()
    {
        return this.getStringValue();
    }
}
