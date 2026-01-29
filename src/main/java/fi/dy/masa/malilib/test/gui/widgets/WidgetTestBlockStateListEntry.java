package fi.dy.masa.malilib.test.gui.widgets;

import java.util.List;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntrySortable;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.test.gui.GuiTestBlockStateList;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetTestBlockStateListEntry extends WidgetListEntrySortable<GuiTestBlockStateList.Entry>
{
    private final GuiTestBlockStateList parent;
    private final GuiTestBlockStateList.Entry entry;
    private final boolean isOdd;

    private static int maxNameLengthExpected;
    private static int maxNameLengthFound;
    private static int maxCountLength;

    public WidgetTestBlockStateListEntry(int x, int y, int width, int height, boolean isOdd, @Nullable GuiTestBlockStateList.Entry entry, GuiTestBlockStateList parent, int listIndex)
    {
        super(x, y, width, height, entry, listIndex);
        this.entry = entry;
        this.isOdd = isOdd;
        this.parent = parent;
    }

    public GuiTestBlockStateList getParent()
    {
        return this.parent;
    }

    public boolean isOdd()
    {
        return this.isOdd;
    }

    @Override
    public @Nullable GuiTestBlockStateList.Entry getEntry()
    {
        return this.entry;
    }

    @Override
    protected int getColumnPosX(int column)
    {
        final int x1 = this.x + 4;
        final int x2 = x1 + maxNameLengthExpected + 56; // including item icon
        final int x3 = x2 + maxNameLengthFound + 56;

        return switch (column)
        {
            case 1 -> x2;
            case 2 -> x3;
            case 3 -> x3 + maxCountLength + 20;
            default -> x1;
        };
    }

    @Override
    protected int getCurrentSortColumn()
    {
        return 0;
    }

    @Override
    protected boolean getSortInReverse()
    {
        return false;
    }

    public static void setMaxEntryLength(List<GuiTestBlockStateList.Entry> entryList)
    {
        maxNameLengthExpected = 9;
        maxNameLengthFound = 9;
        maxCountLength = 3 * maxNameLengthExpected;

        for (GuiTestBlockStateList.Entry entry : entryList)
        {
            Block block = entry.getBlock();
            String name = block.getName().getString();

            maxNameLengthExpected = Math.max(maxNameLengthExpected, StringUtils.getStringWidth(name));
            maxNameLengthFound    = Math.max(maxNameLengthFound,    StringUtils.getStringWidth(name));
        }

        maxCountLength = Math.max(maxCountLength, 3 * maxNameLengthFound);
    }

    @Override
    public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        // Default color for even entries
        int color = 0xA0303030;

        // Draw a lighter background for the hovered and the selected entry
        if (selected)
        {
            color = 0xA0707070;
        }
        else if (this.isMouseOver(mouseX, mouseY))
        {
            color = 0xA0505050;
        }
        // Draw a slightly darker background for odd entries
        else if (this.isOdd)
        {
            color = 0xA0101010;
        }

        // Background
        RenderUtils.drawRect(ctx, this.x, this.y, this.width, this.height, color);

        // Selected Indicator
        if (selected)
        {
            RenderUtils.drawOutline(ctx, this.x, this.y, this.width, this.height, 0xFFE0E0E0);
        }

        int x1 = this.getColumnPosX(0);
        int x2 = this.getColumnPosX(1);
        int x3 = this.getColumnPosX(2);
        final int iconSize = this.parent.iconSize;
        int y = this.y;
        color = 0xFFFFFFFF;

        if (this.entry != null)
        {
            this.drawString(ctx, x1 + iconSize + 4, (y + (iconSize / 2)), color, this.entry.name());
//            this.drawString(drawContext, x2 + 24, y, color, this.entry.state().toString());

            y += 3;
            RenderUtils.drawRect(ctx, x1, y, iconSize, iconSize, 0x20FFFFFF); // light background for the item
            // scale: 0.625f ?
            RenderUtils.renderModelInGui(ctx, x1, y, iconSize, this.entry.state(), 0.75f, 0.0F);
            y += 3;
        }

        super.render(ctx, mouseX, mouseY, selected);
    }
}
