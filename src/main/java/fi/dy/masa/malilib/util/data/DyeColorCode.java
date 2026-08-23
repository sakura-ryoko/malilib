package fi.dy.masa.malilib.util.data;

public enum DyeColorCode
{
    WHITE       ( 0, "§f", "white"),
    ORANGE      ( 1, "§6", "orange"),
    MAGENTA     ( 2, "§d", "magenta"),
    LIGHT_BLUE  ( 3, "§9", "light_blue"),
    YELLOW      ( 4, "§e", "yellow"),
    LIME        ( 5, "§a", "lime"),
    PINK        ( 6, "§d", "pink"),
    GRAY        ( 7, "§8", "gray"),
    SILVER      ( 8, "§7", "silver"),
    CYAN        ( 9, "§3", "cyan"),
    PURPLE      (10, "§5", "purple"),
    BLUE        (11, "§1", "blue"),
    BROWN       (12, "§6", "brown"),
    GREEN       (13, "§2", "green"),
    RED         (14, "§c", "red"),
    BLACK       (15, "§0", "black");

    private static final DyeColorCode[] COLOR_CODES_BY_META = DyeColorCode.values();

    private final int meta;
    private final String name;
    private final String textColorCode;

    DyeColorCode(int meta, String textColorCode, String name)
    {
        this.meta = meta;
        this.textColorCode = textColorCode;
        this.name = name;
    }

    public int getMetadata()
    {
        return this.meta;
    }

    public String getName()
    {
        return this.name;
    }

    public String getTextColorCode()
    {
        return this.textColorCode;
    }

    public static DyeColorCode getByMeta(int meta)
    {
        if (meta < 0 || meta > 15)
        {
            meta = 0;
        }

        return COLOR_CODES_BY_META[meta];
    }
}
