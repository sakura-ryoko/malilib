package malilib.gui.widget.list.entry;

import net.minecraft.block.Block;

import malilib.gui.widget.BlockModelWidget;
import malilib.gui.widget.InteractableWidget;
import malilib.render.text.StyledTextLine;
import malilib.util.game.wrap.RegistryUtils;

public class BlockEntryWidget extends BaseDataListEntryWidget<Block>
{
    protected final InteractableWidget blockModelWidget;
    protected boolean useModel = true;

    public BlockEntryWidget(Block data, DataListEntryWidgetData constructData)
    {
        super(data, constructData);

        this.blockModelWidget = new BlockModelWidget(data);
        this.blockModelWidget.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, 0xFF202020);

        this.setText(StyledTextLine.parseFirstLine(RegistryUtils.getBlockIdStr(data)));
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidgetIf(this.blockModelWidget, this.useModel);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        if (this.useModel)
        {
            this.getTextOffset().setXOffset(22);
            this.blockModelWidget.setX(this.getX() + 2);
            this.blockModelWidget.centerVerticallyInside(this);
        }
        else
        {
            this.getTextOffset().setXOffset(2);
        }
    }

    public void setUseModel(boolean useModel)
    {
        this.useModel = useModel;
    }
}
