package fi.dy.masa.malilib.util.data.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.gson.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.value.FileWriteType;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.data.BooleanConsumer;
import fi.dy.masa.malilib.util.data.FloatConsumer;
import fi.dy.masa.malilib.util.position.BlockMirror;
import fi.dy.masa.malilib.util.position.BlockRotation;
import fi.dy.masa.malilib.util.position.Vec3d;

/**
 * Being moved to util/data/json sooner or later
 */
public class JsonUtils
{
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void copyPropertyIfExists(JsonObject srcObj, JsonObject dstObj, String name)
    {
        if (srcObj.has(name))
        {
            dstObj.add(name, deepCopy(srcObj.get(name)));
        }
    }

    @Nullable
    public static JsonObject getNestedObject(JsonObject parent, String key, boolean create)
    {
        if (parent.has(key) == false || parent.get(key).isJsonObject() == false)
        {
            if (create == false)
            {
                return null;
            }

            JsonObject obj = new JsonObject();
            parent.add(key, obj);
            return obj;
        }
        else
        {
            return parent.get(key).getAsJsonObject();
        }
    }

    public static boolean hasBoolean(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);

        if (el != null && el.isJsonPrimitive())
        {
            try
            {
                el.getAsBoolean();
                return true;
            }
            catch (Exception ignore) {}
        }

        return false;
    }

    public static boolean hasInteger(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);

        if (el != null && el.isJsonPrimitive())
        {
            try
            {
                el.getAsInt();
                return true;
            }
            catch (Exception ignore) {}
        }

        return false;
    }

    public static boolean hasLong(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);

        if (el != null && el.isJsonPrimitive())
        {
            try
            {
                el.getAsLong();
                return true;
            }
            catch (Exception ignore) {}
        }

        return false;
    }

    public static boolean hasFloat(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);

        if (el != null && el.isJsonPrimitive())
        {
            try
            {
                el.getAsFloat();
                return true;
            }
            catch (Exception ignore) {}
        }

        return false;
    }

    public static boolean hasDouble(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);

        if (el != null && el.isJsonPrimitive())
        {
            try
            {
                el.getAsDouble();
                return true;
            }
            catch (Exception ignore) {}
        }

        return false;
    }

    public static boolean hasString(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);

        if (el != null && el.isJsonPrimitive())
        {
            try
            {
                el.getAsString();
                return true;
            }
            catch (Exception ignore) {}
        }

        return false;
    }

    public static boolean hasObject(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);
        return el != null && el.isJsonObject();
    }

    public static boolean hasArray(JsonObject obj, String name)
    {
        JsonElement el = obj.get(name);
        return el != null && el.isJsonArray();
    }

    public static boolean getBooleanOrDefault(JsonObject obj, String name, boolean defaultValue)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                return obj.get(name).getAsBoolean();
            }
            catch (Exception ignore) {}
        }

        return defaultValue;
    }

    public static int getIntegerOrDefault(JsonObject obj, String name, int defaultValue)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                return obj.get(name).getAsInt();
            }
            catch (Exception ignore) {}
        }

        return defaultValue;
    }

    public static long getLongOrDefault(JsonObject obj, String name, long defaultValue)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                return obj.get(name).getAsLong();
            }
            catch (Exception ignore) {}
        }

        return defaultValue;
    }

    public static float getFloatOrDefault(JsonObject obj, String name, float defaultValue)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                return obj.get(name).getAsFloat();
            }
            catch (Exception ignore) {}
        }

        return defaultValue;
    }

    public static double getDoubleOrDefault(JsonObject obj, String name, double defaultValue)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                return obj.get(name).getAsDouble();
            }
            catch (Exception ignore) {}
        }

        return defaultValue;
    }

    @Nullable
    public static String getStringOrDefault(JsonObject obj, String name, @Nullable String defaultValue)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                return obj.get(name).getAsString();
            }
            catch (Exception ignore) {}
        }

        return defaultValue;
    }

    public static void getBooleanIfExists(JsonObject obj, String name, BooleanConsumer consumer)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                consumer.accept(obj.get(name).getAsBoolean());
            }
            catch (Exception ignore) {}
        }
    }

    public static void getIntegerIfExists(JsonObject obj, String name, IntConsumer consumer)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                consumer.accept(obj.get(name).getAsInt());
            }
            catch (Exception ignore) {}
        }
    }

    public static void getLongIfExists(JsonObject obj, String name, LongConsumer consumer)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                consumer.accept(obj.get(name).getAsLong());
            }
            catch (Exception ignore) {}
        }
    }

    public static void getFloatIfExists(JsonObject obj, String name, FloatConsumer consumer)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                consumer.accept(obj.get(name).getAsFloat());
            }
            catch (Exception ignore) {}
        }
    }

    public static void getDoubleIfExists(JsonObject obj, String name, DoubleConsumer consumer)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                consumer.accept(obj.get(name).getAsDouble());
            }
            catch (Exception ignore) {}
        }
    }

    public static void getStringIfExists(JsonObject obj, String name, Consumer<String> consumer)
    {
        if (obj.has(name) && obj.get(name).isJsonPrimitive())
        {
            try
            {
                consumer.accept(obj.get(name).getAsString());
            }
            catch (Exception ignore) {}
        }
    }

    public static boolean getBoolean(JsonObject obj, String name)
    {
        return getBooleanOrDefault(obj, name, false);
    }

    public static int getInteger(JsonObject obj, String name)
    {
        return getIntegerOrDefault(obj, name, 0);
    }

    public static long getLong(JsonObject obj, String name)
    {
        return getLongOrDefault(obj, name, 0);
    }

    public static float getFloat(JsonObject obj, String name)
    {
        return getFloatOrDefault(obj, name, 0);
    }

    public static double getDouble(JsonObject obj, String name)
    {
        return getDoubleOrDefault(obj, name, 0);
    }

    @Nullable
    public static String getString(JsonObject obj, String name)
    {
        return getStringOrDefault(obj, name, null);
    }

    public static void addIfNotEqual(JsonObject obj, String name, int value, int excludeValue)
    {
        if (value != excludeValue)
        {
            obj.addProperty(name, value);
        }
    }

    public static void addIfNotEqual(JsonObject obj, String name, float value, float excludeValue)
    {
        if (value != excludeValue)
        {
            obj.addProperty(name, value);
        }
    }

    public static void addIfNotEqual(JsonObject obj, String name, double value, double excludeValue)
    {
        if (value != excludeValue)
        {
            obj.addProperty(name, value);
        }
    }

    public static void addIfNotEqual(JsonObject obj, String name, boolean value, boolean excludeValue)
    {
        if (value != excludeValue)
        {
            obj.addProperty(name, value);
        }
    }

    public static void addIfNotEqual(JsonObject obj, String name, String value, String excludeValue)
    {
        if (value.equals(excludeValue) == false)
        {
            obj.addProperty(name, value);
        }
    }

    public static void addIfNotEqual(JsonObject obj, String name, BlockRotation value, BlockRotation excludeValue)
    {
        if (value != excludeValue)
        {
            obj.addProperty(name, value.getName());
        }
    }

    public static void addIfNotEqual(JsonObject obj, String name, BlockMirror value, BlockMirror excludeValue)
    {
        if (value != excludeValue)
        {
            obj.addProperty(name, value.getName());
        }
    }

    public static void addStringIfNotNull(JsonObject obj, String name, @Nullable String value)
    {
        if (value != null)
        {
            obj.addProperty(name, value);
        }
    }

    public static void addElementIfNotNull(JsonObject obj, String name, @Nullable JsonElement el)
    {
        if (el != null)
        {
            obj.add(name, el);
        }
    }

    public static boolean hasBlockPos(JsonObject obj, String name)
    {
        return getBlockPos(obj, name) != null;
    }

    public static JsonArray blockPosToJson(Vec3i pos)
    {
        JsonArray arr = new JsonArray();

        arr.add(new JsonPrimitive(pos.getX()));
        arr.add(new JsonPrimitive(pos.getY()));
        arr.add(new JsonPrimitive(pos.getZ()));

        return arr;
    }

    @Nullable
    public static Vec3i getVec3i(JsonObject obj, String name)
    {
        if (hasArray(obj, name))
        {
            JsonArray arr = obj.getAsJsonArray(name);

            if (arr.size() == 3)
            {
                try
                {
                    return new Vec3i(arr.get(0).getAsInt(), arr.get(1).getAsInt(), arr.get(2).getAsInt());
                }
                catch (Exception ignore) {}
            }
        }

        return null;
    }

    @Nullable
    public static Vec3i getVec3iOrDefault(JsonObject obj, String name, @Nullable Vec3i defaultVec)
    {
        Vec3i vec = getVec3i(obj, name);
        return vec != null ? vec : defaultVec;
    }

    public static void getVec3iIfExists(JsonObject obj, String name, Consumer<Vec3i> consumer)
    {
        Vec3i vec = getVec3i(obj, name);

        if (vec != null)
        {
            consumer.accept(vec);
        }
    }

    @Nullable
    public static BlockPos getBlockPos(JsonObject obj, String name)
    {
        if (hasArray(obj, name))
        {
            JsonArray arr = obj.getAsJsonArray(name);

            if (arr.size() == 3)
            {
                try
                {
                    return new BlockPos(arr.get(0).getAsInt(), arr.get(1).getAsInt(), arr.get(2).getAsInt());
                }
                catch (Exception ignore) {}
            }
        }

        return null;
    }

    @Nullable
    public static BlockPos getBlockPosOrDefault(JsonObject obj, String name, @Nullable BlockPos defaultPos)
    {
        BlockPos pos = getBlockPos(obj, name);
        return pos != null ? pos : defaultPos;
    }

    public static void getBlockPosIfExists(JsonObject obj, String name, Consumer<BlockPos> consumer)
    {
        BlockPos pos = getBlockPos(obj, name);

        if (pos != null)
        {
            consumer.accept(pos);
        }
    }

    public static void putBlockPosIfNotNull(JsonObject obj, String name, @Nullable BlockPos pos)
    {
        if (pos != null)
        {
            obj.add(name, blockPosToJson(pos));
        }
    }

    public static boolean hasVec3d(JsonObject obj, String name)
    {
        return getVec3d(obj, name) != null;
    }

    public static JsonArray vec3dToJson(Vec3d vec)
    {
        JsonArray arr = new JsonArray();

        arr.add(new JsonPrimitive(vec.x));
        arr.add(new JsonPrimitive(vec.y));
        arr.add(new JsonPrimitive(vec.z));

        return arr;
    }

    @Nullable
    public static Vec3d getVec3d(JsonObject obj, String name)
    {
        if (hasArray(obj, name))
        {
            JsonArray arr = obj.getAsJsonArray(name);

            if (arr.size() == 3)
            {
                try
                {
                    return Vec3d.of(arr.get(0).getAsDouble(), arr.get(1).getAsDouble(), arr.get(2).getAsDouble());
                }
                catch (Exception ignore) {}
            }
        }

        return null;
    }

    @Nullable
    public static Vec3d getVec3dOrDefault(JsonObject obj, String name, @Nullable Vec3d defaultVec)
    {
        Vec3d vec = getVec3d(obj, name);
        return vec != null ? vec : defaultVec;
    }

    public static void getVec3dIfExists(JsonObject obj, String name, Consumer<Vec3d> consumer)
    {
        Vec3d vec = getVec3d(obj, name);

        if (vec != null)
        {
            consumer.accept(vec);
        }
    }

    public static BlockRotation getRotation(JsonObject obj, String name)
    {
        String str = getString(obj, name);

        if (str != null)
        {
            try
            {
                return BlockRotation.byName(str);
            }
            catch (Exception ignore) {}
        }

        return BlockRotation.NONE;
    }

    public static BlockMirror getMirror(JsonObject obj, String name)
    {
        String str = getString(obj, name);

        if (str != null)
        {
            try
            {
                return BlockMirror.byName(str);
            }
            catch (Exception ignore) {}
        }

        return BlockMirror.NONE;
    }

    public static Direction.Axis getAxis(JsonObject obj, String name)
    {
        String str = getString(obj, name);

        if (str != null)
        {
            try
            {
                return Direction.Axis.byName(str);
            }
            catch (Exception ignore) {}
        }

        return Direction.Axis.Y;
    }

    public static JsonArray stringListAsArray(List<String> list)
    {
        JsonArray arr = new JsonArray();

        for (String str : list)
        {
            arr.add(new JsonPrimitive(str));
        }

        return arr;
    }

    public static List<String> arrayAsStringList(JsonArray arr)
    {
        List<String> list = new ArrayList<>();
        final int count = arr.size();

        for (int i = 0; i < count; ++i)
        {
            list.add(arr.get(i).getAsString());
        }

        return list;
    }

    public static void getObjectIfExists(JsonElement el, String objectName, Consumer<JsonObject> objectConsumer)
    {
        if (el.isJsonObject() == false)
        {
            return;
        }

        JsonObject parentObj = el.getAsJsonObject();

        if (hasObject(parentObj, objectName))
        {
            JsonObject obj = parentObj.get(objectName).getAsJsonObject();
            objectConsumer.accept(obj);
        }
    }

    public static void getArrayIfExists(JsonElement el, String arrayName, Consumer<JsonArray> arrayConsumer)
    {
        if (el.isJsonObject() == false)
        {
            return;
        }

        JsonObject obj = el.getAsJsonObject();

        if (hasArray(obj, arrayName))
        {
            JsonArray arr = obj.get(arrayName).getAsJsonArray();
            arrayConsumer.accept(arr);
        }
    }

    public static void getArrayElementsIfExists(JsonElement el, String arrayName, Consumer<JsonElement> elementConsumer)
    {
        getArrayIfExists(el, arrayName, (arr) -> getArrayElements(arr, elementConsumer));
    }

    public static void getArrayElements(JsonArray arr, Consumer<JsonElement> elementConsumer)
    {
        int size = arr.size();

        for (int i = 0; i < size; ++i)
        {
            elementConsumer.accept(arr.get(i));
        }
    }

    public static void getArrayElementsIfObjects(JsonElement el, String arrayName, Consumer<JsonObject> elementConsumer)
    {
        getArrayIfExists(el, arrayName, (arr) -> getArrayElementsAsObjects(arr, elementConsumer));
    }

    public static void getArrayElementsAsObjects(JsonArray arr, Consumer<JsonObject> elementConsumer)
    {
        int size = arr.size();

        for (int i = 0; i < size; ++i)
        {
            JsonElement el = arr.get(i);

            if (el.isJsonObject())
            {
                elementConsumer.accept(el.getAsJsonObject());
            }
        }
    }

    /**
     * Writes the given values to a JsonArray, using the given serializer. Note that
     * the serializer is allowed to return null for a given value - in that case that value is skipped.
     */
    public static <T> JsonArray toArray(Collection<T> values, Function<T, JsonElement> elementSerializer)
    {
        JsonArray arr = new JsonArray();

        for (T value : values)
        {
            JsonElement el = elementSerializer.apply(value);

            if (el != null)
            {
                arr.add(el);
            }
        }

        return arr;
    }

    // https://stackoverflow.com/questions/29786197/gson-jsonobject-copy-value-affected-others-jsonobject-instance
    @Nonnull
    public static JsonObject deepCopy(@Nonnull JsonObject jsonObject)
    {
        JsonObject result = new JsonObject();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet())
        {
            result.add(entry.getKey(), deepCopy(entry.getValue()));
        }

        return result;
    }

    @Nonnull
    public static JsonArray deepCopy(@Nonnull JsonArray jsonArray)
    {
        JsonArray result = new JsonArray();

        for (JsonElement e : jsonArray)
        {
            result.add(deepCopy(e));
        }

        return result;
    }

    @Nonnull
    public static JsonElement deepCopy(@Nonnull JsonElement jsonElement)
    {
        if (jsonElement.isJsonPrimitive() || jsonElement.isJsonNull())
        {
            return jsonElement; // these are immutable anyway
        }
        else if (jsonElement.isJsonObject())
        {
            return deepCopy(jsonElement.getAsJsonObject());
        }
        else if (jsonElement.isJsonArray())
        {
            return deepCopy(jsonElement.getAsJsonArray());
        }
        else
        {
            throw new UnsupportedOperationException("Unsupported element: " + jsonElement);
        }
    }

    @Nullable
    public static JsonElement parseJsonFromString(String str)
    {
        try
        {
            return JsonParser.parseString(str);
        }
        catch (Exception ignore) {}

        return null;
    }

    @Nullable
    public static JsonElement parseJsonFile(Path file)
    {
        if (Files.isRegularFile(file) && Files.isReadable(file))
        {
            try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8))
            {
                return JsonParser.parseReader(reader);
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("Failed to parse the JSON file '{}'", file.toAbsolutePath(), e);
            }
        }

        return null;
    }

    /**
     * Converts the given JsonElement tree into its string representation.
     * @param compact if true, then the output is written on one line without spaces or line breaks.
     * @return The string representation of the given JSON element
     */
    public static String jsonToString(JsonElement element, boolean compact)
    {
        Gson gson = compact ? new Gson() : GSON;
        return gson.toJson(element);
    }

    public static boolean writeJsonToFile(JsonElement root, Path file)
    {
        return writeJsonToFile(root, file, GSON);
    }

    public static boolean writeJsonToFile(final JsonElement root, final Path file, final Gson gson)
    {
        return FileUtils.writeDataToFile(file, w -> writeJsonToWriter(root, file, w, gson),
                                         FileWriteType.TEMP_AND_RENAME);
//                                         MaLiLibConfigs.Generic.CONFIG_WRITE_METHOD.getValue());
    }

    public static void writeJsonToWriter(JsonElement root, Path file, BufferedWriter writer, Gson gson)
    {
        try
        {
            writer.write(gson.toJson(root));
        }
        catch (IOException e)
        {
//            MessageDispatcher.warning().console(e)
//                             .translate("malilib.message.error.failed_to_write_json_to_file",
//                                        file.toAbsolutePath(), e.getMessage());

            InfoUtils.showGuiOrInGameMessage(Message.MessageType.WARNING,
                                             "malilib.message.error.failed_to_write_json_to_file",
                                             file.toAbsolutePath().toString(), e.getLocalizedMessage());
        }
    }

    public static void loadFromFile(Path dir, String fileName, Consumer<JsonElement> dataConsumer)
    {
        Path file = dir.resolve(fileName);
        loadFromFile(file, dataConsumer);
    }

    public static void loadFromFile(Path file, Consumer<JsonElement> dataConsumer)
    {
        JsonElement element = parseJsonFile(file);

        if (element != null)
        {
            dataConsumer.accept(element);
        }
    }
}
