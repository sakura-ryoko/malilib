package fi.dy.masa.malilib.config;

public interface IConfigString
{
	default String getStringValue() { return this.getDefaultStringValue(); }

	default String getDefaultStringValue() { return ""; }

	default void setStringValue(String value) {}

	default String getLastStringValue() { return this.getDefaultStringValue(); }

	default void updateLastStringValue() {}
}
