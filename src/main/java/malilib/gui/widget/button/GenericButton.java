package malilib.gui.widget.button;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import malilib.gui.icon.DefaultIcons;
import malilib.gui.icon.Icon;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.IconWidget;
import malilib.gui.widget.InteractableWidget;
import malilib.listener.EventListener;
import malilib.render.RenderUtils;
import malilib.render.ShapeRenderUtils;
import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.render.text.StyledTextLine;
import malilib.render.text.StyledTextUtils;
import malilib.util.StringUtils;
import malilib.util.data.EdgeInt;
import malilib.util.data.Int2BooleanFunction;
import malilib.util.data.LeftRight;

public class GenericButton extends InteractableWidget
{
    @Nullable protected ButtonActionListener actionListener;
    @Nullable protected Supplier<Icon> buttonIconSupplier;
    @Nullable protected Supplier<String> displayStringSupplier;
    @Nullable protected Icon buttonIcon;
    @Nullable protected String fullDisplayString;
    @Nullable protected StyledTextLine fullText;
    protected Icon backgroundIcon = DefaultIcons.BUTTON_BACKGROUND;
    protected LeftRight iconAlignment = LeftRight.LEFT;
    protected boolean canScrollToClick;
    protected boolean playClickSound = true;
    protected boolean renderButtonBackgroundTexture = true;
    protected boolean renderFullTextOnHover = true;
    protected boolean rightAligned;
    protected int backgroundIconSlicingEdgeThickness = 4;
    protected int disabledTextColor = 0xFF606060;
    protected int iconVsLabelPadding = 5;

    public GenericButton(int width, int height)
    {
        super(width, height);

        this.canReceiveMouseClicks = true;
        this.blockHoverContentFromBelow = true;

        this.textSettings.setTextColor(0xFFE0E0E0);
        this.textSettings.setHoveredTextColor(0xFFFFFFA0);
        this.textSettings.setUseHoverTextColor(true);
        this.textOffset.setCenterHorizontally(true);
        this.textOffset.setXOffset(0);
        this.padding.setLeftRight(5);

        this.getHoverInfoFactory().setStringListProvider("full_label", this::getFullLabelHoverString, 99);
        this.getBorderRenderer().getNormalSettings().setColor(0x00000000);
    }

    /**
     * Sets an action listener that takes no arguments.
     */
    public GenericButton setActionListener(@Nullable EventListener actionListener)
    {
        this.actionListener = (mBtn, btn) -> {
            if (mBtn == 0)
            {
                actionListener.onEvent();
                return true;
            }
            return false;
        };

        return this;
    }

    /**
     * Sets an action listener that only takes in the mouse button that was clicked.
     */
    public GenericButton setActionListener(@Nullable Int2BooleanFunction actionListener)
    {
        this.actionListener = (mBtn, btn) -> actionListener.apply(mBtn);
        return this;
    }

    /**
     * Sets an action listener that gets in as arguments both the mouse button that was clicked,
     * and a reference to this button widget.
     */
    public GenericButton setActionListener(@Nullable ButtonActionListener actionListener)
    {
        this.actionListener = actionListener;
        return this;
    }

    /**
     * Sets the supplier that provides the full (non-clamped) display string.
     * The displayStringSupplier takes precedence over a possible fixed display string,
     * and will in fact override the value.
     */
    public GenericButton setDisplayStringSupplier(@Nullable Supplier<String> displayStringSupplier)
    {
        this.displayStringSupplier = displayStringSupplier;
        this.updateButtonState();
        return this;
    }

    /**
     * Sets the supplier that provides the button icon.
     * The buttonIconSupplier takes precedence over a possible fixed icon,
     * and will in fact override the value.
     */
    public GenericButton setButtonIconSupplier(@Nullable Supplier<Icon> buttonIconSupplier)
    {
        this.buttonIconSupplier = buttonIconSupplier;
        this.updateButtonState();
        return this;
    }

    public GenericButton setCanScrollToClick(boolean canScroll)
    {
        this.canScrollToClick = canScroll;
        this.canReceiveMouseScrolls = canScroll;
        return this;
    }

    public GenericButton setPlayClickSound(boolean playSound)
    {
        this.playClickSound = playSound;
        return this;
    }

    public GenericButton setIsRightAligned(boolean rightAligned)
    {
        this.rightAligned = rightAligned;
        return this;
    }

    /**
     * Sets the padding between the icon and the display string
     */
    public GenericButton setIconVsLabelPadding(int padding)
    {
        this.iconVsLabelPadding = padding;
        return this;
    }

    public GenericButton setDisabledTextColor(int color)
    {
        this.disabledTextColor = color;
        return this;
    }

    /**
     * Set the "edge ring" thickness for spliced background texture.
     * Basically this means that this many pixels from the opposite edge will be avoided
     * in the four spliced mode, and in the nine spliced mode the 8 edge parts will be this thick.
     */
    public void setBackgroundIconSlicingEdgeThickness(int backgroundIconSlicingEdgeThickness)
    {
        this.backgroundIconSlicingEdgeThickness = backgroundIconSlicingEdgeThickness;
    }

    public GenericButton setBackgroundIcon(Icon icon)
    {
        this.backgroundIcon = icon;
        return this;
    }

    public GenericButton setRenderButtonBackgroundTexture(boolean renderButtonBackgroundTexture)
    {
        this.renderButtonBackgroundTexture = renderButtonBackgroundTexture;
        return this;
    }

    /**
     * Sets whether overly long and clamped text should be rendered in full (with a hover background)
     * when the button is hovered over.
     */
    public GenericButton setRenderFullTextOnHover(boolean render)
    {
        this.renderFullTextOnHover = render;
        return this;
    }

    public GenericButton setIconAlignment(LeftRight alignment)
    {
        this.iconAlignment = alignment;
        return this;
    }

    /**
     * Sets the fixed display string. Note that if a displayStringSupplier
     * has been set, then that will overwrite this value.
     */
    public GenericButton setDisplayString(String text)
    {
        this.fullDisplayString = text;
        this.updateButtonState();
        return this;
    }

    /**
     * Sets the button icon. Note that if a buttonIconSupplier
     * has been set, then that will overwrite this value.
     */
    public GenericButton setButtonIcon(@Nullable Icon buttonIcon)
    {
        this.buttonIcon = buttonIcon;
        this.updateButtonState();
        return this;
    }

    protected List<String> getFullLabelHoverString()
    {
        StyledTextLine text = this.getTextForRender();

        if (this.getTextForRender() != null && this.fullDisplayString != null)
        {
            if (this.automaticWidth == false &&
                text.renderWidth > this.getMaxDisplayStringWidth())
            {
                return ImmutableList.of(StringUtils.translate("malilib.hover.button.full_button_label",
                                                              this.fullDisplayString));
            }
        }

        return EMPTY_STRING_LIST;
    }

    @Override
    public void updateWidgetState()
    {
        this.updateButtonState();
    }

    /**
     * Updates the display string and any other possible state
     */
    public void updateButtonState()
    {
        this.updateButtonIcon();
        this.updateDisplayString();
        this.updateSize();
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isEnabled() && this.tryExecuteAction(mouseButton))
        {
            this.playClickSound();
            super.onMouseClicked(mouseX, mouseY, mouseButton); // Call the possible click listener

            this.updateButtonState();
        }

        return true;
    }

    @Override
    protected boolean onMouseScrolled(int mouseX, int mouseY, double verticalWheelDelta, double horizontalWheelDelta)
    {
        if (this.canScrollToClick)
        {
            int mouseButton = verticalWheelDelta < 0.0 ? 1 : 0;
            return this.onMouseClicked(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    protected void playClickSound()
    {
        if (this.playClickSound)
        {
            ISound sound = PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F);
            this.mc.getSoundHandler().playSound(sound);
        }
    }

    @Override
    protected void onSizeChanged()
    {
        super.onSizeChanged();
        this.updateDisplayString();
    }

    @Override
    public void updateWidth()
    {
        if (this.automaticWidth)
        {
            int width = this.padding.getHorizontalTotal();
            StyledTextLine text = this.getTextForRender();

            if (text != null)
            {
                width += text.renderWidth;
            }

            if (this.buttonIcon != null)
            {
                int extraPadding = text != null ? this.iconVsLabelPadding : 0;
                width += this.buttonIcon.getWidth() + extraPadding;
            }

            if (this.rightAligned)
            {
                int oldWidth = this.getWidth();

                if (width != oldWidth)
                {
                    this.setPositionNoUpdate(this.getX() - (width - oldWidth), this.getY());
                }
            }

            this.setWidthNoUpdate(width);
        }
    }

    @Override
    public void updateHeight()
    {
        if (this.automaticHeight)
        {
            int height = 0;

            if (this.getTextForRender() != null)
            {
                height = this.getLineHeight();
            }

            if (this.buttonIcon != null)
            {
                height = Math.max(height, this.buttonIcon.getHeight());
            }

            height += this.padding.getVerticalTotal();

            this.setHeightNoUpdate(height);
        }
    }

    protected boolean tryExecuteAction(int mouseButton)
    {
        return this.actionListener == null || this.actionListener.actionPerformedWithButton(mouseButton, this);
    }

    protected int getMaxDisplayStringWidth()
    {
        Icon icon = this.buttonIcon;
        int totalWidth = this.automaticWidth ? 8192 : this.getWidth();
        int usedWidth = this.padding.getHorizontalTotal();

        if (icon != null)
        {
            usedWidth += icon.getWidth() + this.iconVsLabelPadding;
        }

        return totalWidth - usedWidth;
    }

    protected void updateButtonIcon()
    {
        if (this.buttonIconSupplier != null)
        {
            this.buttonIcon = this.buttonIconSupplier.get();
        }
    }

    protected void updateDisplayString()
    {
        this.fullText = null;

        if (this.displayStringSupplier != null)
        {
            this.fullDisplayString = this.displayStringSupplier.get();
        }

        if (this.fullDisplayString != null)
        {
            this.buildDisplayText(this.fullDisplayString);
        }
        else
        {
            this.text = null;
        }

        this.updateHoverStrings();
    }

    protected void buildDisplayText(String displayString)
    {
        StyledTextLine text = StyledTextLine.parseFirstLine(displayString);

        if (this.automaticWidth == false)
        {
            int maxWidth = this.getMaxDisplayStringWidth();

            if (text.renderWidth > maxWidth)
            {
                // Only set fullText if the text is clamped
                this.fullText = text;
                text = StyledTextUtils.clampStyledTextToMaxWidth(text, maxWidth, LeftRight.RIGHT, " ...");
            }
        }

        this.text = text;
    }

    protected StyledTextLine getFullTextForRender()
    {
        return this.fullText;
    }

    /*
    protected boolean shouldUpdateDisplayString(String oldFullString)
    {
        return Objects.equals(oldFullString, this.fullDisplayString) == false ||
               ((this.displayString == null) != (this.fullDisplayString == null));
    }
    */

    @Override
    protected int getTextColorForRender(boolean hovered)
    {
        return this.isEnabled() == false ? this.disabledTextColor : super.getTextColorForRender(hovered);
    }

    @Override
    protected int getTextPositionX(int x, int usableWidth, int textWidth)
    {
        Icon icon = this.buttonIcon;

        if (this.iconAlignment == LeftRight.LEFT && icon != null &&
            this.textOffset.getCenterHorizontally() == false)
        {
            // The input x is this.x + this.padding.getLeft()
            return x + this.iconOffset.getXOffset() + icon.getWidth() + this.textOffset.getXOffset();
        }

        return super.getTextPositionX(x, usableWidth, textWidth);
    }

    protected int getIconOffsetX(int buttonWidth, int iconWidth)
    {
        int iconXOffset = this.padding.getLeft() + this.iconOffset.getXOffset();
        StyledTextLine text = this.getTextForRender();

        if (iconXOffset > 0)
        {
            return iconXOffset;
        }
        // With icon-only buttons, center it horizontally
        else if (text == null || text.renderWidth == 0)
        {
            return (buttonWidth - iconWidth) / 2;
        }
        else
        {
            boolean bgEnabled = this.getBackgroundRenderer().getNormalSettings().isEnabled();
            return bgEnabled ? 4 : 0;
        }
    }

    protected void renderButtonBackgroundIcon(int x, int y, float z, int width, int height,
                                              boolean hovered, ScreenContext ctx)
    {
        int variantIndex = IconWidget.getVariantIndex(this.isEnabled(), hovered);
        int iconWidth = this.backgroundIcon.getWidth();
        int iconHeight = this.backgroundIcon.getHeight();
        int edge = this.backgroundIconSlicingEdgeThickness;

        if (width > (iconWidth - edge) * 2 || height > (iconHeight - edge) * 2)
        {
            RenderUtils.renderNineSlicedTexture(x, y, z, width, height, edge, this.backgroundIcon, variantIndex, ctx);
        }
        else
        {
            this.backgroundIcon.renderFourSlicedAt(x, y, z, width, height, variantIndex, ctx);
        }
    }

    protected void renderIcon(int x, int y, float z, int width, int height, boolean hovered, ScreenContext ctx)
    {
        Icon icon = this.buttonIcon;

        if (icon != null)
        {
            boolean leftAligned = this.iconAlignment == LeftRight.LEFT;
            int iconWidth = icon.getWidth();
            int iconYOffset = this.iconOffset.getYOffset();
            int offX = this.getIconOffsetX(width, iconWidth);
            int offY = iconYOffset > 0 ? iconYOffset : (height - icon.getHeight()) / 2;
            int ix = leftAligned ? x + offX : x + width - iconWidth - offX;

            icon.renderAt(ix, y + offY, z + 0.125f, IconWidget.getVariantIndex(this.isEnabled(), hovered), ctx);
        }
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        int width = this.getWidth();
        int height = this.getHeight();
        boolean hovered = this.isHoveredForRender(ctx);

        this.renderIcon(x, y, z, width, height, hovered, ctx);
    }

    @Override
    protected void renderWidgetBackgroundAndBorder(int x, int y, float z, ScreenContext ctx)
    {
        super.renderWidgetBackgroundAndBorder(x, y, z, ctx);

        if (this.renderButtonBackgroundTexture)
        {
            int width = this.getWidth();
            int height = this.getHeight();
            boolean hovered = this.isHoveredForRender(ctx);

            this.renderButtonBackgroundIcon(x, y, z, width, height, hovered, ctx);
        }
    }

    @Override
    protected void renderText(int x, int y, float z, int color, ScreenContext ctx)
    {
        StyledTextLine fullText = this.getFullTextForRender();

        if (this.renderFullTextOnHover && fullText != null && this.isHoveredForRender(ctx))
        {
            this.renderFullTextWithBackground(x, y, z, color, fullText, ctx);
        }
        else
        {
            super.renderText(x, y, z, color, ctx);
        }
    }

    protected void renderFullTextWithBackground(int x, int y, float z, int color, StyledTextLine text, ScreenContext ctx)
    {
        EdgeInt padding = this.padding;
        int usableWidth = this.getWidth() - padding.getHorizontalTotal();
        int usableHeight = this.getHeight() - padding.getVerticalTotal();
        x = this.getTextPositionX(x + padding.getLeft(), usableWidth, text.renderWidth);
        y = this.getTextPositionY(y + padding.getTop(), usableHeight, this.getLineHeight());
        int width = text.renderWidth + 8;
        int height = this.getLineHeight() + 6;

        this.renderFullTextBackground(x - 4, y - 4, z, width, height, ctx);
        this.renderTextLine(x, y, z + 2, color, true, text, ctx);
    }

    protected void renderFullTextBackground(int x, int y, float z, int width, int height, ScreenContext ctx)
    {
        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();
        ShapeRenderUtils.renderRectangle(x, y, z + 1, width, height, 0xF0000000, builder);
        ShapeRenderUtils.renderOutline(x, y, z + 1, width, height, 1, 0xFF30E0E0, builder);
        builder.draw();
    }

    public static GenericButton create(int width, int height, String translationKey)
    {
        GenericButton button = new GenericButton(width, height);
        button.fullDisplayString = StringUtils.translate(translationKey);
        button.updateButtonState();
        return button;
    }

    public static GenericButton create(int height, String translationKey)
    {
        return create(-1, height, translationKey);
    }

    public static GenericButton create(int height, String translationKey, EventListener actionListener)
    {
        GenericButton button = create(height, translationKey);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton create(int height, String translationKey, ButtonActionListener actionListener)
    {
        GenericButton button = create(height, translationKey);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton create(String translationKey)
    {
        return create(20, translationKey);
    }

    public static GenericButton create(int height, Supplier<String> displayStringSupplier)
    {
        GenericButton button = new GenericButton(-1, height);
        button.setDisplayStringSupplier(displayStringSupplier);
        return button;
    }

    public static GenericButton create(int height, Supplier<String> displayStringSupplier, EventListener actionListener)
    {
        GenericButton button = create(height, displayStringSupplier);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton create(int height, Supplier<String> displayStringSupplier, ButtonActionListener actionListener)
    {
        GenericButton button = create(height, displayStringSupplier);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton create(Supplier<String> displayStringSupplier)
    {
        return create(20, displayStringSupplier);
    }

    public static GenericButton create(String translationKey, EventListener actionListener)
    {
        GenericButton button = create(translationKey);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton create(int width, int height, Icon icon)
    {
        GenericButton button = new GenericButton(width, height);
        button.getPadding().setAll(0);
        button.setButtonIcon(icon);
        button.setRenderButtonBackgroundTexture(false);
        button.updateButtonState();
        return button;
    }

    public static GenericButton create(Icon icon)
    {
        return create(icon.getWidth(), icon.getHeight(), icon);
    }

    public static GenericButton create(int height, String translationKey, Icon icon)
    {
        GenericButton button = new GenericButton(-1, height);
        button.fullDisplayString = StringUtils.translate(translationKey);
        button.setButtonIcon(icon);
        button.padding.setLeftRight(4);
        button.getTextOffset().setCenterHorizontally(false);
        button.getTextOffset().setXOffset(button.padding.getLeft() + button.iconOffset.getXOffset());
        button.updateButtonState();
        return button;
    }

    public static GenericButton create(String translationKey, Icon icon)
    {
        return create(20, translationKey, icon);
    }

    public static GenericButton create(Icon icon, EventListener actionListener)
    {
        GenericButton button = create(icon);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton create(int height, Icon icon, EventListener actionListener)
    {
        int size = height;
        GenericButton button = create(size, size, icon);
        button.setActionListener(actionListener);
        return button;
    }

    public static GenericButton create(@Nullable Supplier<Icon> iconSupplier, EventListener actionListener)
    {
        GenericButton button = new GenericButton(-1, -1);
        button.getPadding().setAll(0);
        button.setRenderButtonBackgroundTexture(false);
        button.setActionListener(actionListener);
        button.setButtonIconSupplier(iconSupplier);
        return button;
    }
}
