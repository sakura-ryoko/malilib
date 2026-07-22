package malilib.config.serialization;

import java.util.HashMap;
import java.util.Optional;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;

import malilib.MaLiLib;
import malilib.config.option.BaseGenericConfig;
import malilib.config.option.BooleanAndDoubleConfig;
import malilib.config.option.BooleanAndFileConfig;
import malilib.config.option.BooleanAndIntConfig;
import malilib.config.option.BooleanConfig;
import malilib.config.option.ColorConfig;
import malilib.config.option.ConfigInfo;
import malilib.config.option.DirectoryConfig;
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
import malilib.util.data.json.JsonDeserializers;

@SuppressWarnings("unchecked")
public class JsonConfigSerializerRegistry
{
    private final HashMap<Class<? extends ConfigInfo>, ValueFromJsonDeserializer<?, ?>> configValueDeserializers = new HashMap<>();
    private final HashMap<Class<? extends ConfigInfo>, ConfigToJsonSerializer<?>> serializers = new HashMap<>();
    private final HashMap<Class<? extends ConfigInfo>, ConfigFromJsonLoader<?>> configValueLoader = new HashMap<>();

    public JsonConfigSerializerRegistry()
    {
        this.registerDefaultSerializers();
    }

    /**
     * Registers a BaseGenericConfig serializer and value deserializer and value loader. Use this (or the
     * {@link #registerGenericConfigValueDeserializer(Class, malilib.config.serialization.JsonConfigSerializerRegistry.ValueFromJsonDeserializer)}
     * method in addition to
     * {@link #registerSerializers(Class, malilib.config.serialization.JsonConfigSerializerRegistry.ConfigToJsonSerializer, malilib.config.serialization.JsonConfigSerializerRegistry.ConfigFromJsonLoader)})
     * if you want to have your config usable for server-side config value overrides.
     */
    public <T, C extends BaseGenericConfig<T>> void registerGenericConfigSerializers(Class<C> type,
                                                                                     ConfigToJsonSerializer<C> serializer,
                                                                                     ValueFromJsonDeserializer<T, C> valueDeserializer)
    {
        ConfigFromJsonLoader<C> loader = (c, el) -> JsonConfigDeserializers.loadConfigValue(c, el, valueDeserializer.deserializeValue(c, el));

        this.registerSerializers(type, serializer, loader);
        this.registerGenericConfigValueDeserializer(type, valueDeserializer);
    }

    /**
     * Registers a config option serializer and deserializer
     */
    public <C extends ConfigInfo> void registerSerializers(Class<C> type,
                                                           ConfigToJsonSerializer<C> serializer,
                                                           ConfigFromJsonLoader<C> configValueLoader)
    {
        if (this.serializers.containsKey(type))
        {
            MaLiLib.LOGGER.warn("Tried to register a config value JSON serializer for {}, but one already exists", type.getName());
            return;
        }

        this.serializers.put(type, serializer);
        this.configValueLoader.put(type, configValueLoader);
    }

    /**
     * You can use this method to register a config value deserializer, that makes the config usable
     * for server-side config value overrides.
     */
    public <T, C extends BaseGenericConfig<?>> void registerGenericConfigValueDeserializer(Class<C> type,
                                                                                           ValueFromJsonDeserializer<T, C> valueDeserializer)
    {
        if (this.configValueDeserializers.containsKey(type))
        {
            MaLiLib.LOGGER.warn("Tried to register a BaseGenericConfig value JSON deserializer for {}, but one already exists", type.getName());
            return;
        }

        this.configValueDeserializers.put(type, valueDeserializer);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <C extends ConfigInfo> ConfigToJsonSerializer<C> getSerializer(ConfigInfo config)
    {
        ConfigToJsonSerializer<C> serializer = (ConfigToJsonSerializer<C>) this.serializers.get(config.getClass());

        if (serializer == null)
        {
            Class<?> clazz = config.getClass().getSuperclass();

            while (clazz != null && ConfigInfo.class.isAssignableFrom(clazz))
            {
                serializer = (ConfigToJsonSerializer<C>) this.serializers.get(clazz);

                if (serializer != null)
                {
                    return serializer;
                }

                clazz = clazz.getSuperclass();
            }
        }

        return serializer;
    }

    @SuppressWarnings("unchecked, rawtypes")
    @Nullable
    public <T, C extends BaseGenericConfig<T>> ConfigFromJsonOverrider<C> getOverrider(ConfigInfo config)
    {
        ValueFromJsonDeserializer<?, ? extends BaseGenericConfig> deserializer = this.configValueDeserializers.get(config.getClass());

        if (deserializer == null)
        {
            Class<?> clazz = config.getClass().getSuperclass();

            while (clazz != null && ConfigInfo.class.isAssignableFrom(clazz))
            {
                deserializer = this.configValueDeserializers.get(clazz);

                if (deserializer != null)
                {
                    break;
                }

                clazz = clazz.getSuperclass();
            }
        }

        ValueFromJsonDeserializer<T, C> deserializer2 = (ValueFromJsonDeserializer<T, C>) deserializer;

        return (c, el) -> deserializer2.deserializeValue(c, el).ifPresent(c::enableOverrideWithValue);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <C extends ConfigInfo> ConfigFromJsonLoader<C> getConfigValueLoader(ConfigInfo config)
    {
        ConfigFromJsonLoader<C> deSerializer = (ConfigFromJsonLoader<C>) this.configValueLoader.get(config.getClass());

        if (deSerializer == null)
        {
            Class<?> clazz = config.getClass().getSuperclass();

            while (clazz != null && ConfigInfo.class.isAssignableFrom(clazz))
            {
                deSerializer = (ConfigFromJsonLoader<C>) this.configValueLoader.get(clazz);

                if (deSerializer != null)
                {
                    return deSerializer;
                }

                clazz = clazz.getSuperclass();
            }
        }

        return deSerializer;
    }

    protected void registerDefaultSerializers()
    {
        this.registerSerializers(ColorConfig.class,                         JsonConfigSerializers::serializeColorConfig,            (c, el) -> JsonConfigDeserializers.loadPrimitiveConfig(el, JsonElement::getAsString, c::loadColorValueFromString, c::getName));
        this.registerSerializers(HotkeyConfig.class,                        JsonConfigSerializers::serializeHotkeyConfig,           JsonConfigDeserializers::loadHotkeyConfig);

        this.registerSerializers(HotkeyedBooleanConfig.class,               JsonConfigSerializers::serializeHotkeyedBooleanConfig,  JsonConfigDeserializers::loadHotkeyedBooleanConfig);
        this.registerGenericConfigValueDeserializer(HotkeyedBooleanConfig.class, (c, el) -> JsonConfigDeserializers.readBooleanFromHotkeyedBoolean(el));

        this.registerGenericConfigSerializers(BooleanConfig.class,          JsonConfigSerializers::serializeBooleanConfig,          (c, el) -> JsonDeserializers.readPrimitiveValue(el, JsonElement::getAsBoolean));
        this.registerGenericConfigSerializers(DirectoryConfig.class,        JsonConfigSerializers::serializeFileConfig,             (c, el) -> JsonDeserializers.readPath(el));
        this.registerGenericConfigSerializers(DoubleConfig.class,           JsonConfigSerializers::serializeDoubleConfig,           (c, el) -> JsonDeserializers.readPrimitiveValue(el, JsonElement::getAsDouble));
        this.registerGenericConfigSerializers(FileConfig.class,             JsonConfigSerializers::serializeFileConfig,             (c, el) -> JsonDeserializers.readPath(el));
        this.registerGenericConfigSerializers(IntegerConfig.class,          JsonConfigSerializers::serializeIntegerConfig,          (c, el) -> JsonDeserializers.readPrimitiveValue(el, JsonElement::getAsInt));
        this.registerGenericConfigSerializers(StringConfig.class,           JsonConfigSerializers::serializeStringConfig,           (c, el) -> JsonDeserializers.readPrimitiveValue(el, JsonElement::getAsString));

        this.registerGenericConfigSerializers(BlackWhiteListConfig.class,   JsonConfigSerializers::serializeBlackWhiteListConfig,   (c, el) -> JsonDeserializers.readBlackWhiteListValue(el, c));
        this.registerGenericConfigSerializers(BooleanAndDoubleConfig.class, JsonConfigSerializers::serializeBooleanAndDoubleConfig, (c, el) -> JsonDeserializers.readBooleanAndDoubleValue(el));
        this.registerGenericConfigSerializers(BooleanAndFileConfig.class,   JsonConfigSerializers::serializeBooleanAndFileConfig,   (c, el) -> JsonDeserializers.readBooleanAndFileValue(el));
        this.registerGenericConfigSerializers(BooleanAndIntConfig.class,    JsonConfigSerializers::serializeBooleanAndIntConfig,    (c, el) -> JsonDeserializers.readBooleanAndIntValue(el));
        this.registerGenericConfigSerializers(DualColorConfig.class,        JsonConfigSerializers::serializeDualColorConfig,        (c, el) -> JsonDeserializers.readDualColorValue(el));
        this.registerGenericConfigSerializers(OptionConfig.class, JsonConfigSerializers::serializeOptionListConfig, (c, el) -> JsonDeserializers.readOptionListValue(el, c.getAllValues()));
        this.registerGenericConfigSerializers(ValueListConfig.class,        JsonConfigSerializers::serializeValueListConfig,        (c, el) -> JsonDeserializers.readValueList(el, c.getFromStringConverter()));
        this.registerGenericConfigSerializers(Vec2dConfig.class,            JsonConfigSerializers::serializeVec2dConfig,            (c, el) -> JsonDeserializers.readVec2dValue(el));
        this.registerGenericConfigSerializers(Vec2iConfig.class,            JsonConfigSerializers::serializeVec2iConfig,            (c, el) -> JsonDeserializers.readVec2iValue(el));

    }

    public interface ConfigToJsonSerializer<C extends ConfigInfo>
    {
        JsonElement configValueToJson(C config);
    }

    @SuppressWarnings("rawtypes")
    public interface ValueFromJsonDeserializer<T, C extends BaseGenericConfig>
    {
        Optional<T> deserializeValue(C config, JsonElement data);
    }

    public interface ConfigFromJsonLoader<C extends ConfigInfo>
    {
        void loadConfigValue(C config, JsonElement data);
    }

    public interface ConfigFromJsonOverrider<C extends ConfigInfo>
    {
        void overrideConfigValue(C config, JsonElement data);
    }
}
