package fi.dy.masa.malilib.test.gui.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.Block;

import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.test.gui.GuiTestBlockStateList;
import fi.dy.masa.malilib.util.MathUtils;

public class WidgetTestBlockStateList extends WidgetListBase<GuiTestBlockStateList.Entry, WidgetTestBlockStateListEntry>
{
    private final GuiTestBlockStateList parent;
    private final List<GuiTestBlockStateList.Entry> entries;


    public WidgetTestBlockStateList(int x, int y, int width, int height, int iconSize,
                                    @Nullable GuiTestBlockStateList parent)
    {
        super(x, y, width, height, parent);
        this.parent = parent;
        this.entries = new ArrayList<>();
        this.browserEntryHeight = iconSize + 6;
        this.buildAllEntries();
    }

    private void buildAllEntries()
    {
        this.entries.clear();

        for (Block block : this.parent.getBlocks())
        {
            this.entries.add(new GuiTestBlockStateList.Entry(block.getName().getString(), block.defaultBlockState()));
        }
    }

    @Override
    protected WidgetTestBlockStateListEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd,
                                                                  GuiTestBlockStateList.Entry entry)
    {
        return new WidgetTestBlockStateListEntry(x, y,
                                                 this.browserEntryWidth,
                                                 MathUtils.max(this.getBrowserEntryHeightFor(entry), this.parent.iconSize + 6),
                                                 isOdd, entry, this.parent, listIndex);
    }

    @Override
    protected Collection<GuiTestBlockStateList.Entry> getAllEntries()
    {
        return this.entries;
    }
}
