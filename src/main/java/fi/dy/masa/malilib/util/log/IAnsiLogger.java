/**
 * Cloned from CoreLib by Sakura Ryoko; Originally written under CSVTool by Sakura.
 */
package fi.dy.masa.malilib.util.log;

public interface IAnsiLogger
{
    default String format(final String format, final Object... args)
    {
        String result = format;

        for (Object arg : args)
        {
            result = result.replaceFirst("\\{\\}", arg.toString());
        }

        return result;
    }

    void info(String fmt, Object... args);

    void debug(String fmt, Object... args);

    void warn(String fmt, Object... args);

    void error(String fmt, Object... args);

    void fatal(String fmt, Object... args);
}
