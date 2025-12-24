package fi.dy.masa.malilib.config.options.table.type;

import com.google.gson.JsonObject;

public abstract class Entry
{
    @Override
    public String toString()
    {
		return Entry.getString(this);
    }

    public static String getString(Entry entry)
    {
        if (entry.getType() == EntryTypes.STRING)
        {
            return ((StringEntry) entry).getValue();
        }
        else if (entry.getType() == EntryTypes.INTEGER)
        {
            return Integer.toString(((IntegerEntry) entry).getValue());
        }
        else if (entry.getType() == EntryTypes.DOUBLE)
        {
            return Double.toString(((DoubleEntry) entry).getValue());
        }
        else if (entry.getType() == EntryTypes.BOOLEAN)
        {
            return Boolean.toString(((BooleanEntry) entry).getValue());
//        } else if (entry.getType() == EntryTypes.KEYBIND) {
//            return ((KeybindEntry) entry).getKeybind().getStringValue();
        }
        throw new IllegalStateException();
    }

    public abstract EntryTypes getType();

    public abstract JsonObject getAsJsonObject();

    public abstract Entry copy();

    public abstract boolean wasConfigModified(Entry entry);

    public abstract String asString();
}
