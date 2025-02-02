package fi.dy.masa.malilib.util.time.formatter;

import javax.annotation.Nullable;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.ApiStatus;

import fi.dy.masa.malilib.util.time.DurationFormat;

/**
 * Ported from CoreLib by Sakura Ryoko
 */
@ApiStatus.Internal
public class DurationFmtRegular extends DurationFmt
{
    public DurationFmtRegular(DurationFormat fmt)
    {
        super(fmt);
        this.formatString = "HH:mm:ss.SSS";
    }

    @Override
    public String format(long duration, @Nullable String fmt)
    {
        return DurationFormatUtils.formatDurationHMS(duration);
    }
}
