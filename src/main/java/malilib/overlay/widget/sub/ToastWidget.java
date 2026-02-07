package malilib.overlay.widget.sub;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import malilib.config.value.HorizontalAlignment;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.icon.Icon;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.BaseWidget;
import malilib.render.text.StyledText;
import malilib.render.text.StyledTextLine;
import malilib.render.text.StyledTextUtils;
import malilib.render.text.TextRenderer;
import malilib.util.data.EdgeInt;
import malilib.util.data.MarkerManager;

public class ToastWidget extends BaseWidget
{
    protected final MarkerManager<String> markerManager = new MarkerManager<>(JsonPrimitive::new, JsonElement::getAsString);
    protected final List<StyledText> text = new ArrayList<>();
    protected final HorizontalAlignment horizontalAlignment;
    protected final long fadeInDuration;
    protected final long fadeOutDuration;
    protected Icon backgroundTexture = DefaultIcons.TOAST_BACKGROUND;
    protected long displayTimeMs = -1L;
    protected long fadeInEndTime;
    protected long fadeOutStartTime;
    protected long expireTime;
    protected int messageGap;

    public ToastWidget(int maxWidth, int lineHeight, int messageGap, EdgeInt padding,
                       int fadeInTimeMs, int fadeOutTimeMs,
                       HorizontalAlignment horizontalAlignment)
    {
        super();

        this.horizontalAlignment = horizontalAlignment;
        this.fadeInDuration = (long) fadeInTimeMs * 1000000L;
        this.fadeOutDuration = (long) fadeOutTimeMs * 1000000L;
        this.messageGap = messageGap;
        this.padding.setFrom(padding);
        this.setLineHeight(lineHeight);
        this.setMaxWidth(maxWidth);
    }

    public void onBecomeActive(long currentTime)
    {
        this.fadeInEndTime = currentTime + this.fadeInDuration;
        this.updateExpireTime(currentTime);
    }

    public void setMessageGap(int messageGap)
    {
        this.messageGap = messageGap;
    }

    public boolean hasExpired(long currentTime)
    {
        return currentTime > this.expireTime;
    }

    /**
     * Returns the relative age of this toast, in the range 0.0f - 1.0f, where 1.0f is the expiration age.
     */
    public float getRelativeAge()
    {
        long currentTime = System.nanoTime();

        if (currentTime > this.expireTime)
        {
            return 1.0f;
        }

        long remainingAge = this.expireTime - currentTime;

        return 1.0f - (float) ((double) remainingAge / (double) (this.displayTimeMs * 1000000L));
    }

    public MarkerManager<String> getMarkerManager()
    {
        return this.markerManager;
    }

    /**
     * Adds the given text to the current text.
     * @param text the text to add to the end of the current text
     */
    public void addText(StyledText text)
    {
        this.text.add(this.wrapTextToWidth(text));
        this.updateSize();
    }

    /**
     * Adds the given text to the current text.
     * @param text the text to add to the end of the current text
     * @param displayTimeMs the new lifetime of the toast. Use -1 to not update/refresh the current life time.
     */
    public void addText(StyledText text, int displayTimeMs)
    {
        this.addText(text);
        this.setDisplayTime(displayTimeMs);
    }

    /**
     * Replaces the current text with the given text.
     * @param text the new text to set in the toast
     * @param displayTimeMs the new lifetime of the toast. Use -1 to not update/refresh the current life time.
     */
    public void replaceText(StyledText text, int displayTimeMs)
    {
        this.text.clear();
        this.addText(text, displayTimeMs);
    }

    public void setBackgroundTexture(Icon backgroundTexture)
    {
        this.backgroundTexture = backgroundTexture;
    }

    protected StyledText wrapTextToWidth(StyledText text)
    {
        return StyledTextUtils.wrapStyledTextToMaxWidth(text, this.maxWidth);
    }

    @Override
    public void updateSize()
    {
        int messageCount = this.text.size();
        int width = 0;
        int height = messageCount > 1 ? (messageCount - 1) * this.messageGap : 0;
        int lineHeight = this.getLineHeight();

        for (StyledText text : this.text)
        {
            width = Math.max(width, StyledTextLine.getRenderWidth(text.lines));
            height += text.lines.size() * lineHeight;
        }

        EdgeInt padding = this.padding;
        width += padding.getHorizontalTotal();
        height += padding.getVerticalTotal();

        this.setSizeNoUpdate(width, height);
    }

    protected void setDisplayTime(int displayTimeMs)
    {
        this.displayTimeMs =  displayTimeMs < 0 ? 5000 : displayTimeMs;
        this.updateExpireTime(System.nanoTime());
    }

    protected void updateExpireTime(long currentTime)
    {
        this.expireTime = currentTime + this.displayTimeMs * 1000000L;
        this.fadeOutStartTime = this.expireTime - this.fadeOutDuration;
    }

    public void render(long currentTime, ScreenContext ctx)
    {
        int offsetX = 0;

        if (currentTime < this.fadeInEndTime && this.fadeInDuration > 0)
        {
            double progress = (double) (this.fadeInEndTime - currentTime) / (double) this.fadeInDuration;
            offsetX = (int) (progress * this.getWidth());
        }
        else if (currentTime >= this.fadeOutStartTime && this.fadeOutDuration > 0)
        {
            double progress = (double) (currentTime - this.fadeOutStartTime) / (double) this.fadeOutDuration;
            offsetX = (int) (progress * this.getWidth());
        }

        int x = this.getX();
        int y = this.getY();
        float z = this.getZ();

        if (this.horizontalAlignment == HorizontalAlignment.RIGHT)
        {
            x += offsetX;
        }
        else if (this.horizontalAlignment == HorizontalAlignment.LEFT)
        {
            x -= offsetX;
        }

        this.renderToastBackground(x, y, z, ctx);
        this.renderToastText(x, y, z, ctx);
    }

    protected void renderToastBackground(int x, int y, float z, ScreenContext ctx)
    {
        this.backgroundTexture.renderFourSlicedAt(x, y, z, this.getWidth(), this.getHeight(), ctx);
    }

    protected void renderToastText(int x, int y, float z, ScreenContext ctx)
    {
        if (this.text.isEmpty() == false)
        {
            x += this.padding.getLeft();
            y += this.padding.getTop();

            int color = this.getTextSettings().getTextColor();
            int lineHeight = this.getLineHeight();

            for (StyledText text : this.text)
            {
                TextRenderer.INSTANCE.renderText(x, y, z, color, true, lineHeight, text, ctx);
                y += text.lines.size() * lineHeight + this.messageGap;
            }
        }
    }
}
