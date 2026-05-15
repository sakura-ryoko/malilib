package fi.dy.masa.malilib.gui.button;

import javax.annotation.Nullable;

import net.minecraft.client.input.MouseButtonEvent;

import fi.dy.masa.malilib.config.IConfigOptionValues;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigButtonOptionValues extends ButtonGeneric
{
    private final IConfigOptionValues<?> config;
    @Nullable private final String prefixTranslationKey;

    public ConfigButtonOptionValues(int x, int y, int width, int height, IConfigOptionValues<?> config)
    {
        this(x, y, width, height, config, null);
    }

    public ConfigButtonOptionValues(int x, int y, int width, int height, IConfigOptionValues<?> config, @Nullable String prefixTranslationKey)
    {
        super(x, y, width, height, "");
        this.config = config;
        this.prefixTranslationKey = prefixTranslationKey;

        this.updateDisplayString();
    }

    @Override
    protected boolean onMouseClickedImpl(MouseButtonEvent click, boolean doubleClick)
    {
        if (click.input() == 0)
        {
            // Left Click
            this.config.cycleValue(false);
        }
        else if (click.input() == 1)
        {
            // Right Click
            this.config.cycleValue(true);
        }
        else if (click.input() == 2)
        {
            // Middle Click
            this.config.resetToDefault();
        }

        this.updateDisplayString();

        return super.onMouseClickedImpl(click, doubleClick);
    }

    @Override
    public void updateDisplayString()
    {
        if (this.prefixTranslationKey != null)
        {
            this.displayString = StringUtils.translate(this.prefixTranslationKey, this.config.getOptionValue().getDisplayName());
        }
        else
        {
            this.displayString = this.config.getOptionValue().getDisplayName();
        }

        if (!this.config.getOptionValue().getHoverText().isEmpty())
        {
            this.setHoverStrings(this.config.getOptionValue().getHoverText());
        }
    }
}
