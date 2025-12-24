package fi.dy.masa.malilib.config.options.table.type;

import com.google.gson.JsonObject;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigBoolean;

public class BooleanEntry extends Entry
{
	private final IConfigBoolean configBoolean;

	public BooleanEntry(boolean value)
	{
		this.configBoolean = new ConfigBoolean("", value, "", "", "");
	}

	public static BooleanEntry of(boolean val)
	{
		return new BooleanEntry(val);
	}

	public boolean getValue()
	{
		return configBoolean.getBooleanValue();
	}

	public void setValue(boolean value)
	{
		this.configBoolean.setBooleanValue(value);
	}

	@Override
	public EntryTypes getType()
	{
		return EntryTypes.BOOLEAN;
	}

	@Override
	public JsonObject getAsJsonObject()
	{
		JsonObject obj = new JsonObject();
		obj.addProperty("type", "boolean");
		obj.addProperty("value", String.valueOf(this.configBoolean.getBooleanValue()));
		return obj;
	}

	@Override
	public Entry copy()
	{
		return new BooleanEntry(this.configBoolean.getBooleanValue());
	}

	@Override
	public boolean wasConfigModified(Entry entry)
	{
		if (!(entry instanceof BooleanEntry other))
		{
			return true;
		}
		return this.configBoolean.getBooleanValue() != other.configBoolean.getBooleanValue();
	}

    @Override
    public String asString()
    {
        return Boolean.toString(this.getValue());
    }

    public static BooleanEntry getFromJsonObject(JsonObject obj)
	{
		boolean val = Boolean.parseBoolean(obj.get("value").getAsString());
		return BooleanEntry.of(val);
	}

	public IConfigBoolean getBooleanValue()
	{
		return this.configBoolean;
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof BooleanEntry other))
		{
			return false;
		}
		return other.getValue() == this.getValue();
	}
}
