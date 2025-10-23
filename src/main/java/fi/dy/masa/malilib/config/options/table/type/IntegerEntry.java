package fi.dy.masa.malilib.config.options.table.type;

import com.google.gson.JsonObject;

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

    @Override
    public JsonObject getAsJsonObject() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "integer");
        obj.addProperty("value", String.valueOf(this.value));
        return obj;
    }

    public static Entry getFromJsonObject(JsonObject obj) {
        try {
            int val = Integer.parseInt(obj.get("value").getAsString());
            return IntegerEntry.of(val);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("Failed to parse integer from JSON object: " + obj);
            return IntegerEntry.of(0);
        }
    }
}
