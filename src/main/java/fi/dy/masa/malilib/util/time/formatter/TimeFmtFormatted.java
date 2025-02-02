package fi.dy.masa.malilib.util.time.formatter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.time.TimeFormat;

/**
 * Ported from CoreLib by Sakura Ryoko
 */
@ApiStatus.Internal
public class TimeFmtFormatted extends TimeFmt
{
    private final DateTimeFormatter formatter;

    public TimeFmtFormatted(TimeFormat fmt)
    {
        super(fmt);
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss", Locale.ROOT);
        this.formatString = "yyyy-MM-dd_HH.mm.ss";
    }

    @Override
    public String formatTo(long time, @Nullable String fmt)
    {
        if (fmt != null && !fmt.isEmpty())
        {
            DateTimeFormatter format = this.setFormat(fmt);

            if (format != null)
            {
                return format.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
            }
        }

        return this.formatter.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }

    @Override
    public long formatFrom(@Nonnull String formatted, @Nullable String fmt)
    {
        DateTimeFormatter format;
        LocalDateTime dateTime;

        if (fmt != null)
        {
            format = this.setFormat(fmt);

            if (format == null)
            {
                return 0L;
            }
        }
        else
        {
            format = this.formatter;
        }

        try
        {
            dateTime = LocalDateTime.parse(formatted, format);
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("fromFormat(): {}",
                                 StringUtils.translate("malilib.gui.label.time_format.error.invalid_format", err.getMessage()));
            return 0L;
        }

        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    @Override
    public String formatNow(@Nullable String fmt)
    {
        if (fmt != null && !fmt.isEmpty())
        {
            DateTimeFormatter format = this.setFormat(fmt);

            if (format != null)
            {
                return format.format(ZonedDateTime.now());
            }
        }

        return this.formatter.format(ZonedDateTime.now());
    }

    private @Nullable DateTimeFormatter setFormat(@Nonnull String fmt)
    {
        DateTimeFormatter temp;

        try
        {
            temp = DateTimeFormatter.ofPattern(fmt, Locale.ROOT);
        }
        catch (IllegalArgumentException err)
        {
            MaLiLib.LOGGER.error("setFormat(): {}",
                                 StringUtils.translate("malilib.gui.label.time_format.error.invalid_format", err.getMessage()));
            return null;
        }

        this.formatString = fmt;
        return temp;
    }
}
