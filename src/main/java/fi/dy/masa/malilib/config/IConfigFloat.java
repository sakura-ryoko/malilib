package fi.dy.masa.malilib.config;

public interface IConfigFloat extends IConfigValue, IConfigSlider
{
    float getFloatValue();

    float getDefaultFloatValue();

    void setFloatValue(float value);

    float getMinFloatValue();

    float getMaxFloatValue();
}
