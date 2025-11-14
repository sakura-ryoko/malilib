package fi.dy.masa.malilib.config.options.table.type;

import com.google.gson.JsonObject;

import fi.dy.masa.malilib.MaLiLib;

public class IntegerEntry extends Entry
{
	private int value;

	public IntegerEntry(int value)
	{
		this.value = value;
	}

	public static IntegerEntry of(int val)
	{
		return new IntegerEntry(val);
	}

	public int getValue()
	{
		return this.value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}

	@Override
	public EntryTypes getType()
	{
		return EntryTypes.INTEGER;
	}

	@Override
	public JsonObject getAsJsonObject()
	{
		JsonObject obj = new JsonObject();
		obj.addProperty("type", "integer");
		obj.addProperty("value", String.valueOf(this.value));
		return obj;
	}

	@Override
	public Entry copy()
	{
		return new IntegerEntry(this.value);
	}

	@Override
	public boolean wasConfigModified(Entry entry)
	{
		if (!(entry instanceof IntegerEntry other))
		{
			return true;
		}
		return this.value != other.value;
	}

    @Override
    public String asString()
    {
        return Integer.toString(getValue());
    }

    public static IntegerEntry getFromJsonObject(JsonObject obj)
	{
		try
		{
			int val = Integer.parseInt(obj.get("value").getAsString());
			return IntegerEntry.of(val);
		}
		catch (NumberFormatException e)
		{
//		e.printStackTrace();
//			System.out.println("Failed to parse integer from JSON object: " + obj);
			MaLiLib.LOGGER.error("Failed to parse integer from JSON object: [{}]; {}", obj, e.getLocalizedMessage());
			return IntegerEntry.of(0);
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof IntegerEntry other))
		{
			return false;
		}
		return other.getValue() == this.getValue();
	}
}
