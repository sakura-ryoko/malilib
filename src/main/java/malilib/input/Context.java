package malilib.input;

import com.google.common.collect.ImmutableList;

import malilib.config.value.BaseOptionConfigValue;

public class Context extends BaseOptionConfigValue
{
    public static final Context INGAME = new Context("ingame",  "malilib.name.key_context.ingame", 0);
    public static final Context GUI    = new Context("gui",     "malilib.name.key_context.gui", 1);
    public static final Context ANY    = new Context("any",     "malilib.name.key_context.any", 2);

    public static final ImmutableList<Context> VALUES = ImmutableList.of(INGAME, GUI, ANY);

    protected final int iconIndex;

    private Context(String name, String translationKey, int iconIndex)
    {
        super(name, translationKey);

        this.iconIndex = iconIndex;
    }

    public int getIconIndex()
    {
        return this.iconIndex;
    }
}
