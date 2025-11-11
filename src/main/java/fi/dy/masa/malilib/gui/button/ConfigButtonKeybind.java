package fi.dy.masa.malilib.gui.button;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Click;
import org.apache.commons.lang3.StringUtils;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindCategory;
import fi.dy.masa.malilib.util.KeyCodes;

public class ConfigButtonKeybind extends ButtonGeneric
{
    @Nullable protected final IKeybindConfigGui host;
    protected final IKeybind keybind;
    protected final List<String> overlapInfo = new ArrayList<>();
    protected boolean selected;
    protected boolean firstKey;

    public ConfigButtonKeybind(int x, int y, int width, int height, IKeybind keybind, @Nullable IKeybindConfigGui host)
    {
        super(x, y, width, height, "");

        this.host = host;
        this.keybind = keybind;

        this.updateDisplayString();
        this.setHoverInfoRequiresShift(true);
    }

    @Override
    protected boolean onMouseClickedImpl(Click click, boolean doubleClick)
    {
        super.onMouseClickedImpl(click, doubleClick);

        if (this.selected)
        {
            this.addKey(click.getKeycode() - 100);
            this.updateDisplayString();
        }
        else if (click.getKeycode() == 0)
        {
            this.selected = true;

            if (this.host != null)
            {
                this.host.setActiveKeybindButton(this);
            }
        }

        return true;
    }

    public void onKeyPressed(int keyCode)
    {
        if (this.selected)
        {
            if (keyCode == KeyCodes.KEY_ESCAPE)
            {
                if (this.firstKey)
                {
                    this.keybind.clearKeys();
                }

                if (this.host != null)
                {
                    this.host.setActiveKeybindButton(null);
                }
            }
            else
            {
                this.addKey(keyCode);
            }

            this.updateDisplayString();
        }
    }

    private void addKey(int keyCode)
    {
        if (this.firstKey)
        {
            this.keybind.clearKeys();
            this.firstKey = false;
        }

        this.keybind.addKey(keyCode);
    }

    public void onSelected()
    {
        this.selected = true;
        this.firstKey = true;
        this.updateDisplayString();
    }

    public void onClearSelection()
    {
        this.selected = false;
        this.updateDisplayString();
    }

    public boolean isSelected()
    {
        return this.selected;
    }

    public void updateDisplayString()
    {
        String valueStr = this.keybind.getKeysDisplayString();

        if (this.keybind.getKeys().size() == 0 || StringUtils.isBlank(valueStr))
        {
            valueStr = "NONE";
        }

        this.clearHoverStrings();

        if (this.selected)
        {
            this.displayString = "> " + GuiBase.TXT_YELLOW + valueStr + GuiBase.TXT_RST + " <";
        }
        else
        {
            this.updateConflicts();

            if (this.overlapInfo.size() > 0)
            {
                this.displayString = GuiBase.TXT_GOLD + valueStr + GuiBase.TXT_RST;
            }
            else
            {
                this.displayString = valueStr;
            }
        }
    }

    protected void updateConflicts()
    {
        List<KeybindCategory> categories = InputEventHandler.getKeybindManager().getKeybindCategories();
        List<IHotkey> overlaps = new ArrayList<>();
        this.overlapInfo.clear();

        for (KeybindCategory category : categories)
        {
            List<? extends IHotkey> hotkeys = category.getHotkeys();

            for (IHotkey hotkey : hotkeys)
            {
                if (this.keybind.overlaps(hotkey.getKeybind()))
                {
                    overlaps.add(hotkey);
                }
            }

            if (overlaps.size() > 0)
            {
                if (this.overlapInfo.size() > 0)
                {
                    this.overlapInfo.add("-----");
                }

                this.overlapInfo.add(category.getModName());
                this.overlapInfo.add(" > " + category.getCategory());

                for (IHotkey overlap : overlaps)
                {
                    String key = " [ " + GuiBase.TXT_GOLD + overlap.getKeybind().getKeysDisplayString() + GuiBase.TXT_RST + " ]";
                    this.overlapInfo.add("    - " + overlap.getName() + key);
                }

                overlaps.clear();
            }
        }

        if (this.overlapInfo.size() > 0)
        {
            this.setHoverStrings(this.overlapInfo);
        }
    }
}
