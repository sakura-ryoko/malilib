package fi.dy.masa.malilib.util.time.formatter;

import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;

import fi.dy.masa.malilib.util.time.DurationFormat;

/**
 * Ported from CoreLib by Sakura Ryoko
 */
@ApiStatus.Internal
public abstract class DurationFmt
{
    protected DurationFormat type;
    protected String formatString;

    public DurationFmt(DurationFormat fmt)
    {
        this.formatString = "";
        this.type = fmt;
    }

    public DurationFormat getType()
    {
        return this.type;
    }

    public String format(long duration) { return this.format(duration, null); }

    public String format(long duration, @Nullable String fmt) { return ""; }

    public String getFormatString()
    {
        return this.formatString;
    }
}
