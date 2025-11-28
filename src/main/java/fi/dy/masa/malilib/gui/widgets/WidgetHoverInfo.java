package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetHoverInfo extends WidgetBase
{
    protected final List<String> lines = new ArrayList<>();

    public WidgetHoverInfo(int x, int y, int width, int height, String key, Object... args)
    {
        super(x, y, width, height);

        this.setInfoLines(key, args);
    }

    protected void setInfoLines(String key, Object... args)
    {
        String[] split = StringUtils.translate(key, args).split("\\n");

        for (String str : split)
        {
            this.lines.add(str);
        }
    }

    /**
     * Adds the provided lines to the list.
     * The strings will be split into separate lines from any "\n" sequences.
     * @param lines ()
     */
    public void addLines(String... lines)
    {
        for (String line : lines)
        {
            line = StringUtils.translate(line);
            String[] split = line.split("\\n");

            for (String str : split)
            {
                this.lines.add(str);
            }
        }
    }

    public List<String> getLines()
    {
        return this.lines;
    }

    @Override
    public void render(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.render(ctx, mouseX, mouseY, selected);
    }

    @Override
    public void postRenderHovered(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.postRenderHovered(ctx, mouseX, mouseY, selected);
        RenderUtils.drawHoverText(ctx, mouseX, mouseY, this.lines);
    }
}
