package malilib.util.data.json;

import java.util.List;
import java.util.function.Function;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.tuple.Pair;

import malilib.config.option.BooleanAndDoubleConfig.BooleanAndDouble;
import malilib.config.option.BooleanAndFileConfig.BooleanAndFile;
import malilib.config.option.BooleanAndIntConfig.BooleanAndInt;
import malilib.config.value.BlackWhiteList;
import malilib.config.value.OptionConfigValue;
import malilib.util.data.Color4f;
import malilib.util.position.Vec2d;
import malilib.util.position.Vec2i;

public class JsonSerializers
{
    public static JsonElement serializeDualColorValue(Pair<Color4f, Color4f> value)
    {
        JsonObject obj = new JsonObject();
        obj.add("color1", new JsonPrimitive(value.getLeft().intValue));
        obj.add("color2", new JsonPrimitive(value.getRight().intValue));
        return obj;
    }

    public static JsonElement serializeVec2dValue(Vec2d value)
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", value.x);
        obj.addProperty("y", value.y);
        return obj;
    }

    public static JsonElement serializeVec2iValue(Vec2i value)
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", value.x);
        obj.addProperty("y", value.y);
        return obj;
    }

    public static JsonElement serializeBooleanAndIntValue(BooleanAndInt value)
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("b", value.booleanValue);
        obj.addProperty("i", value.intValue);
        return obj;
    }

    public static JsonElement serializeBooleanAndDoubleValue(BooleanAndDouble value)
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("b", value.booleanValue);
        obj.addProperty("d", value.doubleValue);
        return obj;
    }

    public static JsonElement serializeBooleanAndFileValue(BooleanAndFile value)
    {
        JsonObject obj = new JsonObject();
        obj.add("enabled", new JsonPrimitive(value.booleanValue));
        obj.add("directory", new JsonPrimitive(value.fileValue.toAbsolutePath().toString()));
        return obj;
    }

    public static <T extends OptionConfigValue> JsonElement serializeOptionListValue(T value)
    {
        return new JsonPrimitive(value.getName());
    }

    public static <T> JsonElement serializeValueListAsElement(List<T> value, Function<T, JsonElement> toElementConverter)
    {
        JsonArray arr = new JsonArray();

        for (T val : value)
        {
            arr.add(toElementConverter.apply(val));
        }

        return arr;
    }

    public static <T> JsonElement serializeValueListAsString(List<T> value, Function<T, String> toStringConverter)
    {
        JsonArray arr = new JsonArray();

        for (T val : value)
        {
            arr.add(new JsonPrimitive(toStringConverter.apply(val)));
        }

        return arr;
    }

    public static <T> JsonElement serializeBlackWhiteList(BlackWhiteList<T> value)
    {
        JsonObject obj = new JsonObject();
        obj.add("type", new JsonPrimitive(value.getListType().getName()));
        obj.add("blacklist", JsonUtils.stringListAsArray(value.getBlackListAsString()));
        obj.add("whitelist", JsonUtils.stringListAsArray(value.getWhiteListAsString()));
        return obj;
    }
}
