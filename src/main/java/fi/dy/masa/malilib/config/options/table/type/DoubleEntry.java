package fi.dy.masa.malilib.config.options.table.type;

import com.google.gson.JsonObject;

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

    @Override
    public JsonObject getAsJsonObject() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "double");
        obj.addProperty("value", String.valueOf(this.value));
        return obj;
    }

    public static Entry getFromJsonObject(JsonObject obj) {
        try {
            if (!obj.has("type") || !obj.get("type").getAsString().equals("double")) {
                System.out.println("JSON object is not of type double: " + obj);
                return DoubleEntry.of(0.0);
            }
            double val = Double.parseDouble(obj.get("value").getAsString());
            return DoubleEntry.of(val);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("Failed to parse double from JSON object: " + obj);
            return DoubleEntry.of(0.0);
        }
    }
}
