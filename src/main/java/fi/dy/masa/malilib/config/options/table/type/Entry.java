package fi.dy.masa.malilib.config.options.table.type;

import com.google.gson.JsonObject;

public abstract class Entry {@Override
    public String toString() {
        throw new UnsupportedOperationException();
    }

    public static String getString(Entry entry) {
        if (entry.getType() == EntryTypes.STRING) {
            return ((StringEntry) entry).getValue();
        } else if (entry.getType() == EntryTypes.INTEGER) {
            return Integer.toString(((IntegerEntry) entry).getValue());
        } else if (entry.getType() == EntryTypes.DOUBLE) {
            return Double.toString(((DoubleEntry) entry).getValue());
        }
        throw new IllegalStateException();
    }
    public abstract EntryTypes getType();

    public abstract JsonObject getAsJsonObject();
}
