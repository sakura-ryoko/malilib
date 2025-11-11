package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiKeybindSettings;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetKeybindSettings extends WidgetBase
{
    public static final Identifier TEXTURE = Identifier.of(MaLiLibReference.MOD_ID, "textures/gui/gui_widgets.png");

    protected final String keybindName;
    protected final IKeybind keybind;
    protected final KeybindSettings settings;
    protected final WidgetListBase<?, ?> widgetList;
    @Nullable protected final IDialogHandler dialogHandler;

    public WidgetKeybindSettings(int x, int y, int width, int height,
            IKeybind keybind, String keybindName, WidgetListBase<?, ?> widgetList, @Nullable IDialogHandler dialogHandler)
    {
        super(x, y, width, height);

        this.keybind = keybind;
        this.keybindName = keybindName;
        this.settings = keybind.getSettings();
        this.widgetList = widgetList;
        this.dialogHandler = dialogHandler;
    }

    @Override
    protected boolean onMouseClickedImpl(Click click, boolean doubleClick)
    {
        if (click.getKeycode() == 0)
        {
            if (this.dialogHandler != null)
            {
                this.dialogHandler.openDialog(new GuiKeybindSettings(this.keybind, this.keybindName, this.dialogHandler, GuiUtils.getCurrentScreen()));
            }
            else
            {
                GuiBase.openGui(new GuiKeybindSettings(this.keybind, this.keybindName, null, GuiUtils.getCurrentScreen()));
            }

            return true;
        }
        // Reset the settings to defaults on right click
        else if (click.getKeycode() == 1)
        {
            this.keybind.resetSettingsToDefaults();
            this.widgetList.refreshEntries();
            return true;
        }

        return false;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.render(drawContext, mouseX, mouseY, selected);

        int w = 18;
        int v1 = this.settings.getActivateOn().ordinal() * w;
        int v2 = this.settings.getAllowExtraKeys() == false ? w : 0;
        int v3 = this.settings.isOrderSensitive() ? w : 0;
        int v4 = this.settings.isExclusive() ? w : 0;
        int v5 = this.settings.shouldCancel() ? w : 0;

        int x = this.x;
        int y = this.y;

        int edgeColor = this.keybind.areSettingsModified() ? 0xFFFFBB33 : 0xFFFFFFFF;
        RenderUtils.drawRect(drawContext, x, y, 20, 20, edgeColor);
        RenderUtils.drawRect(drawContext, x + 1, y + 1, 18, 18, 0xFF000000);

        x += 1;
        y += 1;
        float z = 0;

        RenderUtils.drawTexturedRect(drawContext, TEXTURE, x, y,  0,  v1, w, w, z);
        RenderUtils.drawTexturedRect(drawContext, TEXTURE, x, y,  18, v2, w, w, z);
        RenderUtils.drawTexturedRect(drawContext, TEXTURE, x, y,  36, v3, w, w, z);
        RenderUtils.drawTexturedRect(drawContext, TEXTURE, x, y,  54, v4, w, w, z);
        RenderUtils.drawTexturedRect(drawContext, TEXTURE, x, y,  72, v5, w, w, z);
    }

    @Override
    public void postRenderHovered(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.postRenderHovered(drawContext, mouseX, mouseY, selected);
        List<String> text = new ArrayList<>();
        String name, val;
        String strYes = StringUtils.translate("malilib.gui.label.yes");
        String strNo = StringUtils.translate("malilib.gui.label.no");

        text.add(GuiBase.TXT_WHITE + GuiBase.TXT_UNDERLINE + StringUtils.translate("malilib.gui.label.keybind_settings.title_advanced_keybind_settings"));

        name = StringUtils.translate("malilib.gui.label.keybind_settings.activate_on");
        val = GuiBase.TXT_BLUE + this.settings.getActivateOn().name();
        text.add(String.format("%s: %s", name, val));

        name = StringUtils.translate("malilib.gui.label.keybind_settings.allow_empty_keybind");
        val = this.settings.getAllowEmpty() ? (GuiBase.TXT_GREEN + strYes) : (GuiBase.TXT_GOLD + strNo);
        text.add(String.format("%s: %s", name, val));

        name = StringUtils.translate("malilib.gui.label.keybind_settings.allow_extra_keys");
        val = this.settings.getAllowExtraKeys() ? (GuiBase.TXT_GREEN + strYes) : (GuiBase.TXT_GOLD + strNo);
        text.add(String.format("%s: %s", name, val));

        name = StringUtils.translate("malilib.gui.label.keybind_settings.order_sensitive");
        val = this.settings.isOrderSensitive() ? (GuiBase.TXT_GOLD + strYes) : (GuiBase.TXT_GREEN + strNo);
        text.add(String.format("%s: %s", name, val));

        name = StringUtils.translate("malilib.gui.label.keybind_settings.cancel_further");
        val = this.settings.shouldCancel() ? (GuiBase.TXT_GOLD + strYes) : (GuiBase.TXT_GREEN + strNo);
        text.add(String.format("%s: %s", name, val));

        name = StringUtils.translate("malilib.gui.label.keybind_settings.exclusive");
        val = this.settings.isExclusive() ? (GuiBase.TXT_GOLD + strYes) : (GuiBase.TXT_GREEN + strNo);
        text.add(String.format("%s: %s", name, val));

        name = StringUtils.translate("malilib.gui.label.keybind_settings.context");
        val = GuiBase.TXT_BLUE + this.settings.getContext().name();
        text.add(String.format("%s: %s", name, val));

        text.add("");
        String[] parts = StringUtils.translate("malilib.gui.label.keybind_settings.tips").split("\\n");

        text.addAll(Arrays.asList(parts));

        RenderUtils.drawHoverText(drawContext, mouseX + 10, mouseY, text);
    }
}
