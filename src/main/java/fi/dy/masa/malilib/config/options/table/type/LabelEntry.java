package fi.dy.masa.malilib.config.options.table.type;

import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.options.table.Label;

public class LabelEntry extends Entry
{
    private Label value;

    public LabelEntry(Label value)
    {
        this.value = value;
    }

    public LabelEntry(String label, String comment)
    {
        this.value = Label.of(label, comment);
    }

    public static LabelEntry of()
    {
        return new LabelEntry("", "");
    }


    public static LabelEntry of(String label)
    {
        return new LabelEntry(label, "");
    }

    public static LabelEntry of(String label, String comment)
    {
        return new LabelEntry(label, comment);
    }

    public Label getValue()
    {
        return this.value;
    }

    public void setValue(Label value)
    {
        this.value = value;
    }

    @Override
    public EntryTypes getType()
    {
        return EntryTypes.LABEL;
    }

    @Override
    public JsonObject getAsJsonObject()
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "label");
        obj.addProperty("label", this.value.label());
        obj.addProperty("comment", this.value.comment());
        return obj;
    }

    @Override
    public Entry copy()
    {
        return new LabelEntry(value);
    }

    @Override
    public boolean wasConfigModified(Entry entry)
    {
        return false;
    }

    @Override
    public String asString() {
        return this.getValue().label();
    }

    public static LabelEntry getFromJsonObject(JsonObject obj)
    {
        String label = obj.get("label").getAsString();
        String comment = obj.get("comment").getAsString();
        return LabelEntry.of(label, comment);
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof LabelEntry other))
        {
            return false;
        }
        return other.getValue().equals(this.getValue());
    }
}
