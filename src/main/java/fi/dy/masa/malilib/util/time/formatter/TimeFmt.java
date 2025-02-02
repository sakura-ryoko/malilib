package fi.dy.masa.malilib.util.time.formatter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;

import fi.dy.masa.malilib.util.time.TimeFormat;

/**
 * Ported from CoreLib by Sakura Ryoko
 */
@ApiStatus.Internal
public abstract class TimeFmt
{
    protected String formatString;
    protected TimeFormat type;

    public TimeFmt(TimeFormat fmt)
    {
        this.type = fmt;
        this.formatString = "";
    }

    public TimeFormat getType()
    {
        return this.type;
    }

    public String formatTo(long time) { return this.formatTo(time, null); }

    public String formatTo(long time, @Nullable String fmt) { return ""; }

    public long formatFrom(@Nonnull String formattedTime) { return this.formatFrom(formattedTime, null); }

    public long formatFrom(@Nonnull String formattedTime, @Nullable String fmt) { return 0L; }

    public String formatNow() { return this.formatNow(null); }

    public String formatNow(@Nullable String fmt) { return ""; }

    public String getFormatString()
    {
        return this.formatString;
    }
}
