package fi.dy.masa.malilib.config;

public interface IConfigOptionList
{
    IConfigOptionListEntry getOptionListValue();

    IConfigOptionListEntry getDefaultOptionListValue();

    void setOptionListValue(IConfigOptionListEntry value);

    default IConfigOptionListEntry getLastOptionListValue() { return this.getDefaultOptionListValue(); }

    default void updateLastOptionListValue() {}
}
