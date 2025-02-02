package fi.dy.masa.malilib.util.time.formatter;

import javax.annotation.Nullable;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.ApiStatus;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.time.DurationFormat;

/**
 * Ported from CoreLib by Sakura Ryoko
 */
@ApiStatus.Internal
public class DurationFmtPretty extends DurationFmt
{
    public DurationFmtPretty(DurationFormat fmt)
    {
        super(fmt);
        this.formatString = "d' days 'H' hours 'm' minutes 's' seconds'";
    }

    @Override
    public String format(long duration, @Nullable String fmt)
    {
        try
        {
            return DurationFormatUtils.formatDurationWords(duration, true, true);
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("format(): {}",
                                 StringUtils.translate("malilib.gui.label.duration_format.error.invalid_format", err.getMessage()));
        }

        return DurationFormatUtils.formatDurationHMS(duration);
    }
}
