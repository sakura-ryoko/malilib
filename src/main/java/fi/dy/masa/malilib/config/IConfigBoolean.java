package fi.dy.masa.malilib.config;

public interface IConfigBoolean extends IConfigValue
{
    boolean getBooleanValue();

    boolean getDefaultBooleanValue();

    void setBooleanValue(boolean value);

    default void toggleBooleanValue()
    {
        this.setBooleanValue(! this.getBooleanValue());
        this.markDirty();
        this.checkIfClean();
    }

    default boolean getLastBooleanValue() { return this.getDefaultBooleanValue(); }

    default void updateLastBooleanValue() {}
}
