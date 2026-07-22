package malilib.util.data.json;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.tuple.Pair;

import malilib.MaLiLib;
import malilib.config.option.BooleanAndDoubleConfig.BooleanAndDouble;
import malilib.config.option.BooleanAndFileConfig.BooleanAndFile;
import malilib.config.option.BooleanAndIntConfig.BooleanAndInt;
import malilib.config.option.list.BlackWhiteListConfig;
import malilib.config.option.list.ValueListConfig;
import malilib.config.value.BaseOptionConfigValue;
import malilib.config.value.BlackWhiteList;
import malilib.config.value.OptionConfigValue;
import malilib.util.data.Color4f;
import malilib.util.position.Vec2d;
import malilib.util.position.Vec2i;
import malilib.util.restriction.UsageRestriction;
import malilib.util.restriction.UsageRestriction.ListType;

public class JsonDeserializers
{
    public static <T> Optional<T> readPrimitiveValue(JsonElement element,
                                                     Function<JsonElement, T> deserializer)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                return Optional.ofNullable(deserializer.apply(element));
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to deserialize value from the JSON element '{}' - not a JSON primitive", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Exception while trying to deserialize a primitive value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static Optional<Path> readPath(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                return Optional.ofNullable(Paths.get(element.getAsString()));
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to deserialize a path from the JSON element '{}' - not a JSON primitive", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Exception while trying to deserialize a path from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static Optional<Pair<Color4f, Color4f>> readDualColorValue(JsonElement element)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasInteger(obj, "color1") &&
                    JsonUtils.hasInteger(obj, "color2"))
                {
                    int value1 = JsonUtils.getInteger(obj, "color1");
                    int value2 = JsonUtils.getInteger(obj, "color2");

                    return Optional.of(Pair.of(Color4f.fromColor(value1), Color4f.fromColor(value2)));
                }
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read DualColorConfig value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read DualColorConfig value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static Optional<BooleanAndInt> readBooleanAndIntValue(JsonElement element)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasBoolean(obj, "b") &&
                    JsonUtils.hasInteger(obj, "i"))
                {
                    boolean booleanValue = JsonUtils.getBoolean(obj, "b");
                    int intValue = JsonUtils.getInteger(obj, "i");

                    return Optional.of(new BooleanAndInt(booleanValue, intValue));
                }
            }
            else if (element.isJsonPrimitive())
            {
                return Optional.of(new BooleanAndInt(element.getAsBoolean(), 0));
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read BooleanAndInt value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read BooleanAndInt value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static Optional<BooleanAndDouble> readBooleanAndDoubleValue(JsonElement element)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasBoolean(obj, "b") &&
                    JsonUtils.hasDouble(obj, "d"))
                {
                    boolean booleanValue = JsonUtils.getBoolean(obj, "b");
                    double doubleValue = JsonUtils.getDouble(obj, "d");

                    return Optional.of(new BooleanAndDouble(booleanValue, doubleValue));
                }
            }
            else if (element.isJsonPrimitive())
            {
                return Optional.of(new BooleanAndDouble(element.getAsBoolean(), 0.0));
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read BooleanAndDouble value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read BooleanAndDouble value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static Optional<BooleanAndFile> readBooleanAndFileValue(JsonElement element)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasBoolean(obj, "enabled") &&
                    JsonUtils.hasString(obj, "directory"))
                {
                    boolean booleanValue = JsonUtils.getBoolean(obj, "enabled");
                    Path fileValue = Paths.get(JsonUtils.getString(obj, "directory"));

                    return Optional.of(new BooleanAndFile(booleanValue, fileValue));
                }
            }
            else if (element.isJsonPrimitive())
            {
                return Optional.of(new BooleanAndFile(element.getAsBoolean(), Paths.get("")));
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read BooleanAndFile value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read BooleanAndFile value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static <T extends OptionConfigValue> Optional<T> readOptionListValue(JsonElement element, List<T> allValues)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                return Optional.of(BaseOptionConfigValue.findValueByName(element.getAsString(), allValues));
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read OptionList value from the JSON element '{}' - not a JSON primitive", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read OptionList value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static <T> Optional<ImmutableList<T>> readValueList(JsonElement element, Function<String, T> fromStringConverter)
    {
        try
        {
            if (element.isJsonArray())
            {
                ImmutableList.Builder<T> builder = ImmutableList.builder();
                List<String> strings = JsonUtils.arrayAsStringList(element.getAsJsonArray());

                for (T value : ValueListConfig.getStringListAsValues(strings, fromStringConverter))
                {
                    builder.add(value);
                }

                return Optional.of(builder.build());
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read a list of values from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read a list of values from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static Optional<Vec2d> readVec2dValue(JsonElement element)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();
                double x = JsonUtils.getDouble(obj, "x");
                double y = JsonUtils.getDouble(obj, "y");

                return Optional.of(new Vec2d(x, y));
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read Vec2d value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read Vec2d value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static Optional<Vec2i> readVec2iValue(JsonElement element)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();
                int x = JsonUtils.getInteger(obj, "x");
                int y = JsonUtils.getInteger(obj, "y");

                return Optional.of(new Vec2i(x, y));
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read Vec2i value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read Vec2i value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }

    public static <T> Optional<BlackWhiteList<T>> readBlackWhiteListValue(JsonElement element, BlackWhiteListConfig<T> config)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasString(obj, "type") &&
                    JsonUtils.hasArray(obj, "blacklist") &&
                    JsonUtils.hasArray(obj, "whitelist"))
                {
                    String typeStr = JsonUtils.getString(obj, "type");
                    ListType type = BaseOptionConfigValue.findValueByName(typeStr, UsageRestriction.ListType.VALUES);
                    List<String> blackListStr = JsonUtils.arrayAsStringList(obj.getAsJsonArray("blacklist"));
                    List<String> whiteListStr = JsonUtils.arrayAsStringList(obj.getAsJsonArray("whitelist"));

                    BlackWhiteList<T> list = config.getValue();
                    ValueListConfig<T> blackList = list.getBlackList().copy();
                    ValueListConfig<T> whiteList = list.getWhiteList().copy();

                    blackList.setValue(ValueListConfig.getStringListAsValues(blackListStr, list.getFromStringConverter()));
                    whiteList.setValue(ValueListConfig.getStringListAsValues(whiteListStr, list.getFromStringConverter()));

                    return Optional.of(new BlackWhiteList<>(type, blackList, whiteList, list.getToStringConverter(), list.getFromStringConverter()));
                }
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to read BlackWhiteList value from the JSON element '{}'", element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read BlackWhiteList value from the JSON element '{}'", element, e);
        }

        return Optional.empty();
    }
}
