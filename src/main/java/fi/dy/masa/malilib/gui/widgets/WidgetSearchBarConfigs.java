package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.hotkeys.KeybindSettings.Context;
import fi.dy.masa.malilib.util.KeyCodes;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyInput;

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
    protected boolean onMouseClickedImpl(Click click, boolean doubleClick)
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
    protected boolean onKeyTypedImpl(KeyInput input)
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

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.render(drawContext, mouseX, mouseY, selected);

        if (this.searchOpen)
        {
            this.button.render(drawContext, mouseX, mouseY, false);
        }
    }
}
