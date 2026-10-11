package fi.dy.masa.malilib.input;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;
import fi.dy.masa.malilib.util.MessageOutputType;
import fi.dy.masa.malilib.util.data.json.JsonUtils;

@ApiStatus.Experimental
public class KeyBindSettings
{
    public static final Codec<KeyBindSettings> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                                        BaseOptionListConfigValue.CODEC.fieldOf("context").forGetter(get -> get.context),
                                        BaseOptionListConfigValue.CODEC.fieldOf("activate_on").forGetter(get -> get.activateOn),
		                                MessageOutputType.CODEC.optionalFieldOf("message_output", MessageOutputType.NONE).forGetter(get -> get.messageOutput),
		                                BaseOptionListConfigValue.CODEC.optionalFieldOf("cancel", CancelCondition.NEVER).forGetter(get -> get.cancel),
		                                Codec.BOOL.optionalFieldOf("allow_empty", false).forGetter(get -> get.allowEmpty),
		                                Codec.BOOL.optionalFieldOf("allow_extra_keys", false).forGetter(get -> get.allowExtraKeys),
		                                Codec.BOOL.optionalFieldOf("exclusive", false).forGetter(get -> get.exclusive),
		                                Codec.BOOL.optionalFieldOf("first_only", false).forGetter(get -> get.firstOnly),
		                                Codec.BOOL.optionalFieldOf("invert", false).forGetter(get -> get.invertHeld),
		                                Codec.BOOL.optionalFieldOf("order_sensitive", true).forGetter(get -> get.orderSensitive),
		                                Codec.INT.optionalFieldOf("priority", 50).forGetter(get -> get.priority),
		                                Codec.BOOL.optionalFieldOf("show_toast", true).forGetter(get -> get.showToast),
		                                Codec.BOOL.optionalFieldOf("toggle", false).forGetter(get -> get.toggle),
		                                Codec.BOOL.optionalFieldOf("useScrollAdjusting", true).forGetter(get -> get.useScrollAdjusting)
//                            Codec.BOOL.fieldOf("cancel").forGetter(get -> get.cancel),
                    )
                    .apply(instance, KeyBindSettings::new)
    );
    public static final KeyBindSettings DEFAULT                     = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, false, true, false, true);
    public static final KeyBindSettings EXCLUSIVE                   = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, false, true, true, true);
    public static final KeyBindSettings RELEASE                     = new KeyBindSettings(Context.INGAME, KeyAction.RELEASE, false, true, false, false);
    public static final KeyBindSettings RELEASE_ALLOW_EXTRA         = new KeyBindSettings(Context.INGAME, KeyAction.RELEASE, true, true, false, false);
    public static final KeyBindSettings RELEASE_EXCLUSIVE           = new KeyBindSettings(Context.INGAME, KeyAction.RELEASE, false, true, true, true);
    public static final KeyBindSettings NOCANCEL                    = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, false, true, false, false);
    public static final KeyBindSettings PRESS_ALLOWEXTRA            = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, true, true, false, true);
    public static final KeyBindSettings PRESS_ALLOWEXTRA_EMPTY      = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, true, true, false, true, true);
    public static final KeyBindSettings PRESS_NON_ORDER_SENSITIVE   = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, false, false, false, true);
    public static final KeyBindSettings INGAME_BOTH                 = new KeyBindSettings(Context.INGAME, KeyAction.BOTH, false, true, false, true);
    public static final KeyBindSettings MODIFIER_INGAME             = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, true, false, false, false);
    public static final KeyBindSettings MODIFIER_INGAME_EMPTY       = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, true, false, false, false, true);
    public static final KeyBindSettings MODIFIER_GUI                = new KeyBindSettings(Context.GUI, KeyAction.PRESS, true, false, false, false);
    public static final KeyBindSettings GUI                         = new KeyBindSettings(Context.GUI, KeyAction.PRESS, false, true, false, true);

    private final Context context;
    private final KeyAction activateOn;
    private final CancelCondition cancel;               // WIP
    private final MessageOutputType messageOutput;
    private final boolean allowEmpty;
    private final boolean allowExtraKeys;
    private final boolean exclusive;
    private final boolean firstOnly;                    // WIP
    private final boolean invertHeld;                   // WIP
    private final boolean orderSensitive;
    private final boolean showToast;                    // WIP
    private final boolean toggle;                       // WIP
    private final boolean useScrollAdjusting;           // WIP
    private final int priority;
//    private final boolean cancel;

    private KeyBindSettings(Context context, KeyAction activateOn, boolean allowExtraKeys, boolean orderSensitive, boolean exclusive, boolean cancel)
    {
        this(context, activateOn, allowExtraKeys, orderSensitive, exclusive, cancel, false);
    }

    private KeyBindSettings(Context context, KeyAction activateOn, boolean allowExtraKeys, boolean orderSensitive, boolean exclusive, boolean cancel, boolean allowEmpty)
    {
        this(context, activateOn, allowExtraKeys, orderSensitive,
             cancel ? CancelCondition.ALWAYS : CancelCondition.NEVER,
             exclusive, false, 50,
             allowEmpty, false, false, true,
             true, MessageOutputType.NONE);
    }

    // todo
    private KeyBindSettings(Context context, KeyAction activateOn,
                            boolean allowExtraKeys, boolean orderSensitive,
                            CancelCondition cancel,
                            boolean exclusive, boolean firstOnly, int priority,
                            boolean allowEmpty, boolean toggle, boolean invertHeld,
                            boolean showToast, boolean useScrollAdjusting, MessageOutputType messageOutput)
    {
        this.context = context;
        this.activateOn = activateOn;
        this.cancel = cancel;
        this.messageOutput = messageOutput;
        this.allowEmpty = allowEmpty;
        this.allowExtraKeys = allowExtraKeys;
        this.exclusive = exclusive;
        this.firstOnly = firstOnly;
        this.priority = priority;
        this.invertHeld = invertHeld;
        this.orderSensitive = orderSensitive;
        this.showToast = showToast;
        this.toggle = toggle;
        this.useScrollAdjusting = useScrollAdjusting;
//        this.cancel = false;
    }

    private KeyBindSettings(@NotNull BaseOptionListConfigValue context,
                            @NotNull BaseOptionListConfigValue activateOn,
                            @NotNull MessageOutputType messageOutput,
                            BaseOptionListConfigValue cancel,
                            Boolean allowEmpty, Boolean allowExtraKeys, Boolean exclusive,
                            Boolean firstOnly, Boolean invert, Boolean orderSensitive, Integer priority,
                            Boolean showToast, Boolean toggle, Boolean scrollAdjusting)
    {
        this((Context) context, (KeyAction) activateOn, allowExtraKeys, orderSensitive, (CancelCondition) cancel, exclusive, firstOnly, priority, allowEmpty, toggle, invert, showToast, scrollAdjusting, messageOutput);
    }

    public static KeyBindSettings create(Context context, KeyAction activateOn, boolean allowExtraKeys, boolean orderSensitive, boolean exclusive, boolean cancel)
    {
        return create(context, activateOn, allowExtraKeys, orderSensitive, exclusive, cancel, false);
    }

    public static KeyBindSettings create(Context context, KeyAction activateOn, boolean allowExtraKeys, boolean orderSensitive, boolean exclusive, boolean cancel, boolean allowEmpty)
    {
        return new KeyBindSettings(context, activateOn, allowExtraKeys, orderSensitive, exclusive, cancel, allowEmpty);
    }

    // todo
    public static KeyBindSettings create(Context context, KeyAction activateOn,
                                         boolean allowExtraKeys, boolean orderSensitive,
                                         CancelCondition cancelCondition,
                                         boolean exclusive, boolean firstOnly, int priority,
                                         boolean allowEmpty, boolean toggle, boolean invertHeld,
                                         boolean showToast, boolean useScrollAdjusting, MessageOutputType messageOutput)
    {
        return new KeyBindSettings(context, activateOn, allowExtraKeys, orderSensitive, cancelCondition, exclusive, firstOnly, priority, allowEmpty, toggle, invertHeld, showToast, useScrollAdjusting, messageOutput);
    }

    public Context getContext()
    {
        return this.context;
    }

    public KeyAction getActivateOn()
    {
        return this.activateOn;
    }

    public CancelCondition getCancelCondition()
    {
        return this.cancel;
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

    public boolean getFirstOnly()
    {
        return this.firstOnly;
    }

    public boolean getInvertHeld()
    {
        return this.invertHeld;
    }

    public int getPriority()
    {
        return this.priority;
    }

    public boolean getShowToast()
    {
        return this.showToast;
    }

    public boolean isToggle()
    {
        return this.toggle;
    }

    public boolean useScrollAdjusting()
    {
        return this.useScrollAdjusting;
    }

    public MessageOutputType getMessageType()
    {
        return this.messageOutput;
    }

    public boolean shouldCancel()
    {
        return this.cancel == CancelCondition.ALWAYS || this.cancel == CancelCondition.ON_SUCCESS;
//        return this.cancel;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("activate_on", this.activateOn.getName());
        obj.addProperty("allow_empty", this.allowEmpty);
        obj.addProperty("allow_extra_keys", this.allowExtraKeys);
        obj.addProperty("cancel", this.cancel.getName());
        obj.addProperty("context", this.context.getName());
        obj.addProperty("exclusive", this.exclusive);
        obj.addProperty("first_only", this.firstOnly);
        obj.addProperty("invert", this.invertHeld);
        obj.addProperty("message_output", this.messageOutput.name());
        obj.addProperty("order_sensitive", this.orderSensitive);
        obj.addProperty("priority", this.priority);
        obj.addProperty("show_toast", this.showToast);
        obj.addProperty("toggle", this.toggle);
        obj.addProperty("useScrollAdjusting", this.useScrollAdjusting);

//        obj.addProperty("cancel", this.cancel);

        return obj;
    }

    public JsonObject toJsonCodec()
    {
        return (JsonObject) CODEC
                .encodeStart(JsonOps.INSTANCE, this)
                .resultOrPartial((err) -> MaLiLib.LOGGER.warn("KeybindSettings#toJsonCodec(): Error {}", err))
                .orElse(new JsonObject());
    }

    public static @Nullable KeyBindSettings fromJsonCodec(JsonObject obj)
    {
        com.mojang.datafixers.util.Pair<KeyBindSettings, JsonElement> pair = CODEC
                .decode(JsonOps.INSTANCE, obj)
                .resultOrPartial((err) -> MaLiLib.LOGGER.warn("KeybindSettings#fromJsonCodec(): Error {}", err))
                .orElse(null);

        if (pair != null && pair.getFirst() != null)
        {
            return pair.getFirst();
        }

        return null;
    }

    public static KeyBindSettings fromJson(JsonObject obj)
    {
        Context context = Context.INGAME;
        KeyAction activateOn = KeyAction.PRESS;
        MessageOutputType messageOutput = MessageOutputType.NONE;
        String contextStr = JsonUtils.getString(obj, "context");
        String activateStr = JsonUtils.getString(obj, "activate_on");
        String messageTypeStr = JsonUtils.getStringOrDefault(obj, "message_output", MessageOutputType.NONE.name());

        if (contextStr != null)
        {
            context = BaseOptionListConfigValue.findValueByName(contextStr, Context.VALUES);
        }

        if (activateStr != null)
        {
            activateOn = BaseOptionListConfigValue.findValueByName(activateStr, KeyAction.VALUES);
        }

        if (messageTypeStr != null)
        {
            for (MessageOutputType msg : MessageOutputType.values())
            {
                if (msg.name().equalsIgnoreCase(messageTypeStr))
                {
                    messageOutput = msg;
                    break;
                }
            }
        }

        String cancelName = JsonUtils.getStringOrDefault(obj, "cancel_condition", "false");
        CancelCondition cancel;

        // Backwards compatibility with the old boolean value
        if (cancelName.equalsIgnoreCase("true"))
        {
            cancel = CancelCondition.ALWAYS;
        }
        else if (cancelName.equalsIgnoreCase("false"))
        {
            cancel = CancelCondition.NEVER;
        }
        else
        {
            cancel = BaseOptionListConfigValue.findValueByName(cancelName, CancelCondition.VALUES);
        }

        boolean allowEmpty = JsonUtils.getBoolean(obj, "allow_empty");
        boolean allowExtraKeys = JsonUtils.getBoolean(obj, "allow_extra_keys");
        boolean exclusive = JsonUtils.getBooleanOrDefault(obj, "exclusive", false);
        boolean firstOnly = JsonUtils.getBooleanOrDefault(obj, "first_only", false);
        boolean invert = JsonUtils.getBooleanOrDefault(obj, "invert", false);
        boolean orderSensitive = JsonUtils.getBooleanOrDefault(obj, "order_sensitive", true);
        int priority = JsonUtils.getIntegerOrDefault(obj, "priority", 50);
        boolean showToast = JsonUtils.getBooleanOrDefault(obj, "show_toast", true);
        boolean toggle = JsonUtils.getBooleanOrDefault(obj, "toggle", true);
        boolean useScrollAdjusting = JsonUtils.getBooleanOrDefault(obj, "useScrollAdjusting", true);
//        boolean cancel = JsonUtils.getBooleanOrDefault(obj, "cancel", true);

        return create(context, activateOn, allowExtraKeys, orderSensitive, cancel, exclusive, firstOnly, priority, allowEmpty, toggle, invert, showToast, useScrollAdjusting, messageOutput);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) { return true; }
        if (obj == null || this.getClass() != obj.getClass()) { return false; }

        KeyBindSettings other = (KeyBindSettings) obj;

        return  this.activateOn == other.activateOn &&
                this.context == other.context &&
                this.allowEmpty == other.allowEmpty &&
                this.allowExtraKeys == other.allowExtraKeys &&
                this.cancel == other.cancel &&
                this.exclusive == other.exclusive &&
                this.firstOnly == other.firstOnly &&
                this.invertHeld == other.invertHeld &&
                this.messageOutput == other.messageOutput &&
                this.orderSensitive == other.orderSensitive &&
                this.priority == other.priority &&
                this.showToast == other.showToast &&
                this.toggle == other.toggle &&
                this.useScrollAdjusting == other.useScrollAdjusting;
    }
}
