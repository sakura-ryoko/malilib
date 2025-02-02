package fi.dy.masa.malilib.util.time;

import java.util.Random;

public class TimeTestExample
{
    public static String runDurationTest()
    {
        StringBuilder result = new StringBuilder("*** Duration Test:\n");
        long duration = new Random(System.currentTimeMillis()).nextInt();

        // Cannot use a negative duration.
        while (duration < 1)
        {
            duration = new Random(System.currentTimeMillis()).nextInt();
        }

        result.append("Random Value: ").append(duration).append("\n");

        for (DurationFormat format : DurationFormat.VALUES)
        {
            result.append(format.getDisplayName().toUpperCase()).append(": §a(").append(format.format(duration, null)).append(")§r // [Format: §e").append(format.getFormatString()).append("]§r\n");
        }

        return result.toString();
    }

    public static String runTimeDateTest()
    {
        StringBuilder result = new StringBuilder("*** Time/Date Test:\n");

        result.append("Vanilla Formatted Now: ").append(System.currentTimeMillis()).append("\n");

        for (TimeFormat format : TimeFormat.VALUES)
        {
            result.append(format.getDisplayName().toUpperCase()).append(": §a(").append(format.formatNow(null)).append(")§r // [Format: §e").append(format.getFormatString()).append("]§r\n");
        }

        return result.toString();
    }
}
