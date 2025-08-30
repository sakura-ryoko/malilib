package fi.dy.masa.malilib.util.time;

import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;

import fi.dy.masa.malilib.util.time.formatter.*;

/**
 * Ported from CoreLib by Sakura Ryoko
 */
@ApiStatus.Internal
public class TimeFmtType<T extends TimeFmt>
{
    public static final TimeFmtType<TimeFmtRegular> REGULAR;
    public static final TimeFmtType<TimeFmtISOLocal> ISO_LOCAL;
    public static final TimeFmtType<TimeFmtISOOffset> ISO_OFFSET;
    public static final TimeFmtType<TimeFmtFormatted> FORMATTED;
    public static final TimeFmtType<TimeFmtRFC1123> RFC1123;
    public static final TimeFmtType<TimeFmtTimeOnly> TIME_ONLY;
	public static final TimeFmtType<TimeFmtTimeAmPm> TIME_AM_PM;
	public static final TimeFmtType<TimeFmtDayAmPm> DAY_AM_PM;
	public static final TimeFmtType<TimeFmtDateAmPm> DATE_AM_PM;
    public static final TimeFmtType<TimeFmtDateOnly> DATE_ONLY;

    private final TimeFactory<? extends T> factory;
    private final TimeFormat timeFmt;

    private static <T extends TimeFmt> TimeFmtType<T> create(TimeFactory<? extends T> factory, TimeFormat timeFmt)
    {
        return new TimeFmtType<>(factory, timeFmt);
    }

    private TimeFmtType(TimeFactory<? extends T> factory, TimeFormat timeFmt)
    {
        this.timeFmt = timeFmt;
        this.factory = factory;
    }

    @Nullable
    public T init(TimeFormat fmt)
    {
        return this.factory.create(fmt);
    }

    public TimeFormat getFmt()
    {
        return this.timeFmt;
    }

    static
    {
        REGULAR = create(TimeFmtRegular::new, TimeFormat.REGULAR);
        ISO_LOCAL = create(TimeFmtISOLocal::new, TimeFormat.ISO_LOCAL);
        ISO_OFFSET = create(TimeFmtISOOffset::new, TimeFormat.ISO_OFFSET);
        FORMATTED = create(TimeFmtFormatted::new, TimeFormat.FORMATTED);
        RFC1123 = create(TimeFmtRFC1123::new, TimeFormat.RFC1123);
        TIME_ONLY = create(TimeFmtTimeOnly::new, TimeFormat.TIME_ONLY);
		TIME_AM_PM = create(TimeFmtTimeAmPm::new, TimeFormat.TIME_AM_PM);
		DAY_AM_PM = create(TimeFmtDayAmPm::new, TimeFormat.DAY_AM_PM);
		DATE_AM_PM = create(TimeFmtDateAmPm::new, TimeFormat.DATE_AM_PM);
        DATE_ONLY = create(TimeFmtDateOnly::new, TimeFormat.DATE_ONLY);
    }

    @FunctionalInterface
    interface TimeFactory<T extends TimeFmt>
    {
        T create(TimeFormat fmt);
    }
}
