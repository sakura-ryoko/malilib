package fi.dy.masa.malilib.hotkeys;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.StringIdentifiable;

public class KeybindSettings
{
    public static final Codec<KeybindSettings> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Context.CODEC.fieldOf("context").forGetter(get -> get.context),
                            KeyAction.CODEC.fieldOf("activateOn").forGetter(get -> get.activateOn),
                            Codec.BOOL.fieldOf("allowExtraKeys").forGetter(get -> get.allowExtraKeys),
                            Codec.BOOL.fieldOf("orderSensitive").forGetter(get -> get.orderSensitive),
                            Codec.BOOL.fieldOf("exclusive").forGetter(get -> get.exclusive),
                            Codec.BOOL.fieldOf("cancel").forGetter(get -> get.cancel),
                            Codec.BOOL.optionalFieldOf("allowEmpty", false).forGetter(get -> get.allowEmpty)
                    )
                    .apply(instance, KeybindSettings::new)
    );
    public static final KeybindSettings DEFAULT                     = new KeybindSettings(Context.INGAME, KeyAction.PRESS, false, true, false, true);
    public static final KeybindSettings EXCLUSIVE                   = new KeybindSettings(Context.INGAME, KeyAction.PRESS, false, true, true, true);
    public static final KeybindSettings RELEASE                     = new KeybindSettings(Context.INGAME, KeyAction.RELEASE, false, true, false, false);
    public static final KeybindSettings RELEASE_ALLOW_EXTRA         = new KeybindSettings(Context.INGAME, KeyAction.RELEASE, true, true, false, false);
    public static final KeybindSettings RELEASE_EXCLUSIVE           = new KeybindSettings(Context.INGAME, KeyAction.RELEASE, false, true, true, true);
    public static final KeybindSettings NOCANCEL                    = new KeybindSettings(Context.INGAME, KeyAction.PRESS, false, true, false, false);
    public static final KeybindSettings PRESS_ALLOWEXTRA            = new KeybindSettings(Context.INGAME, KeyAction.PRESS, true, true, false, true);
    public static final KeybindSettings PRESS_ALLOWEXTRA_EMPTY      = new KeybindSettings(Context.INGAME, KeyAction.PRESS, true, true, false, true, true);
    public static final KeybindSettings PRESS_NON_ORDER_SENSITIVE   = new KeybindSettings(Context.INGAME, KeyAction.PRESS, false, false, false, true);
    public static final KeybindSettings INGAME_BOTH                 = new KeybindSettings(Context.INGAME, KeyAction.BOTH, false, true, false, true);
    public static final KeybindSettings MODIFIER_INGAME             = new KeybindSettings(Context.INGAME, KeyAction.PRESS, true, false, false, false);
    public static final KeybindSettings MODIFIER_INGAME_EMPTY       = new KeybindSettings(Context.INGAME, KeyAction.PRESS, true, false, false, false, true);
    public static final KeybindSettings MODIFIER_GUI                = new KeybindSettings(Context.GUI,    KeyAction.PRESS, true, false, false, false);
    public static final KeybindSettings GUI                         = new KeybindSettings(Context.GUI,    KeyAction.PRESS, false, true, false, true);

    private final Context context;
    private final KeyAction activateOn;
    private final boolean allowEmpty;
    private final boolean allowExtraKeys;
    private final boolean orderSensitive;
    private final boolean exclusive;
    private final boolean cancel;

    private KeybindSettings(Context context, KeyAction activateOn, boolean allowExtraKeys, boolean orderSensitive, boolean exclusive, boolean cancel)
    {
        this(context, activateOn, allowExtraKeys, orderSensitive, exclusive, cancel, false);
    }

    private KeybindSettings(Context context, KeyAction activateOn, boolean allowExtraKeys, boolean orderSensitive, boolean exclusive, boolean cancel, boolean allowEmpty)
    {
        this.context = context;
        this.activateOn = activateOn;
        this.allowExtraKeys = allowExtraKeys;
        this.orderSensitive = orderSensitive;
        this.exclusive = exclusive;
        this.cancel = cancel;
        this.allowEmpty = allowEmpty;
    }

    public static KeybindSettings create(Context context, KeyAction activateOn, boolean allowExtraKeys, boolean orderSensitive, boolean exclusive, boolean cancel)
    {
        return create(context, activateOn, allowExtraKeys, orderSensitive, exclusive, cancel, false);
    }

    public static KeybindSettings create(Context context, KeyAction activateOn, boolean allowExtraKeys, boolean orderSensitive, boolean exclusive, boolean cancel, boolean allowEmpty)
    {
        return new KeybindSettings(context, activateOn, allowExtraKeys, orderSensitive, exclusive, cancel, allowEmpty);
    }

    public Context getContext()
    {
        return this.context;
    }

    public KeyAction getActivateOn()
    {
        return this.activateOn;
    }

    public boolean getAllowEmpty()
    {
        return this.allowEmpty;
    }

    public boolean getAllowExtraKeys()
    {
        return this.allowExtraKeys;
    }

    public boolean isOrderSensitive()
    {
        return this.orderSensitive;
    }

    public boolean isExclusive()
    {
        return this.exclusive;
    }

    public boolean shouldCancel()
    {
        return this.cancel;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("activate_on", this.activateOn.name());
        obj.addProperty("context", this.context.name());
        obj.addProperty("allow_empty", this.allowEmpty);
        obj.addProperty("allow_extra_keys", this.allowExtraKeys);
        obj.addProperty("order_sensitive", this.orderSensitive);
        obj.addProperty("exclusive", this.exclusive);
        obj.addProperty("cancel", this.cancel);

        return obj;
    }

    public JsonObject toJsonCodec()
    {
        return (JsonObject) CODEC
                .encodeStart(JsonOps.INSTANCE, this)
                .resultOrPartial((err) -> MaLiLib.LOGGER.warn("KeybindSettings#toJsonCodec(): Error {}", err))
                .orElse(new JsonObject());
    }

    public static @Nullable KeybindSettings fromJsonCodec(JsonObject obj)
    {
        com.mojang.datafixers.util.Pair<KeybindSettings, JsonElement> pair = CODEC
                .decode(JsonOps.INSTANCE, obj)
                .resultOrPartial((err) -> MaLiLib.LOGGER.warn("KeybindSettings#fromJsonCodec(): Error {}", err))
                .orElse(null);

        if (pair != null && pair.getFirst() != null)
        {
            return pair.getFirst();
        }

        return null;
    }

    public static KeybindSettings fromJson(JsonObject obj)
    {
        Context context = Context.INGAME;
        KeyAction activateOn = KeyAction.PRESS;
        String contextStr = JsonUtils.getString(obj, "context");
        String activateStr = JsonUtils.getString(obj, "activate_on");

        if (contextStr != null)
        {
            for (Context ctx : Context.values())
            {
                if (ctx.name().equalsIgnoreCase(contextStr))
                {
                    context = ctx;
                    break;
                }
            }
        }

        if (activateStr != null)
        {
            for (KeyAction act : KeyAction.values())
            {
                if (act.name().equalsIgnoreCase(activateStr))
                {
                    activateOn = act;
                    break;
                }
            }
        }

        boolean allowEmpty = JsonUtils.getBoolean(obj, "allow_empty");
        boolean allowExtraKeys = JsonUtils.getBoolean(obj, "allow_extra_keys");
        boolean orderSensitive = JsonUtils.getBooleanOrDefault(obj, "order_sensitive", true);
        boolean exclusive = JsonUtils.getBooleanOrDefault(obj, "exclusive", true);
        boolean cancel = JsonUtils.getBooleanOrDefault(obj, "cancel", true);

        return create(context, activateOn, allowExtraKeys, orderSensitive, exclusive, cancel, allowEmpty);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KeybindSettings other = (KeybindSettings) obj;
        if (activateOn != other.activateOn)
            return false;
        if (context != other.context)
            return false;
        if (allowEmpty != other.allowEmpty)
            return false;
        if (allowExtraKeys != other.allowExtraKeys)
            return false;
        if (cancel != other.cancel)
            return false;
        if (exclusive != other.exclusive)
            return false;
        if (orderSensitive != other.orderSensitive)
            return false;
        return true;
    }

    public enum Context implements IConfigOptionListEntry, StringIdentifiable
    {
        INGAME  ("ingame",  "malilib.label.key_context.ingame"),
        GUI     ("gui",     "malilib.label.key_context.gui"),
        ANY     ("any",     "malilib.label.key_context.any");

        public static final StringIdentifiable.EnumCodec<Context> CODEC = StringIdentifiable.createCodec(Context::values);
        public static final ImmutableList<Context> VALUES = ImmutableList.copyOf(values());

        private final String configString;
        private final String translationKey;

        Context(String configString, String translationKey)
        {
            this.configString = configString;
            this.translationKey = translationKey;
        }

        @Override
        public @Nonnull String asString()
        {
            return this.configString;
        }

        @Override
        public String getStringValue()
        {
            return this.configString;
        }

        @Override
        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }

        @Override
        public IConfigOptionListEntry cycle(boolean forward)
        {
            int id = this.ordinal();

            if (forward)
            {
                if (++id >= values().length)
                {
                    id = 0;
                }
            }
            else
            {
                if (--id < 0)
                {
                    id = values().length - 1;
                }
            }

            return values()[id % values().length];
        }

        @Override
        public Context fromString(String name)
        {
            return fromStringStatic(name);
        }

        public static Context fromStringStatic(String name)
        {
            for (Context context : Context.values())
            {
                if (context.configString.equalsIgnoreCase(name))
                {
                    return context;
                }
            }

            return Context.INGAME;
        }
    }
}
