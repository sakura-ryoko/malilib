package fi.dy.masa.malilib.config.options.table.type;

import com.google.gson.JsonObject;

public class StringEntry extends Entry
{
	private String value;

	public StringEntry(String value)
	{
		this.value = value;
	}

	public static StringEntry of(String str)
	{
		return new StringEntry(str);
	}

	public String getValue()
	{
		return this.value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public EntryTypes getType()
	{
		return EntryTypes.STRING;
	}

	@Override
	public JsonObject getAsJsonObject()
	{
		JsonObject obj = new JsonObject();
		obj.addProperty("type", "string");
		obj.addProperty("value", this.value);
		return obj;
	}

	@Override
	public Entry copy()
	{
		return new StringEntry(value);
	}

	@Override
	public boolean wasConfigModified(Entry entry)
	{
		if (!(entry instanceof StringEntry other))
		{
			return true;
		}
		return !this.value.equals(other.value);
	}

    @Override
    public String asString() {
        return this.getValue();
    }

    public static StringEntry getFromJsonObject(JsonObject obj)
	{
		String val = obj.get("value").getAsString();
		return StringEntry.of(val);
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof StringEntry other))
		{
			return false;
		}
		return other.getValue().equals(this.getValue());
	}
}
