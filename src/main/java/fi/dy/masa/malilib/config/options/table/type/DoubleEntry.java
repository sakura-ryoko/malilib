package fi.dy.masa.malilib.config.options.table.type;

import com.google.gson.JsonObject;

public class DoubleEntry extends Entry
{
    private double value;

    public DoubleEntry(double value)
    {
        this.value = value;
    }

    public static DoubleEntry of(double val)
    {
        return new DoubleEntry(val);
    }

    public double getValue()
    {
        return value;
    }

    public void setValue(double value)
    {
        this.value = value;
    }

    @Override
    public EntryTypes getType()
    {
        return EntryTypes.DOUBLE;
    }

    @Override
    public JsonObject getAsJsonObject()
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "double");
        obj.addProperty("value", String.valueOf(this.value));
        return obj;
    }

    @Override
    public Entry copy()
    {
        return new DoubleEntry(value);
    }

    @Override
    public boolean wasConfigModified(Entry entry)
    {
        if (!(entry instanceof DoubleEntry other))
        {
            return true;
        }
        return this.value != other.value;
    }

    public static DoubleEntry getFromJsonObject(JsonObject obj)
    {
        try
        {
            double val = Double.parseDouble(obj.get("value").getAsString());
            return DoubleEntry.of(val);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            System.out.println("Failed to parse double from JSON object: " + obj);
            return DoubleEntry.of(0.0);
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof DoubleEntry other)) return false;
        return other.getValue() == this.getValue();
    }
}
