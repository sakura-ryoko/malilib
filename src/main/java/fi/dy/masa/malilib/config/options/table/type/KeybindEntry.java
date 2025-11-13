//package fi.dy.masa.malilib.config.options.table.type;
//
//import com.google.gson.JsonObject;
//import fi.dy.masa.malilib.hotkeys.IKeybind;
//import fi.dy.masa.malilib.hotkeys.KeyAction;
//import fi.dy.masa.malilib.hotkeys.KeybindMulti;
//import fi.dy.masa.malilib.hotkeys.KeybindSettings;
//
//public class KeybindEntry extends Entry {
//    private IKeybind keybind;
//
//    public KeybindEntry(IKeybind keybind) {
//        this.keybind = keybind;
//    }
//
//    public static KeybindEntry of(IKeybind keybind) {
//        return new KeybindEntry(keybind);
//    }
//
//    public static KeybindEntry of(String keybind) {
//        return new KeybindEntry(KeybindMulti.fromStorageString(keybind, KeybindSettings.DEFAULT));
//    }
//
//    public IKeybind getKeybind() {
//        return keybind;
//    }
//
//    public void setKeybind(IKeybind keybind) {
//        this.keybind = keybind;
//    }
//
//    @Override
//    public EntryTypes getType() {
//        return EntryTypes.KEYBIND;
//    }
//
//    public static KeybindEntry from(String str) {
//        String[] parts = str.split(";");
//        if (parts.length != 4) {
//            throw new IllegalArgumentException("Invalid keybind string: " + str);
//        }
//        String settingsStr = parts[1];
//        if (settingsStr.length() != 5) {
//            throw new IllegalArgumentException("Invalid keybind settings string: " + settingsStr);
//        }
//
//        KeybindSettings settings = KeybindSettings.create(KeybindSettings.Context.valueOf(parts[3]),
//                KeyAction.valueOf(parts[2]),
//                settingsStr.charAt(1) == '1',
//                settingsStr.charAt(2) == '1',
//                settingsStr.charAt(3) == '1',
//                settingsStr.charAt(4) == '1',
//                settingsStr.charAt(0) == '1');
//
//        IKeybind keybind = KeybindMulti.fromStorageString(parts[0], settings);
//
//        return KeybindEntry.of(keybind);
//    }
//
//    public String getStringValue() {
//        String str = this.keybind.getStringValue();
//        str += ";";
//        KeybindSettings settings = this.keybind.getSettings();
//        str += settings.getAllowEmpty() ? "1" : "0";
//        str += settings.getAllowExtraKeys() ? "1" : "0";
//        str += settings.isOrderSensitive() ? "1" : "0";
//        str += settings.isExclusive() ? "1" : "0";
//        str += settings.shouldCancel() ? "1" : "0";
//        str += ";";
//        str += settings.getActivateOn().name();
//        str += ";";
//        str += settings.getContext().name();
//
//        return str;
//    }
//
//    @Override
//    public JsonObject getAsJsonObject() {
//        JsonObject obj = new JsonObject();
//
//        obj.addProperty("type", "keybind");
//        obj.addProperty("keybind", this.keybind.getStringValue());
//        obj.add("settings", this.keybind.getSettings().toJson());
//
//        return obj;
//    }
//
//    @Override
//    public Entry copy() {
//        // cursed but whatever
//        return KeybindEntry.getFromJsonObject(this.getAsJsonObject());
//    }
//
//    @Override
//    public boolean wasConfigModified(Entry entry) {
//        if (!(entry instanceof KeybindEntry other)) {
//            return true;
//        }
//        return !this.keybind.getStringValue().equals(other.keybind.getStringValue()) ||
//               !this.keybind.getSettings().equals(other.keybind.getSettings());
//    }
//
//    public static KeybindEntry getFromJsonObject(JsonObject obj) {
//        JsonObject settingsObj = obj.getAsJsonObject("settings");
//        KeybindSettings settings = KeybindSettings.fromJson(settingsObj);
//
//        IKeybind keybind = KeybindMulti.fromStorageString(obj.get("keybind").getAsString(), settings);
//
//        return new KeybindEntry(keybind);
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (!(o instanceof KeybindEntry other)) return false;
//        return other.getStringValue().equals(this.getStringValue());
//    }
//}
