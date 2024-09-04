package fi.dy.masa.malilib.gui;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import fi.dy.masa.malilib.config.options.ConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.gui.button.ConfigButtonOptionList;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.gui.widgets.WidgetHoverInfo;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.KeyCodes;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiKeybindSettings extends GuiDialogBase
{
    protected final IKeybind keybind;
    protected final String keybindName;
    protected final ConfigOptionList cfgActivateOn  = new ConfigOptionList("malilib.gui.label.keybind_settings.activate_on", KeyAction.PRESS, "malilib.config.comment.keybind_settings.activate_on");
    protected final ConfigOptionList cfgContext     = new ConfigOptionList("malilib.gui.label.keybind_settings.context", KeybindSettings.Context.INGAME, "malilib.config.comment.keybind_settings.context");
    protected final ConfigBoolean cfgAllowEmpty     = new ConfigBoolean("malilib.gui.label.keybind_settings.allow_empty_keybind", false, "malilib.config.comment.keybind_settings.allow_empty_keybind");
    protected final ConfigBoolean cfgAllowExtra     = new ConfigBoolean("malilib.gui.label.keybind_settings.allow_extra_keys", false, "malilib.config.comment.keybind_settings.allow_extra_keys");
    protected final ConfigBoolean cfgOrderSensitive = new ConfigBoolean("malilib.gui.label.keybind_settings.order_sensitive", false, "malilib.config.comment.keybind_settings.order_sensitive");
    protected final ConfigBoolean cfgExclusive      = new ConfigBoolean("malilib.gui.label.keybind_settings.exclusive", false, "malilib.config.comment.keybind_settings.exclusive");
    protected final ConfigBoolean cfgCancel         = new ConfigBoolean("malilib.gui.label.keybind_settings.cancel_further", false, "malilib.config.comment.keybind_settings.cancel_further");
    protected final List<ConfigBase<?>> configList;
    @Nullable protected final IDialogHandler dialogHandler;
    protected int labelWidth;
    protected int configWidth;

    public GuiKeybindSettings(IKeybind keybind, String name, @Nullable IDialogHandler dialogHandler, Screen parent)
    {
        this.keybind = keybind;
        this.keybindName = name;
        this.dialogHandler = dialogHandler;

        // When we have a dialog handler, then we are inside the Liteloader config menu.
        // In there we don't want to use the normal "GUI replacement and render parent first" trick.
        // The "dialog handler" stuff is used within the Liteloader config menus,
        // because there we can't change the mc.currentScreen reference to this GUI,
        // because otherwise Liteloader will freak out.
        // So instead we are using a weird wrapper "sub panel" thingy in there, and thus
        // we can NOT try to render the parent GUI here in that case, otherwise it will
        // lead to an infinite recursion loop and a StackOverflowError.
        if (this.dialogHandler == null)
        {
            this.setParent(parent);
        }

        this.title = GuiBase.TXT_BOLD + StringUtils.translate("malilib.gui.title.keybind_settings.advanced", this.keybindName) + GuiBase.TXT_RST;
        KeybindSettings settings = this.keybind.getSettings();

        this.cfgActivateOn.setOptionListValue(settings.getActivateOn());
        this.cfgContext.setOptionListValue(settings.getContext());
        this.cfgAllowEmpty.setBooleanValue(settings.getAllowEmpty());
        this.cfgAllowExtra.setBooleanValue(settings.getAllowExtraKeys());
        this.cfgOrderSensitive.setBooleanValue(settings.isOrderSensitive());
        this.cfgExclusive.setBooleanValue(settings.isExclusive());
        this.cfgCancel.setBooleanValue(settings.shouldCancel());

        this.configList = ImmutableList.of(this.cfgActivateOn, this.cfgContext, this.cfgAllowEmpty, this.cfgAllowExtra, this.cfgOrderSensitive, this.cfgExclusive, this.cfgCancel);
        this.labelWidth = this.getMaxPrettyNameLength(this.configList);
        this.configWidth = 100;

        int totalWidth = this.labelWidth + this.configWidth + 30;
        totalWidth = Math.max(totalWidth, this.getStringWidth(this.title) + 20);

        this.setWidthAndHeight(totalWidth, this.configList.size() * 22 + 30);
        this.centerOnScreen();

        this.init(this.mc, this.dialogWidth, this.dialogHeight);
    }

    @Override
    public void initGui()
    {
        this.clearElements();

        int x = this.dialogLeft + 10;
        int y = this.dialogTop + 24;

        for (ConfigBase<?> config : this.configList)
        {
            this.addConfig(x, y, this.labelWidth, this.configWidth, config);
            y += 22;
        }
    }

    protected void addConfig(int x, int y, int labelWidth, int configWidth, ConfigBase<?> config)
    {
        this.addLabel(x, y + 4, labelWidth, 10, 0xFFFFFFFF, StringUtils.translate(config.getConfigGuiDisplayName()));
        this.addWidget(new WidgetHoverInfo(x, y + 2, labelWidth, 12, config.getComment()));
        x += labelWidth + 10;

        if (config instanceof ConfigBoolean)
        {
            this.addWidget(new ConfigButtonBoolean(x, y, configWidth, 20, (ConfigBoolean) config));
        }
        else if (config instanceof ConfigOptionList)
        {
            this.addWidget(new ConfigButtonOptionList(x, y, configWidth, 20, (ConfigOptionList) config));
        }
    }

    @Override
    public void removed()
    {
        KeyAction activateOn = (KeyAction) this.cfgActivateOn.getOptionListValue();
        KeybindSettings.Context context = (KeybindSettings.Context) this.cfgContext.getOptionListValue();
        boolean allowEmpty = this.cfgAllowEmpty.getBooleanValue();
        boolean allowExtraKeys = this.cfgAllowExtra.getBooleanValue();
        boolean orderSensitive = this.cfgOrderSensitive.getBooleanValue();
        boolean exclusive = this.cfgExclusive.getBooleanValue();
        boolean cancel = this.cfgCancel.getBooleanValue();

        KeybindSettings settingsNew = KeybindSettings.create(context, activateOn, allowExtraKeys, orderSensitive, exclusive, cancel, allowEmpty);
        this.keybind.setSettings(settingsNew);

        super.removed();
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks)
    {
        if (this.getParent() != null)
        {
            this.getParent().render(drawContext, mouseX, mouseY, partialTicks);
        }

        super.render(drawContext, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawScreenBackground(DrawContext drawContext, int mouseX, int mouseY)
    {
        super.drawTexturedBG(drawContext, this.dialogLeft, this.dialogTop, this.dialogWidth, this.dialogHeight, true);
        RenderUtils.drawOutlinedBox(this.dialogLeft, this.dialogTop, this.dialogWidth, this.dialogHeight, 0xFF000000, COLOR_HORIZONTAL_BAR);
    }

    @Override
    protected void drawTitle(DrawContext drawContext, int mouseX, int mouseY, float partialTicks)
    {
        this.drawStringWithShadow(drawContext, this.title, this.dialogLeft + 10, this.dialogTop + 6, COLOR_WHITE);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return this.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == KeyCodes.KEY_ESCAPE && this.dialogHandler != null)
        {
            this.dialogHandler.closeDialog();
            return true;
        }
        else
        {
            return super.onKeyTyped(keyCode, scanCode, modifiers);
        }
    }
}
