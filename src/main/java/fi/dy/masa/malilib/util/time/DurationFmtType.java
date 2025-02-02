package fi.dy.masa.malilib.util.time;

import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;

import fi.dy.masa.malilib.util.time.formatter.*;

/**
 * Ported from CoreLib by Sakura Ryoko
 */
@ApiStatus.Internal
public class DurationFmtType<T extends DurationFmt>
{
    public static final DurationFmtType<DurationFmtRegular> REGULAR;
    public static final DurationFmtType<DurationFmtPretty> PRETTY;
    public static final DurationFmtType<DurationFmtISOExtended> ISO_EXTENDED;
    public static final DurationFmtType<DurationFmtFormatted> FORMATTED;

    private final DurationFactory<? extends T> factory;
    private final DurationFormat durationFmt;

    private static <T extends DurationFmt> DurationFmtType<T> create(DurationFactory<? extends T> factory, DurationFormat durationFmt)
    {
        return new DurationFmtType<>(factory, durationFmt);
    }

    private DurationFmtType(DurationFactory<? extends T> factory, DurationFormat durationFmt)
    {
        this.durationFmt = durationFmt;
        this.factory = factory;
    }

    @Nullable
    public T init(DurationFormat fmt)
    {
        return this.factory.create(fmt);
    }

    public DurationFormat getFmt()
    {
        return this.durationFmt;
    }

    static
    {
        REGULAR = create(DurationFmtRegular::new, DurationFormat.REGULAR);
        PRETTY = create(DurationFmtPretty::new, DurationFormat.PRETTY);
        ISO_EXTENDED = create(DurationFmtISOExtended::new, DurationFormat.ISO_EXTENDED);
        FORMATTED = create(DurationFmtFormatted::new, DurationFormat.FORMATTED);
    }

    @FunctionalInterface
    interface DurationFactory<T extends DurationFmt>
    {
        T create(DurationFormat fmt);
    }
}
