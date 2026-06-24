package fi.dy.masa.malilib.test.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.jetbrains.annotations.Nullable;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.test.gui.GuiTestList;

public class WidgetTestList extends WidgetListBase<GuiTestList.Entry, WidgetTestListEntry>
{
    private final GuiTestList parent;

    public WidgetTestList(int x, int y, int width, int height,
                          @Nullable GuiTestList parent)
    {
        super(x, y, width, height, parent);
        this.parent = parent;
    }

    @Override
    protected WidgetTestListEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd,
                                                        GuiTestList.Entry entry)
    {
        return new WidgetTestListEntry(x, y, this.browserEntryWidth, Math.max(this.getBrowserEntryHeightFor(entry), this.parent.iconSize + 4),
                                       isOdd, entry, this.parent, listIndex);
    }

    private List<Block> createBlockEntries()
    {
        List<Block> blocks = new ArrayList<>();

        blocks.add(Blocks.GLASS);
        blocks.add(Blocks.STONE);
        blocks.add(Blocks.ACACIA_FENCE);
        blocks.add(Blocks.GLOW_LICHEN);
        blocks.add(Blocks.SANDSTONE_SLAB);

        return blocks;
    }

    @Override
    protected void refreshBrowserEntries()
    {
        List<Block> blocks = new ArrayList<>(this.createBlockEntries());
        this.listContents.clear();

        for (Block block : blocks)
        {
            this.listContents.add(new GuiTestList.Entry(block.getName().getString(), block.getDefaultState()));
        }

        this.reCreateListEntryWidgets();
    }
}
