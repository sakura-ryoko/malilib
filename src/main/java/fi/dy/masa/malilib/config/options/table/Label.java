package fi.dy.masa.malilib.config.options.table;

public record Label(String label, String comment)
{
    public static Label of(String label, String comment)
    {
        return new Label(label, comment);
    }

    public static Label of(String label)
    {
        return new Label(label, "");
    }

    public static Label of()
    {
        return new Label("", "");
    }
}
