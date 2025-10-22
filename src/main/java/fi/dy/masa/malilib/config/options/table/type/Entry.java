package fi.dy.masa.malilib.config.options.table.type;

public abstract class Entry {
    public abstract EntryTypes getType();

    @Override
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
}
