package malilib.config.value;

import com.google.common.collect.ImmutableList;

import malilib.util.StringUtils;

public enum LayerMode implements OptionConfigValue
{
    ALL             ("all",             "malilib.name.layer_mode.all"),
    SINGLE_LAYER    ("single_layer",    "malilib.name.layer_mode.single_layer"),
    LAYER_RANGE     ("layer_range",     "malilib.name.layer_mode.layer_range"),
    ALL_BELOW       ("all_below",       "malilib.name.layer_mode.all_below"),
    ALL_ABOVE       ("all_above",       "malilib.name.layer_mode.all_above");

    public static final ImmutableList<LayerMode> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    LayerMode(String configString, String translationKey)
    {
        this.configString = configString;
        this.translationKey = translationKey;
    }

    @Override
    public String getName()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }
}
