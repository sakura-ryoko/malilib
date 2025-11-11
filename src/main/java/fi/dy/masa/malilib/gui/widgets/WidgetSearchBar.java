package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.util.KeyCodes;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;

public class WidgetSearchBar extends WidgetBase
{
    protected final WidgetIcon iconSearch;
    protected final LeftRight iconAlignment;
    protected final GuiTextFieldGeneric searchBox;
    protected boolean searchOpen;

    public WidgetSearchBar(int x, int y, int width, int height,
            int searchBarOffsetX, IGuiIcon iconSearch, LeftRight iconAlignment)
    {
        super(x, y, width, height);

        int iw = iconSearch.getWidth();
        int ix = iconAlignment == LeftRight.RIGHT ? x + width - iw - 1 : x + 2;
        int tx = iconAlignment == LeftRight.RIGHT ? x - searchBarOffsetX + 1 : x + iw + 6 + searchBarOffsetX;
        this.iconSearch = new WidgetIcon(ix, y + 1, iconSearch);
        this.iconAlignment = iconAlignment;
        this.searchBox = new GuiTextFieldGeneric(tx, y, width - iw - 7 - Math.abs(searchBarOffsetX), height, this.textRenderer);
        this.searchBox.setZLevel(this.zLevel);
    }

    public String getFilter()
    {
        return this.searchOpen ? this.searchBox.getText().toLowerCase() : "";
    }

    public boolean hasFilter()
    {
        return this.searchOpen && this.searchBox.getText().isEmpty() == false;
    }

    public boolean isSearchOpen()
    {
        return this.searchOpen;
    }

    public void setSearchOpen(boolean isOpen)
    {
        this.searchOpen = isOpen;

        if (this.searchOpen)
        {
            this.searchBox.setFocused(true);
        }
    }

    @Override
    protected boolean onMouseClickedImpl(Click click, boolean doubleClick)
    {
        if (this.searchOpen && this.searchBox.mouseClicked(click, doubleClick))
        {
            return true;
        }
        else if (this.iconSearch.isMouseOver((int) click.x(), (int) click.y()))
        {
            this.setSearchOpen(! this.searchOpen);
			this.searchBox.onClick(click, false);
            return true;
        }

        return false;
    }

    @Override
    protected boolean onKeyTypedImpl(KeyInput input)
    {
        if (this.searchOpen)
        {
            if (this.searchBox.keyPressed(input))
            {
                return true;
            }
            else if (input.key() == KeyCodes.KEY_ESCAPE)
            {
                if (input.hasShift())
                {
                    this.mc.currentScreen.close();
                }

                this.searchOpen = false;
                this.searchBox.setFocused(false);
                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean onCharTypedImpl(CharInput input)
    {
        if (this.searchOpen)
        {
            if (this.searchBox.charTyped(input))
            {
                return true;
            }
        }
        else if (input.isValidChar())
        {
            this.searchOpen = true;
            this.searchBox.setFocused(true);
            this.searchBox.setText("");
            this.searchBox.charTyped(input);

            return true;
        }

        return false;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.render(drawContext, mouseX, mouseY, selected);
        this.iconSearch.render(drawContext, false, this.iconSearch.isMouseOver(mouseX, mouseY));

        if (this.searchOpen)
        {
            this.searchBox.render(drawContext, mouseX, mouseY, 0);
        }
    }
}
