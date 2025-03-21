package fi.dy.masa.malilib.config;

import fi.dy.masa.malilib.util.data.Color4f;

public interface IConfigColor extends IConfigValue
        //, IConfigSlider
{
    Color4f getColor();

    int getIntegerValue();

    int getDefaultIntegerValue();

    void setIntegerValue(int value);

    int getMinIntegerValue();

    int getMaxIntegerValue();
}
