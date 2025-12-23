package malilib.gui.widget;

import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;

import malilib.MaLiLibConfigs;
import malilib.gui.icon.Icon;
import malilib.gui.util.ElementOffset;
import malilib.gui.util.GuiUtils;
import malilib.gui.util.ScreenContext;
import malilib.render.RectangleRenderer;
import malilib.render.ShapeRenderUtils;
import malilib.render.TextRenderUtils;
import malilib.render.text.MultiLineTextRenderSettings;
import malilib.render.text.StyledTextLine;
import malilib.render.text.TextRenderer;
import malilib.render.text.TextStyle;
import malilib.util.data.Color4f;
import malilib.util.data.EdgeInt;
import malilib.util.data.Identifier;
import malilib.util.game.wrap.GameWrap;
import malilib.util.game.wrap.RenderWrap;

public class BaseWidget
{
    public static final ImmutableList<String> EMPTY_STRING_LIST = ImmutableList.of();
    public static final RectangleRenderer DEBUG_TEXT_BG_RENDERER = (x, y, z, w, h, ctx) -> ShapeRenderUtils.renderOutlinedRectangle(x - 3, y - 3, z, w + 6, h + 6, 0xE0000000, 0xFFC0C0C0, ctx);

    private static final ArrayListMultimap<Long, String> DEBUG_STRINGS = ArrayListMultimap.create();
    private static int lastDebugOutlineColorHue;
    private static int nextWidgetId;

    protected final Minecraft mc = GameWrap.getClient();
    protected final EdgeInt margin = new EdgeInt();
    protected final EdgeInt padding = new EdgeInt();
    protected final ElementOffset iconOffset = new ElementOffset();
    protected final ElementOffset textOffset = new ElementOffset();
    protected final MultiLineTextRenderSettings textSettings = new MultiLineTextRenderSettings();
    protected final int id;

    private int x;
    private int y;
    private float z;
    private int height;
    private int width;
    protected float zOffset;

    protected TextRenderer textRenderer;
    @Nullable protected Icon icon;
    @Nullable protected StyledTextLine text;
    protected boolean automaticHeight;
    protected boolean automaticWidth;
    protected int maxHeight;
    protected int maxWidth;
    protected int zLevelIncrement = 2;

    public BaseWidget()
    {
        this(-1, -1);
    }

    public BaseWidget(int x, int y, int width, int height)
    {
        this(width, height);

        this.x = x;
        this.y = y;
    }

    public BaseWidget(int width, int height)
    {
        this.id = nextWidgetId++;
        this.textRenderer = TextRenderer.INSTANCE;
        this.textOffset.setXOffset(4);
        this.textOffset.setYOffset(1);

        this.width = width;
        this.height = height;

        this.automaticWidth = width < 0;
        this.automaticHeight = height < 0;

        if (width < -1)
        {
            this.maxWidth = -width;
        }
        if (height < -1)
        {
            this.maxHeight = -height;
        }
    }

    /**
     * Returns the unique(-ish) ID of this widget.
     * The ID is increment by one for each widget that is created (starting from 0 for each game launch).
     * This ID is mainly meant for things like identifying the top-most hovered widget.
     */
    public int getId()
    {
        return this.id;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public float getZ()
    {
        return this.z;
    }

    public float getZOffset()
    {
        return this.zOffset;
    }

    public int getRight()
    {
        return this.getX() + this.getWidth();
    }

    public int getBottom()
    {
        return this.getY() + this.getHeight();
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public int getMaxWidth()
    {
        return this.maxWidth;
    }

    public int getMaxHeight()
    {
        return this.maxHeight;
    }

    public boolean hasMaxWidth()
    {
        return this.maxWidth > 0;
    }

    public boolean hasMaxHeight()
    {
        return this.maxHeight > 0;
    }

    public boolean hasAutomaticWidth()
    {
        return this.automaticWidth;
    }

    public boolean hasAutomaticHeight()
    {
        return this.automaticHeight;
    }

    protected int clampToMaxWidth(int width)
    {
        if (this.hasMaxWidth())
        {
            width = Math.min(width, this.maxWidth);
        }

        return width;
    }

    protected int clampToMaxHeight(int height)
    {
        if (this.hasMaxHeight())
        {
            height = Math.min(height, this.maxHeight);
        }

        return height;
    }

    protected int getRequestedContentWidth()
    {
        Icon icon = this.getIcon();

        if (icon != null)
        {
            return icon.getWidth() + this.getIconOffset().getXOffset();
        }

        return 0;
    }

    protected int getRequestedContentHeight()
    {
        Icon icon = this.getIcon();

        if (icon != null)
        {
            return icon.getHeight() + this.getIconOffset().getYOffset();
        }

        return 0;
    }

    protected int getNonContentWidth()
    {
        return this.getPadding().getHorizontalTotal();
    }

    protected int getNonContentHeight()
    {
        return this.getPadding().getVerticalTotal();
    }

    public void setX(int x)
    {
        int oldX = this.x;
        int oldY = this.y;

        this.x = x;

        if (oldX != x)
        {
            this.onPositionChanged(oldX, oldY);
        }
    }

    public void setY(int y)
    {
        int oldX = this.x;
        int oldY = this.y;

        this.y = y;

        if (oldY != y)
        {
            this.onPositionChanged(oldX, oldY);
        }
    }

    public void setZ(float z)
    {
        this.z = z;
    }

    public void setZOffset(float zOffset)
    {
        this.zOffset = zOffset;
    }

    public void setPosition(int x, int y)
    {
        int oldX = this.x;
        int oldY = this.y;

        this.x = x;
        this.y = y;

        if (oldX != x || oldY != y)
        {
            this.onPositionChanged(oldX, oldY);
        }
    }

    public void moveBy(int diffX, int diffY)
    {
        this.setPosition(this.x + diffX, this.y + diffY);
    }

    public void setPositionNoUpdate(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void setPositionAndSize(int x, int y, int width, int height)
    {
        int oldX = this.x;
        int oldY = this.y;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.onPositionOrSizeChanged(oldX, oldY);
    }

    public void setRight(int xRight)
    {
        int oldX = this.x;
        int oldY = this.y;

        this.x = xRight - this.width;

        if (oldX != this.x)
        {
            this.onPositionChanged(oldX, oldY);
        }
    }

    public void setBottom(int yBottom)
    {
        int oldX = this.x;
        int oldY = this.y;

        this.y = yBottom - this.height;

        if (oldY != this.y)
        {
            this.onPositionChanged(oldX, oldY);
        }
    }

    public void setWidth(int width)
    {
        int old = this.width;
        this.width = width;

        if (old != width)
        {
            this.onSizeChanged();
        }
    }

    public void setHeight(int height)
    {
        int old = this.height;
        this.height = height;

        if (old != height)
        {
            this.onSizeChanged();
        }
    }

    public void setSize(int width, int height)
    {
        int oldWidth = this.width;
        int oldHeight = this.height;

        this.width = width;
        this.height = height;

        if (oldWidth != width || oldHeight != height)
        {
            this.onSizeChanged();
        }
    }

    public void setWidthNoUpdate(int width)
    {
        this.width = width;
    }

    public void setHeightNoUpdate(int height)
    {
        this.height = height;
    }

    public void setSizeNoUpdate(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the maximum width, and enables the maximum width if the value is > 0
     */
    public void setMaxWidth(int maxWidth)
    {
        this.maxWidth = maxWidth;
        this.updateWidth();
    }

    /**
     * Sets the maximum height, and enables the maximum height if the value is > 0
     */
    public void setMaxHeight(int maxHeight)
    {
        this.maxHeight = maxHeight;
        this.updateHeight();
    }

    public void setAutomaticWidth(boolean automaticWidth)
    {
        this.automaticWidth = automaticWidth;
    }

    public void setAutomaticHeight(boolean automaticHeight)
    {
        this.automaticHeight = automaticHeight;
    }

    public void centerVerticallyInside(BaseWidget containerWidget)
    {
        this.centerVerticallyInside(containerWidget, 0);
    }

    public void centerVerticallyInside(BaseWidget containerWidget, int offset)
    {
        int yOffset = (containerWidget.getHeight() - this.getHeight()) / 2 + offset;
        this.setY(containerWidget.getY() + yOffset);
    }

    /**
     * This method is called after the widget position is changed
     * @param oldX the x position before the position was changed
     * @param oldY the y position before the position was changed
     */
    protected void onPositionChanged(int oldX, int oldY)
    {
    }

    /**
     * This method is called after the widget size is changed
     */
    protected void onSizeChanged()
    {
    }

    /**
     * This method is called after either the widget position or size is changed.
     * This is meant for cases where it's necessary or beneficial to avoid the
     * calls for both size and position changes separately.
     * @param oldX the x position before the position was changed
     * @param oldY the y position before the position was changed
     */
    protected void onPositionOrSizeChanged(int oldX, int oldY)
    {
    }

    /**
     * This is called when the container widget or screen
     * changes its geometry, in case this widget wants to somehow
     * react to that change and maybe update its sub widget positions.
     */
    public void onContainerGeometryChanged()
    {
    }

    public void updateSize()
    {
        this.updateWidth();
        this.updateHeight();
    }

    public void updateWidth()
    {
        updateWidgetDimension(this::hasAutomaticWidth,
                              this::getRequestedContentWidth,
                              this::getNonContentWidth,
                              this::clampToMaxWidth,
                              this::setWidthNoUpdate);
    }

    public void updateHeight()
    {
        updateWidgetDimension(this::hasAutomaticHeight,
                              this::getRequestedContentHeight,
                              this::getNonContentHeight,
                              this::clampToMaxHeight,
                              this::setHeightNoUpdate);
    }

    public void updateWidgetState()
    {
    }

    public void clampToScreen()
    {
        int screenRight = GuiUtils.getScaledWindowWidth();
        int screenBottom = GuiUtils.getScaledWindowHeight();
        int x = this.getX();
        int y = this.getY();

        if (this.getRight() > screenRight)
        {
            x = screenRight - this.getWidth() - 2;
        }

        if (this.getBottom() > screenBottom)
        {
            y = screenBottom - this.getHeight() - 2;
        }

        x = Math.max(x, 0);
        y = Math.max(y, 0);

        this.setPosition(x, y);
    }

    public boolean intersects(EdgeInt rectangle)
    {
        return this.getX()      <= rectangle.getRight() &&
               this.getRight()  >= rectangle.getLeft() &&
               this.getY()      <= rectangle.getBottom() &&
               this.getBottom() >= rectangle.getTop();
    }

    public void setLineHeight(int lineHeight)
    {
        this.getTextSettings().setLineHeight(lineHeight);
    }

    public void setZLevelBasedOnParent(float parentZLevel)
    {
        this.setZ(parentZLevel + this.getZLevelIncrementFromParent());
    }

    /**
     * This method is called whenever a widget gets added to its parent widget or GUI.
     * By default, it updates the widget's own rendering Z-level based on the parent's Z-level.
     */
    public void onWidgetAdded()
    {
    }

    protected int getZLevelIncrementFromParent()
    {
        return this.zLevelIncrement;
    }

    public EdgeInt getMargin()
    {
        return this.margin;
    }

    public EdgeInt getPadding()
    {
        return this.padding;
    }

    public ElementOffset getIconOffset()
    {
        return this.iconOffset;
    }

    public ElementOffset getTextOffset()
    {
        return this.textOffset;
    }

    public MultiLineTextRenderSettings getTextSettings()
    {
        return this.textSettings;
    }

    /**
     * Sets a simple single-line text to be rendered in the widget,
     * without having to add a LabelWidget for it.
     */
    public void setText(@Nullable StyledTextLine text)
    {
        this.text = text;
    }

    @Nullable
    protected StyledTextLine getTextForRender()
    {
        return this.text;
    }

    /**
     * Sets a simple single-line text to be rendered in the widget,
     * without having to add a LabelWidget for it.
     * @param textOffsetX an x offset for the text. By default this is 4 pixels from the left edge.
     * @param textOffsetY an y offset for the text. Note: The text is by default already centered vertically,
     *                    this is an additional offset on top of that!
     */
    public void setText(@Nullable StyledTextLine text, int textOffsetX, int textOffsetY)
    {
        this.text = text;
        this.textOffset.setXOffset(textOffsetX);
        this.textOffset.setYOffset(textOffsetY);
    }

    /**
     * Sets the starting style for the text. Note that the text must have been set already,
     * otherwise this does nothing.
     * @param style the new starting style for the text, to which any styles defined in the text will be merged to
     */
    public void setStartingStyleForText(TextStyle style)
    {
        if (this.text != null)
        {
            this.setText(this.text.withStartingStyle(style));
        }
    }

    @Nullable
    public Icon getIcon()
    {
        return this.icon;
    }

    /**
     * Sets a simple icon to be rendered in the widget,
     * without having to add a nested IconWidget or per-widget code.
     */
    public void setIcon(@Nullable Icon icon)
    {
        this.icon = icon;
    }

    public void bindTexture(Identifier texture)
    {
        RenderWrap.bindTexture(texture);
    }

    public int getLineHeight()
    {
        return this.getTextSettings().getLineHeight();
    }

    public int getFontHeight()
    {
        return this.textRenderer.getFontHeight();
    }

    public int getStringWidth(String str)
    {
        return this.textRenderer.getRenderWidth(str);
    }

    public int getRawStyledTextWidth(String str)
    {
        return StyledTextLine.unParsed(str).renderWidth;
    }

    protected int getTextPositionX(int x, int usableWidth, int textWidth)
    {
        return this.textOffset.getElementPositionX(x, usableWidth, textWidth);
    }

    protected int getTextPositionY(int y, int usableHeight, int textHeight)
    {
        return this.textOffset.getElementPositionY(y, usableHeight, textHeight);
    }

    protected int getIconPositionX(int x, int usableWidth, int iconWidth)
    {
        return this.iconOffset.getElementPositionX(x, usableWidth, iconWidth);
    }

    protected int getIconPositionY(int y, int usableHeight, int iconHeight)
    {
        return this.iconOffset.getElementPositionY(y, usableHeight, iconHeight);
    }

    protected int getTextColorForRender()
    {
        return this.getTextSettings().getTextColor();
    }

    public void renderTextLine(int x, int y, float z, int defaultColor, boolean shadow,
                               StyledTextLine text, ScreenContext ctx)
    {
        this.textRenderer.renderLine(x, y, z, defaultColor, shadow, text, ctx);
    }

    public void renderTextLineRightAligned(int x, int y, float z, int defaultColor, boolean shadow,
                                           StyledTextLine text, ScreenContext ctx)
    {
        x -= text.renderWidth;
        this.textRenderer.renderLine(x, y, z, defaultColor, shadow, text, ctx);
    }

    /**
     * Renders a plain string, by converting it to a StyledTextLine.<br>
     * <b>Note:</b> It's discouraged to use this method, if it's possible to easily
     * generate and cache the resulting StyledTextLine directly.
     */
    public void renderPlainString(int x, int y, float z, int color, boolean shadow, String str, ScreenContext ctx)
    {
        this.textRenderer.renderLine(x, y, z, color, shadow, StyledTextLine.parseJoin(str), ctx);
    }

    protected void renderText(int x, int y, float z, int color, ScreenContext ctx)
    {
        StyledTextLine text = this.getTextForRender();

        if (text != null)
        {
            this.renderTextLine(x, y, z, color, text, ctx);
        }
    }

    protected void renderTextLine(int x, int y, float z, int color, StyledTextLine text, ScreenContext ctx)
    {
        EdgeInt padding = this.padding;
        int usableWidth = this.getWidth() - padding.getHorizontalTotal();
        int usableHeight = this.getHeight() - padding.getVerticalTotal();
        x = this.getTextPositionX(x + padding.getLeft(), usableWidth, text.renderWidth);
        y = this.getTextPositionY(y + padding.getTop(), usableHeight, this.getLineHeight());
        boolean shadow = this.getTextSettings().getTextShadowEnabled();

        this.renderTextLine(x, y, z + 0.0125f, color, shadow, text, ctx);
    }

    protected void renderIcon(int x, int y, float z, ScreenContext ctx)
    {
        Icon icon = this.getIcon();

        if (icon != null)
        {
            int usableWidth = this.getWidth() - this.padding.getHorizontalTotal();
            int usableHeight = this.getHeight() - this.padding.getVerticalTotal();
            x = this.getIconPositionX(x, usableWidth, icon.getWidth());
            y = this.getIconPositionY(y, usableHeight, icon.getHeight());

            icon.renderAt(x, y, z + 0.0125f, 0, ctx);
        }
    }

    public void render(ScreenContext ctx)
    {
        this.renderAt(this.x, this.y, this.z + this.zOffset, ctx);
    }

    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        int color = this.getTextColorForRender();

        this.renderIcon(x, y, z, ctx);
        this.renderText(x, y, z, color, ctx);
    }

    public void renderAtOffset(int xOffset, int yOffset, float zOffset, ScreenContext ctx)
    {
        this.renderAt(this.x + xOffset, this.y + yOffset, this.z + this.zOffset + zOffset, ctx);
    }

    public void renderDebug(boolean hovered, ScreenContext ctx)
    {
        int x = this.x;
        int y = this.y;
        float z = this.z + this.zOffset;

        this.renderDebug(x, y, z, hovered, ctx);
    }

    public void renderDebug(int x, int y, float z, boolean hovered, ScreenContext ctx)
    {
        int w = this.getWidth();
        int h = this.getHeight();

        if (hovered || ctx.getDebugRenderAll())
        {
            renderDebugOutline(x, y, z, w, h, hovered, ctx);

            if (this.padding.isEmpty() == false && MaLiLibConfigs.Debug.GUI_DEBUG_PADDING.getBooleanValue())
            {
                int left = this.padding.getLeft();
                int top = this.padding.getTop();
                int right = this.padding.getRight();
                int bottom = this.padding.getBottom();

                renderDebugOutline(x + left, y + top, z, w - left - right, h - top - bottom, false, 0xFFFFFFFF, ctx);
            }
        }

        boolean debugInfoAlways = ctx.getDebugInfoAlways();

        if (hovered || debugInfoAlways)
        {
            int posX = debugInfoAlways ? x      : ctx.mouseX;
            int posY = debugInfoAlways ? y - 12 : ctx.mouseY;

            if (posY < 0)
            {
                posY = y + h + 2;
            }

            addDebugText(posX, posY, x, y, z, w, h, this.getClass().getName());
        }
    }

    public static int getMaxWidthFrom(BaseWidget... widgets)
    {
        int width = 0;

        for (BaseWidget widget : widgets)
        {
            width = Math.max(width, widget.getWidth());
        }

        return width;
    }

    public static int getMaxHeightFrom(BaseWidget... widgets)
    {
        int height = 0;

        for (BaseWidget widget : widgets)
        {
            height = Math.max(height, widget.getHeight());
        }

        return height;
    }

    public static void updateWidgetDimension(BooleanSupplier automaticSizeChecker,
                                             IntSupplier contentSizeSupplier,
                                             IntSupplier extraSizeSupplier,
                                             IntUnaryOperator sizeClamper,
                                             IntConsumer sizeSetter)
    {
        if (automaticSizeChecker.getAsBoolean())
        {
            int contentSize = contentSizeSupplier.getAsInt();
            int extraSize = extraSizeSupplier.getAsInt();
            int size = sizeClamper.applyAsInt(contentSize + extraSize);
            sizeSetter.accept(size);
        }
    }

    public static void renderDebugOutline(double x, double y, double z, double w, double h,
                                          boolean hovered, ScreenContext ctx)
    {
        int color = Color4f.getColorFromHue(lastDebugOutlineColorHue);
        lastDebugOutlineColorHue += 40;

        renderDebugOutline(x, y, z, w, h, hovered, color, ctx);
    }

    public static void renderDebugOutline(double x, double y, double z, double w, double h,
                                          boolean hovered, int color, ScreenContext ctx)
    {
        double lineWidth = hovered ? 3.0 : 1.0;
        double x1 = x - lineWidth / 4;
        double y1 = y - lineWidth / 4;

        ShapeRenderUtils.renderOutline(x1, y1, z, w + lineWidth / 2, h + lineWidth / 2, lineWidth, color, ctx);
    }

    public static void addDebugText(int mouseX, int mouseY, int x, int y, double z, int w, int h, String text)
    {
        String str = String.format("§7x: §6%d ... %d§7, y: §6%d ... %d§7, z: §d%.1f§7 w: §a%d§7, h: §a%d§7 - §3%s",
                                   x, x + w - 1, y, y + h - 1, z, w, h, text);
        int posY = mouseY - 2;

        Long posLong = (long) posY << 32 | (long) mouseX;
        DEBUG_STRINGS.put(posLong, str);
    }

    public static void renderDebugTextAndClear(ScreenContext ctx)
    {
        if (DEBUG_STRINGS.isEmpty() == false)
        {
            for (Long posLong : DEBUG_STRINGS.keySet())
            {
                int x = (int) posLong.longValue();
                int y = (int) (posLong.longValue() >>> 32);
                TextRenderUtils.renderStyledHoverText(x, y, 10, StyledTextLine.parseList(DEBUG_STRINGS.get(posLong)),
                                                      0xFFFF4040, DEBUG_TEXT_BG_RENDERER, ctx);
            }

            DEBUG_STRINGS.clear();
            RenderWrap.disableItemLighting();
        }

        lastDebugOutlineColorHue = 0;
    }
}
