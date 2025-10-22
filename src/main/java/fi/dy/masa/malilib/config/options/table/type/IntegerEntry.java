package fi.dy.masa.malilib.config.options.table.type;

public class IntegerEntry extends Entry {
    private int value;

    public IntegerEntry(int value) {
        this.value = value;
    }

    public static IntegerEntry of(int val) {
        return new IntegerEntry(val);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public EntryTypes getType() {
        return EntryTypes.INTEGER;
    }
}
