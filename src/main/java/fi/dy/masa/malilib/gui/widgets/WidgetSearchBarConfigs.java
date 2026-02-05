package fi.dy.masa.malilib.gui.widgets;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.hotkeys.KeybindSettings.Context;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.util.KeyCodes;

public class WidgetSearchBarConfigs extends WidgetSearchBar
{
    protected final KeybindMulti searchKey;
    protected final ConfigButtonKeybind button;

    public WidgetSearchBarConfigs(int x, int y, int width, int height, int searchBarOffsetX,
            IGuiIcon iconSearch, LeftRight iconAlignment)
    {
        super(x, y + 3, width - 160, 14, searchBarOffsetX, iconSearch, iconAlignment);

        KeybindSettings settings = KeybindSettings.create(Context.ANY, KeyAction.BOTH, true, true, false, false, false);
        this.searchKey = KeybindMulti.fromStorageString("", settings);
        this.button = new ConfigButtonKeybind(x + width - 150, y, 140, 20, this.searchKey, null);
    }

    public IKeybind getKeybind()
    {
        return this.searchKey;
    }

    @Override
    public boolean hasFilter()
    {
        return super.hasFilter() || (this.searchOpen && this.searchKey.getKeys().size() > 0);
    }

    @Override
    protected boolean onMouseClickedImpl(MouseButtonEvent click, boolean doubleClick)
    {
        if (this.searchOpen)
        {
            if (this.button.isMouseOver((int) click.x(), (int) click.y()))
            {
                boolean selectedPre = this.button.isSelected();
                this.button.onMouseClicked(click, doubleClick);

                if (selectedPre == false)
                {
                    this.button.onSelected();
                }

                return true;
            }
            else if (this.button.isSelected())
            {
                this.button.onClearSelection();
                return true;
            }
        }

        return super.onMouseClickedImpl(click, doubleClick);
    }

    @Override
    protected boolean onKeyTypedImpl(KeyEvent input)
    {
        if (this.searchOpen && this.button.isSelected())
        {
            this.button.onKeyPressed(input.key());

            if (input.key() == KeyCodes.KEY_ESCAPE)
            {
                this.button.onClearSelection();
            }

            return true;
        }

        return super.onKeyTypedImpl(input);
    }

    // This stops you from typing in the Search box
    // while also setting a Search Keybind at the same time ...
    @Override
    protected boolean onCharTypedImpl(CharacterEvent input)
    {
        if (this.searchOpen && this.button.isSelected())
        {
            return true;
        }

        return super.onCharTypedImpl(input);
    }

    @Override
    public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.render(ctx, mouseX, mouseY, selected);

        if (this.searchOpen)
        {
            this.button.render(ctx, mouseX, mouseY, false);

            if (!this.hasFilter())
            {
                this.searchBox.setHoverTooltip("malilib.gui.button.hover.search_bar_hotkey");
            }
            else
            {
                this.searchBox.clearHoverTooltip();
            }
        }
    }
}
