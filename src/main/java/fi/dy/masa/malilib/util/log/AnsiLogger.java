/**
 * Cloned from CoreLib by Sakura Ryoko; Originally written under CSVTool by Sakura.
 */
package fi.dy.masa.malilib.util.log;

import fi.dy.masa.malilib.MaLiLibReference;

public class AnsiLogger implements IAnsiLogger
{
    private final String log;
    private boolean debug;
    private boolean ansiColor;

    public AnsiLogger(Class<?> clazz)
    {
        this(clazz, MaLiLibReference.DEBUG_MODE, MaLiLibReference.ANSI_MODE);
    }

    public AnsiLogger(Class<?> clazz, boolean debug)
    {
        this(clazz, debug, MaLiLibReference.ANSI_MODE);
    }

    public AnsiLogger(Class<?> clazz, boolean debug, boolean ansiColor)
    {
        this.log = clazz.getName();
        this.debug = debug;
        this.ansiColor = ansiColor;
    }

    public void toggleDebug(boolean toggle)
    {
        this.debug = toggle;
    }

    public void toggleAnsiColor(boolean toggle)
    {
        this.ansiColor = toggle;
    }

    @Override
    public void info(String fmt, Object... args)
    {
        if (this.log != null)
        {
            String msg = this.format(fmt, args);

            if (this.ansiColor)
            {
                System.out.printf(AnsiColors.WHITE + "[INFO/" + this.log + "]: %s" + AnsiColors.RESET + "\n", msg);
            }
            else
            {
                System.out.printf("[INFO/" + this.log + "]: %s\n", msg);
            }
        }
    }

    @Override
    public void debug(String fmt, Object... args)
    {
        if (this.log != null && this.debug)
        {
            String msg = this.format(fmt, args);

            if (this.ansiColor)
            {
                System.out.printf(AnsiColors.PURPLE_BOLD + "[DEBUG/" + this.log + "]: %s" + AnsiColors.RESET + "\n", msg);
            }
            else
            {
                System.out.printf("[DEBUG/" + this.log + "]: %s\n", msg);
            }
        }
    }

    @Override
    public void warn(String fmt, Object... args)
    {
        if (this.log != null)
        {
            String msg = this.format(fmt, args);

            if (this.ansiColor)
            {
                System.out.printf(AnsiColors.YELLOW_BOLD + "[WARN/" + this.log + "]: %s" + AnsiColors.RESET + "\n", msg);
            }
            else
            {
                System.out.printf("[WARN/" + this.log + "]: %s\n", msg);
            }
        }
    }

    @Override
    public void error(String fmt, Object... args)
    {
        if (this.log != null)
        {
            String msg = this.format(fmt, args);

            if (this.ansiColor)
            {
                System.out.printf(AnsiColors.RED_BOLD + "[ERROR/" + this.log + "]: %s" + AnsiColors.RESET + "\n", msg);
            }
            else
            {
                System.out.printf("[ERROR/" + this.log + "]: %s\n", msg);
            }
        }
    }

    @Override
    public void fatal(String fmt, Object... args)
    {
        if (this.log != null)
        {
            String msg = this.format(fmt, args);

            if (this.ansiColor)
            {
                System.out.printf(AnsiColors.RED_BOLD_BRIGHT + "[FATAL/" + this.log + "]: %s" + AnsiColors.RESET + "\n", msg);
            }
            else
            {
                System.out.printf("[FATAL/" + this.log + "]: %s\n", msg);
            }
        }
    }
}
