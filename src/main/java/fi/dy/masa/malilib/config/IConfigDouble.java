package fi.dy.masa.malilib.config;

public interface IConfigDouble extends IConfigValue, IConfigSlider
{
    double getDoubleValue();

    double getDefaultDoubleValue();

    void setDoubleValue(double value);

    double getMinDoubleValue();

    double getMaxDoubleValue();

    default double getLastDoubleValue() { return this.getDefaultDoubleValue(); }

    default void updateLastDoubleValue() {}
}
