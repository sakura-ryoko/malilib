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
public class TimeFmtDateOnly extends TimeFmt
{
    private final DateTimeFormatter formatter;

    public TimeFmtDateOnly(TimeFormat fmt)
    {
        super(fmt);
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT);
        this.formatString = "yyyy-MM-dd";
    }

    public String formatTo(long time, @Nullable String fmt)
    {
        return this.formatter.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }

    @Override
    public long formatFrom(@Nonnull String formatted, @Nullable String fmt)
    {
        LocalDateTime dateTime;

        try
        {
            dateTime = LocalDateTime.parse(formatted, this.formatter);
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
        return this.formatter.format(ZonedDateTime.now());
    }
}
