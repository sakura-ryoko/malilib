package fi.dy.masa.malilib.gui.widgets;

import javax.annotation.Nullable;

import net.minecraft.client.gui.DrawContext;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;

public abstract class WidgetListEntrySortable<TYPE> extends WidgetListEntryBase<TYPE>
{
    protected int columnCount = 2;

    public WidgetListEntrySortable(int x, int y, int width, int height, @Nullable TYPE entry, int listIndex)
    {
        super(x, y, width, height, entry, listIndex);
    }

    protected abstract int getColumnPosX(int column);

    protected abstract int getCurrentSortColumn();

    protected abstract boolean getSortInReverse();

    protected int getColumnCount()
    {
        return this.columnCount;
    }

    protected int getMouseOverColumn(int mouseX, int mouseY)
    {
        int numColumns = this.getColumnCount();
        int x1 = this.getColumnPosX(0);
        int xEnd = this.getColumnPosX(numColumns);

        if (mouseY >= this.y && mouseY <= this.y + this.height && mouseX >= x1 && mouseX < xEnd)
        {
            for (int column = 1; column <= numColumns; ++column)
            {
                if (mouseX < this.getColumnPosX(column))
                {
                    return column - 1;
                }
            }
        }

        return -1;
    }

    protected void renderColumnHeader(int mouseX, int mouseY, IGuiIcon iconNatural, IGuiIcon iconReverse, DrawContext drawContext)
    {
        int mouseOverColumn = this.getMouseOverColumn(mouseX, mouseY);
        int sortColumn = this.getCurrentSortColumn();
        boolean reverse = this.getSortInReverse();
        int iconX = this.getColumnPosX(sortColumn + 1) - 21; // align to the right edge

        RenderUtils.color(1f, 1f, 1f, 1f);
        IGuiIcon icon = reverse ? iconReverse : iconNatural;
        this.bindTexture(icon.getTexture());
        icon.renderAt(iconX, this.y + 3, this.zLevel, true, sortColumn == mouseOverColumn, drawContext);

        for (int i = 0; i < this.getColumnCount(); ++i)
        {
            int outlineColor = mouseOverColumn == i ? 0xFFFFFFFF : 0xC0707070;
            int xStart = this.getColumnPosX(i);
            int xEnd = this.getColumnPosX(i + 1);

            RenderUtils.drawOutline(xStart - 3, this.y + 1, xEnd - xStart - 2, this.height - 2, outlineColor);
        }
    }
}
