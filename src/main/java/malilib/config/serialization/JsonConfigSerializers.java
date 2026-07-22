package malilib.config.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import malilib.config.option.BooleanAndDoubleConfig;
import malilib.config.option.BooleanAndFileConfig;
import malilib.config.option.BooleanAndIntConfig;
import malilib.config.option.BooleanConfig;
import malilib.config.option.ColorConfig;
import malilib.config.option.DoubleConfig;
import malilib.config.option.DualColorConfig;
import malilib.config.option.FileConfig;
import malilib.config.option.HotkeyConfig;
import malilib.config.option.HotkeyedBooleanConfig;
import malilib.config.option.IntegerConfig;
import malilib.config.option.OptionConfig;
import malilib.config.option.StringConfig;
import malilib.config.option.Vec2dConfig;
import malilib.config.option.Vec2iConfig;
import malilib.config.option.list.BlackWhiteListConfig;
import malilib.config.option.list.ValueListConfig;
import malilib.config.value.OptionConfigValue;
import malilib.util.data.json.JsonSerializers;

public class JsonConfigSerializers
{
    public static JsonElement serializeBooleanConfig(BooleanConfig config)
    {
        return new JsonPrimitive(config.getValueForSerialization());
    }

    public static JsonElement serializeIntegerConfig(IntegerConfig config)
    {
        return new JsonPrimitive(config.getValueForSerialization());
    }

    public static JsonElement serializeDoubleConfig(DoubleConfig config)
    {
        return new JsonPrimitive(config.getValueForSerialization());
    }

    public static JsonElement serializeStringConfig(StringConfig config)
    {
        return new JsonPrimitive(config.getValueForSerialization());
    }

    public static JsonElement serializeColorConfig(ColorConfig config)
    {
        return new JsonPrimitive(config.getValueForSerialization().toString());
    }

    public static JsonElement serializeFileConfig(FileConfig config)
    {
        return new JsonPrimitive(config.getValueForSerialization().toAbsolutePath().toString());
    }

    public static JsonElement serializeHotkeyConfig(HotkeyConfig config)
    {
        return config.getKeyBind().getAsJsonElement();
    }

    public static JsonElement serializeHotkeyedBooleanConfig(HotkeyedBooleanConfig config)
    {
        JsonObject obj = new JsonObject();
        obj.add("enabled", new JsonPrimitive(config.getValueForSerialization()));
        obj.add("hotkey", config.getKeyBind().getAsJsonElement());
        return obj;
    }

    public static JsonElement serializeDualColorConfig(DualColorConfig config)
    {
        return JsonSerializers.serializeDualColorValue(config.getValueForSerialization());
    }

    public static JsonElement serializeVec2dConfig(Vec2dConfig config)
    {
        return JsonSerializers.serializeVec2dValue(config.getValueForSerialization());
    }

    public static JsonElement serializeVec2iConfig(Vec2iConfig config)
    {
        return JsonSerializers.serializeVec2iValue(config.getValueForSerialization());
    }

    public static JsonElement serializeBooleanAndIntConfig(BooleanAndIntConfig config)
    {
        return JsonSerializers.serializeBooleanAndIntValue(config.getValueForSerialization());
    }

    public static JsonElement serializeBooleanAndDoubleConfig(BooleanAndDoubleConfig config)
    {
        return JsonSerializers.serializeBooleanAndDoubleValue(config.getValueForSerialization());
    }

    public static JsonElement serializeBooleanAndFileConfig(BooleanAndFileConfig config)
    {
        return JsonSerializers.serializeBooleanAndFileValue(config.getValueForSerialization());
    }

    public static <T extends OptionConfigValue> JsonElement serializeOptionListConfig(OptionConfig<T> config)
    {
        return JsonSerializers.serializeOptionListValue(config.getValueForSerialization());
    }

    public static <T> JsonElement serializeValueListConfig(ValueListConfig<T> config)
    {
        return JsonSerializers.serializeValueListAsString(config.getValueForSerialization(), config.getToStringConverter());
    }

    public static <T> JsonElement serializeBlackWhiteListConfig(BlackWhiteListConfig<T> config)
    {
        return JsonSerializers.serializeBlackWhiteList(config.getValueForSerialization());
    }
}
