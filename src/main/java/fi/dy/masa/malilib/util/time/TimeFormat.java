package fi.dy.masa.malilib.util.time;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.time.formatter.TimeFmt;

/**
 * Ported from CoreLib by Sakura Ryoko
 */
public enum TimeFormat implements IConfigOptionListEntry, StringIdentifiable
{
    REGULAR     ("regular",    TimeFmtType.REGULAR,     "malilib.gui.label.time_format.regular"),
    ISO_LOCAL   ("iso_local",  TimeFmtType.ISO_LOCAL,   "malilib.gui.label.time_format.iso_local"),
    ISO_OFFSET  ("iso_offset", TimeFmtType.ISO_OFFSET,  "malilib.gui.label.time_format.iso_offset"),
    FORMATTED   ("formatted",  TimeFmtType.FORMATTED,   "malilib.gui.label.time_format.formatted"),
    RFC1123     ("rfc1123",    TimeFmtType.RFC1123,     "malilib.gui.label.time_format.rfc1123"),
    TIME_ONLY   ("time_only",  TimeFmtType.TIME_ONLY,   "malilib.gui.label.time_format.time_only"),
	TIME_AM_PM  ("time_am_pm", TimeFmtType.TIME_AM_PM,  "malilib.gui.label.time_format.time_am_pm"),
	DAY_AM_PM   ("day_am_pm",  TimeFmtType.DAY_AM_PM,   "malilib.gui.label.time_format.day_am_pm"),
	DATE_AM_PM  ("date_am_pm", TimeFmtType.DATE_AM_PM,  "malilib.gui.label.time_format.date_am_pm"),
    DATE_ONLY   ("date_only",  TimeFmtType.DATE_ONLY,   "malilib.gui.label.time_format.date_only"),
    ;

    public static final StringIdentifiable.EnumCodec<TimeFormat> CODEC = StringIdentifiable.createCodec(TimeFormat::values);
    public static final PacketCodec<ByteBuf, TimeFormat> PACKET_CODEC = PacketCodecs.STRING.xmap(TimeFormat::fromStringStatic, TimeFormat::asString);
    public static final ImmutableList<TimeFormat> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final TimeFmtType<?> type;
    private final String translationKey;

    TimeFormat(String name, TimeFmtType<?> type, String translationKey)
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

    public TimeFmtType<?> getType()
    {
        return this.type;
    }

    private @Nullable TimeFmt init()
    {
        return this.type.init(this);
    }

    public String formatTo(long time)
    {
        return this.formatTo(time, null);
    }

    public String formatTo(long time, @Nullable String fmt)
    {
        TimeFmt formatter = this.init();

        if (formatter != null)
        {
            return formatter.formatTo(time, fmt);
        }

        return "";
    }

    public long formatFrom(@Nonnull String formattedTime)
    {
        return this.formatFrom(formattedTime, null);
    }

    public long formatFrom(@Nonnull String formattedTime, @Nullable String fmt)
    {
        TimeFmt formatter = this.init();

        if (formatter != null)
        {
            return formatter.formatFrom(formattedTime, fmt);
        }

        return 0L;
    }

    public String formatNow()
    {
        return this.formatNow(null);
    }

    public String formatNow(@Nullable String fmt)
    {
        TimeFmt formatter = this.init();

        if (formatter != null)
        {
            return formatter.formatNow(fmt);
        }

        return "";
    }

    public String getFormatString()
    {
        TimeFmt formatter = this.init();

        if (formatter != null)
        {
            return formatter.getFormatString();
        }

        return "";
    }

    @Nullable
    @Override
    public TimeFormat fromString(String value)
    {
        return fromStringStatic(value);
    }

    @Nullable
    public static TimeFormat fromStringStatic(String value)
    {
        for (TimeFormat val : VALUES)
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
