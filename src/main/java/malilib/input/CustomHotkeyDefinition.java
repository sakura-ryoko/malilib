package malilib.input;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import malilib.MaLiLibReference;
import malilib.action.ActionContext;
import malilib.action.NamedAction;
import malilib.action.util.ActionUtils;
import malilib.config.option.CommonDescription;
import malilib.config.option.ConfigInfo;
import malilib.input.callback.HotkeyCallback;
import malilib.render.text.StyledTextLine;
import malilib.util.StringUtils;
import malilib.util.data.json.JsonUtils;

public class CustomHotkeyDefinition extends CommonDescription implements Hotkey, ConfigInfo
{
    protected final String name;
    protected final KeyBind keyBind;
    protected final List<String> lockOverrideMessages = new ArrayList<>(0);
    @Nullable protected String lockMessage;
    protected ImmutableList<NamedAction> actions;
    protected boolean enabled = true;
    protected boolean locked;

    public CustomHotkeyDefinition(String name, KeyBind keyBind, ImmutableList<NamedAction> actions)
    {
        super(name, MaLiLibReference.MOD_INFO);

        this.name = name;
        this.keyBind = keyBind;
        this.actions = actions;

        this.keyBind.setModInfo(this.getModInfo());
        this.keyBind.setCallback(HotkeyCallback.of(this::execute));
        this.keyBind.setNameTranslationKey(name);
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getDisplayName()
    {
        return this.name;
    }

    @Override
    public boolean isModified()
    {
        return this.keyBind.isModified();
    }

    @Override
    public void resetToDefault()
    {
        this.keyBind.resetToDefault();
    }

    @Override
    public KeyBind getKeyBind()
    {
        return this.keyBind;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    @Override
    public boolean isLocked()
    {
        return this.locked;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void toggleEnabled()
    {
        this.enabled = ! this.enabled;
    }

    @Override
    public void setLocked(boolean isLocked)
    {
        this.locked = isLocked;
        this.rebuildLockOverrideMessages();
    }

    @Override
    public void setLockMessage(@Nullable String lockMessage)
    {
        this.lockMessage = lockMessage;
    }

    @Override
    public List<String> getLockAndOverrideMessages()
    {
        return this.lockOverrideMessages;
    }

    protected void rebuildLockOverrideMessages()
    {
        this.lockOverrideMessages.clear();

        if (this.isLocked() && this.lockMessage != null)
        {
            StringUtils.translateAndLineSplit(this.lockOverrideMessages::add, this.lockMessage);
        }
    }

    public ImmutableList<NamedAction> getActionList()
    {
        return this.actions;
    }

    public void setActionList(ImmutableList<NamedAction> actions)
    {
        this.actions = actions;
    }

    public StyledTextLine getActionDisplayName()
    {
        if (this.actions.size() == 1)
        {
            return this.actions.get(0).getColoredWidgetDisplayName();
        }

        String key = "malilib.label.custom_hotkeys.widget.action_display_name.multiple_actions";
        return StyledTextLine.translateFirstLine(key, this.actions.size());
    }

    protected ActionResult execute(ActionContext ctx)
    {
        for (NamedAction action : this.actions)
        {
            action.execute(ctx);
        }

        return ActionResult.SUCCESS;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("name", this.name);
        obj.addProperty("enabled", this.enabled);
        obj.add("hotkey", this.keyBind.getAsJsonElement());
        obj.add("actions", JsonUtils.toArray(this.actions, NamedAction::toJson));

        return obj;
    }

    @Nullable
    public static CustomHotkeyDefinition fromJson(JsonElement el)
    {
        if (el.isJsonObject() == false)
        {
            return null;
        }

        JsonObject obj = el.getAsJsonObject();
        String name = JsonUtils.getStringOrDefault(obj, "name", "?");
        boolean enabled = JsonUtils.getBooleanOrDefault(obj, "enabled", true);
        KeyBind keyBind = KeyBindImpl.fromStorageString("", KeyBindSettings.INGAME_DEFAULT);

        if (JsonUtils.hasObject(obj, "hotkey"))
        {
            keyBind.setValueFromJsonElement(obj.get("hotkey"), name);
        }

        CustomHotkeyDefinition hotkey = new CustomHotkeyDefinition(name, keyBind, ActionUtils.readActionsFromList(obj, "actions"));
        hotkey.enabled = enabled;

        return hotkey;
    }
}
