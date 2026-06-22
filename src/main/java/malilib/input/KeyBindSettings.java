package malilib.input;

import com.google.gson.JsonObject;

import malilib.config.value.BaseOptionListConfigValue;
import malilib.overlay.message.MessageOutput;
import malilib.util.data.json.JsonUtils;

public class KeyBindSettings
{
    public static final KeyBindSettings INGAME_DEFAULT              = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, false, true, CancelCondition.ON_SUCCESS);
    public static final KeyBindSettings INGAME_EXTRA                = builder().extra().build();
    public static final KeyBindSettings INGAME_BOTH                 = new KeyBindSettings(Context.INGAME, KeyAction.BOTH, false, true, CancelCondition.ON_SUCCESS);
    public static final KeyBindSettings INGAME_MODIFIER             = builderInGameModifier().build();
    public static final KeyBindSettings INGAME_MODIFIER_EMPTY       = builderInGameModifier().empty().build();
    public static final KeyBindSettings INGAME_MODIFIER_BOTH        = builderInGameModifier().both().build();
    public static final KeyBindSettings INGAME_RELEASE              = new KeyBindSettings(Context.INGAME, KeyAction.RELEASE, false, true, CancelCondition.ALWAYS);
    public static final KeyBindSettings INGAME_RELEASE_EXCLUSIVE    = builder().release().exclusive().cancel(CancelCondition.ALWAYS).build();
    public static final KeyBindSettings GUI_DEFAULT                 = new KeyBindSettings(Context.GUI, KeyAction.PRESS, false, true, CancelCondition.ON_SUCCESS);
    public static final KeyBindSettings GUI_MODIFIER                = new KeyBindSettings(Context.GUI, KeyAction.PRESS, false, false, CancelCondition.NEVER);

    protected final KeyAction activateOn;
    protected final Context context;
    protected final CancelCondition cancel;
    protected final MessageOutput messageOutput;
    protected final boolean allowEmpty;
    protected final boolean allowExtraKeys;
    protected final boolean exclusive;
    protected final boolean firstOnly;
    protected final boolean invertHeld;
    protected final boolean orderSensitive;
    protected final boolean showToast;
    protected final boolean toggle;
    protected final boolean useScrollAdjusting;
    protected final int priority;

    public KeyBindSettings(Context context, KeyAction activateOn,
                           boolean allowExtraKeys, boolean orderSensitive,
                           CancelCondition cancel)
    {
        this(context, activateOn,
             allowExtraKeys, orderSensitive,
             cancel,
             false, false, 50,
             false, false, false,
             true, true, MessageOutput.DEFAULT_TOGGLE);
    }

    protected KeyBindSettings(Context context, KeyAction activateOn,
                              boolean allowExtraKeys, boolean orderSensitive,
                              CancelCondition cancel,
                              boolean exclusive, boolean firstOnly, int priority,
                              boolean allowEmpty, boolean toggle, boolean invertHeld,
                              boolean showToast, boolean useScrollAdjusting, MessageOutput messageOutput)
    {
        this.activateOn = activateOn;
        this.context = context;
        this.cancel = cancel;
        this.messageOutput = messageOutput;
        this.allowEmpty = allowEmpty;
        this.allowExtraKeys = allowExtraKeys;
        this.orderSensitive = orderSensitive;
        this.exclusive = exclusive;
        this.firstOnly = firstOnly;
        this.invertHeld = invertHeld;
        this.priority = priority;
        this.showToast = showToast;
        this.toggle = toggle;
        this.useScrollAdjusting = useScrollAdjusting;
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

    public MessageOutput getMessageType()
    {
        return this.messageOutput;
    }

    public Builder asBuilder()
    {
        return new Builder(this.context, this.activateOn,
                           this.allowExtraKeys, this.orderSensitive,
                           this.cancel,
                           this.exclusive, this.firstOnly, this.priority,
                           this.allowEmpty, this.toggle, this.invertHeld,
                           this.showToast, this.useScrollAdjusting, this.messageOutput);
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
        obj.addProperty("message_output", this.messageOutput.getName());
        obj.addProperty("order_sensitive", this.orderSensitive);
        obj.addProperty("priority", this.priority);
        obj.addProperty("show_toast", this.showToast);
        obj.addProperty("toggle", this.toggle);
        obj.addProperty("useScrollAdjusting", this.useScrollAdjusting);

        return obj;
    }

    public static KeyBindSettings fromJson(JsonObject obj)
    {
        Context context = Context.INGAME;
        KeyAction activateOn = KeyAction.PRESS;
        MessageOutput messageOutput = MessageOutput.DEFAULT_TOGGLE;
        String contextStr = JsonUtils.getString(obj, "context");
        String activateStr = JsonUtils.getString(obj, "activate_on");
        String messageTypeStr = JsonUtils.getString(obj, "message_output");

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
            messageOutput = BaseOptionListConfigValue.findValueByName(messageTypeStr, MessageOutput.getValues());
        }

        String cancelName = JsonUtils.getStringOrDefault(obj, "cancel", "false");
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

        return builder()
                .context(context)
                .activateOn(activateOn)
                .cancel(cancel)
                .messageOutput(messageOutput)
                .allowEmpty(JsonUtils.getBoolean(obj, "allow_empty"))
                .allowExtraKeys(JsonUtils.getBoolean(obj, "allow_extra_keys"))
                .exclusive(JsonUtils.getBoolean(obj, "exclusive"))
                .firstOnly(JsonUtils.getBoolean(obj, "first_only"))
                .invertHeld(JsonUtils.getBoolean(obj, "invert"))
                .orderSensitive(JsonUtils.getBooleanOrDefault(obj, "order_sensitive", true))
                .priority(JsonUtils.getIntegerOrDefault(obj, "priority", 50))
                .showToast(JsonUtils.getBooleanOrDefault(obj, "show_toast", true))
                .toggle(JsonUtils.getBoolean(obj, "toggle"))
                .useScrollAdjusting(JsonUtils.getBooleanOrDefault(obj, "useScrollAdjusting", true))
                .build();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) { return true; }
        if (obj == null || this.getClass() != obj.getClass()) { return false; }

        KeyBindSettings other = (KeyBindSettings) obj;

        return this.activateOn == other.activateOn &&
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

    public static class Builder
    {
        protected Context context = Context.INGAME;
        protected KeyAction activateOn = KeyAction.PRESS;
        protected CancelCondition cancel = CancelCondition.ON_SUCCESS;
        protected MessageOutput messageOutput = MessageOutput.DEFAULT_TOGGLE;
        protected boolean allowEmpty;
        protected boolean allowExtraKeys;
        protected boolean exclusive;
        protected boolean firstOnly;
        protected boolean invertHeld;
        protected boolean orderSensitive = true;
        protected boolean showToast = true;
        protected boolean toggle;
        protected boolean useScrollAdjusting = true;
        protected int priority = 50;

        public Builder()
        {
        }

        public Builder(Context context, KeyAction activateOn,
                       boolean allowExtraKeys, boolean orderSensitive,
                       CancelCondition cancel,
                       boolean exclusive, boolean firstOnly, int priority,
                       boolean allowEmpty, boolean toggle, boolean invertHeld,
                       boolean showToast, boolean useScrollAdjusting, MessageOutput messageOutput)
        {
            this.context = context;
            this.activateOn = activateOn;
            this.cancel = cancel;
            this.messageOutput = messageOutput;

            this.allowEmpty = allowEmpty;
            this.allowExtraKeys = allowExtraKeys;
            this.exclusive = exclusive;
            this.firstOnly = firstOnly;
            this.invertHeld = invertHeld;
            this.orderSensitive = orderSensitive;
            this.priority = priority;
            this.showToast = showToast;
            this.toggle = toggle;
            this.useScrollAdjusting = useScrollAdjusting;
        }

        public Builder activateOn(KeyAction activateOn)
        {
            this.activateOn = activateOn;
            return this;
        }

        public Builder context(Context context)
        {
            this.context = context;
            return this;
        }

        public Builder cancel(CancelCondition cancel)
        {
            this.cancel = cancel;
            return this;
        }

        public Builder messageOutput(MessageOutput messageOutput)
        {
            this.messageOutput = messageOutput;
            return this;
        }

        public Builder allowEmpty(boolean allowEmpty)
        {
            this.allowEmpty = allowEmpty;
            return this;
        }

        public Builder allowExtraKeys(boolean allowExtraKeys)
        {
            this.allowExtraKeys = allowExtraKeys;
            return this;
        }

        public Builder exclusive(boolean exclusive)
        {
            this.exclusive = exclusive;
            return this;
        }

        public Builder firstOnly(boolean firstOnly)
        {
            this.firstOnly = firstOnly;
            return this;
        }

        public Builder invertHeld(boolean invert)
        {
            this.invertHeld = invert;
            return this;
        }

        public Builder orderSensitive(boolean orderSensitive)
        {
            this.orderSensitive = orderSensitive;
            return this;
        }

        public Builder priority(int priority)
        {
            this.priority = priority;
            return this;
        }

        public Builder showToast(boolean showToast)
        {
            this.showToast = showToast;
            return this;
        }

        public Builder toggle(boolean toggle)
        {
            this.toggle = toggle;
            return this;
        }

        public Builder useScrollAdjusting(boolean useScrollAdjusting)
        {
            this.useScrollAdjusting = useScrollAdjusting;
            return this;
        }

        public Builder gui()
        {
            this.context = Context.GUI;
            return this;
        }

        public Builder press()
        {
            this.activateOn = KeyAction.PRESS;
            return this;
        }

        public Builder release()
        {
            this.activateOn = KeyAction.RELEASE;
            return this;
        }

        public Builder both()
        {
            this.activateOn = KeyAction.BOTH;
            return this;
        }

        public Builder noOrder()
        {
            this.orderSensitive = false;
            return this;
        }

        public Builder empty()
        {
            this.allowEmpty = true;
            return this;
        }

        public Builder extra()
        {
            this.allowExtraKeys = true;
            return this;
        }

        public Builder exclusive()
        {
            this.exclusive = true;
            return this;
        }

        public Builder invert()
        {
            this.invertHeld = true;
            return this;
        }

        public Builder toggle()
        {
            this.toggle = true;
            return this;
        }

        public Builder noCancel()
        {
            this.cancel = CancelCondition.NEVER;
            return this;
        }

        public Builder noOutput()
        {
            this.messageOutput = MessageOutput.NONE;
            this.showToast = false;
            return this;
        }

        public KeyBindSettings build()
        {
            return new KeyBindSettings(this.context, this.activateOn,
                                       this.allowExtraKeys, this.orderSensitive,
                                       this.cancel,
                                       this.exclusive, this.firstOnly, this.priority,
                                       this.allowEmpty, this.toggle, this.invertHeld,
                                       this.showToast, this.useScrollAdjusting, this.messageOutput);
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static Builder builderInGameModifier()
    {
        Builder builder = new Builder();
        return builder.extra().noOrder().noCancel().noOutput();
    }

    public static Builder builderGuiModifier()
    {
        return builderInGameModifier().gui();
    }
}
