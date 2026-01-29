package fi.dy.masa.malilib.test.gui;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.test.gui.widgets.WidgetTestBlockStateList;
import fi.dy.masa.malilib.test.gui.widgets.WidgetTestBlockStateListEntry;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class GuiTestBlockStateList extends GuiListBase<GuiTestBlockStateList.Entry, WidgetTestBlockStateListEntry, WidgetTestBlockStateList>
        implements ISelectionListener<GuiTestBlockStateList.Entry>
{
    private final List<Block> blocks;
    public final int iconSize = 64;

    public GuiTestBlockStateList()
    {
        super(10, 60);
	    this.title = StringUtils.translate("malilib.gui.title.test_widget_list");
        this.blocks = new ArrayList<>();
        this.buildBlockList();
    }

    @Override
    protected int getBrowserWidth()
    {
        return this.getScreenWidth() - 20;
    }

    @Override
    protected int getBrowserHeight()
    {
        return this.getScreenHeight() - 94;
    }

    @Override
    protected WidgetTestBlockStateList createListWidget(int listX, int listY)
    {
        return new WidgetTestBlockStateList(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), this.iconSize, this);
    }

    @Override
    protected ISelectionListener<Entry> getSelectionListener()
    {
        return this;
    }

    @Override
    public void onSelectionChange(@Nullable GuiTestBlockStateList.Entry entry)
    {
        if (entry != null)
        {
            MaLiLib.LOGGER.warn("GuiTestBlockStateList#onSelectionChange(): name: [{}], state: [{}]", entry.name(), entry.state().toString());
        }
    }

    private void buildBlockList()
    {
        this.blocks.clear();

        this.blocks.add(Blocks.STONE);
        this.blocks.add(Blocks.ACACIA_FENCE);
        this.blocks.add(Blocks.GLOW_LICHEN);
//        this.blocks.add(Blocks.SANDSTONE_SLAB);
//        this.blocks.add(Blocks.FIRE);
        this.blocks.add(Blocks.GLASS);
    }

    public List<Block> getBlocks()
    {
        return this.blocks;
    }

    public record Entry(String name, BlockState state)
    {
        public Block getBlock()
        {
            return this.state().getBlock();
        }
    }
}
