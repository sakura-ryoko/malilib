package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.gui.DrawContext;

public class WidgetStringListEntry extends WidgetListEntryBase<String>
{
    private final boolean isOdd;

    public WidgetStringListEntry(int x, int y, int width, int height, boolean isOdd, String entry, int listIndex)
    {
        super(x, y, width, height, entry, listIndex);

        this.isOdd = isOdd;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, boolean selected)
    {
        super.render(drawContext, mouseX, mouseY, selected);
//        RenderUtils.color(1f, 1f, 1f, 1f);

        // Draw a lighter background for the hovered and the selected entry
        if (selected || this.isMouseOver(mouseX, mouseY))
        {
            RenderUtils.drawRect(drawContext, this.x, this.y, this.width, this.height, 0xA0707070);
        }
        else if (this.isOdd)
        {
            RenderUtils.drawRect(drawContext, this.x, this.y, this.width, this.height, 0xA0101010);
        }
        // Draw a slightly lighter background for even entries
        else
        {
            RenderUtils.drawRect(drawContext, this.x, this.y, this.width, this.height, 0xA0303030);
        }

        if (selected)
        {
            RenderUtils.drawOutline(drawContext, this.x, this.y, this.width, this.height, 0xFF90D0F0);
        }

        int yOffset = (this.height - this.fontHeight) / 2 + 1;
        this.drawStringWithShadow(drawContext, this.x + 2, this.y + yOffset, 0xFFFFFFFF, this.entry);

        super.render(drawContext, mouseX, mouseY, selected);
    }
}
