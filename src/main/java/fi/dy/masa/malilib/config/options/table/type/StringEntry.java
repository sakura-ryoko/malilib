package fi.dy.masa.malilib.config.options.table.type;

public class StringEntry extends Entry {
    private String value;

    public StringEntry(String value) {
        this.value = value;
    }

    public static StringEntry of(String str) {
        return new StringEntry(str);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public EntryTypes getType() {
        return EntryTypes.STRING;
    }
}
