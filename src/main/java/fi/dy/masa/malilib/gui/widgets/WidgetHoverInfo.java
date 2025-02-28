package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.DrawContext;

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
     * @param lines
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
    public void render(int mouseX, int mouseY, boolean selected, DrawContext drawContext)
    {
        super.render(mouseX, mouseY, selected, drawContext);
    }

    @Override
    public void postRenderHovered(int mouseX, int mouseY, boolean selected, DrawContext drawContext)
    {
        super.postRenderHovered(mouseX, mouseY, selected, drawContext);
        RenderUtils.drawHoverText(mouseX, mouseY, this.lines, drawContext);
    }
}
