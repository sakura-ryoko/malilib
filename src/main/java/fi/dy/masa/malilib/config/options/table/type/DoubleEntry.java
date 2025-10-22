package fi.dy.masa.malilib.config.options.table.type;

public class DoubleEntry extends Entry {
    private double value;

    public DoubleEntry(double value) {
        this.value = value;
    }

    public static DoubleEntry of(double val) {
        return new DoubleEntry(val);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public EntryTypes getType() {
        return EntryTypes.DOUBLE;
    }
}
