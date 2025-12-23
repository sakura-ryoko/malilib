package malilib.gui.widget.button;

import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;

import malilib.gui.icon.DefaultIcons;
import malilib.gui.icon.Icon;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.IconWidget;
import malilib.listener.EventListener;
import malilib.render.ShapeRenderUtils;
import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.render.text.StyledTextLine;
import malilib.util.StringUtils;
import malilib.util.game.wrap.RenderWrap;

public class OnOffButton extends GenericButton
{
    @Nullable protected final String translationKey;
    protected final BooleanSupplier valueStatusSupplier;
    protected Icon iconOn;
    protected Icon iconOff;
    protected OnOffStyle style;
    protected String displayStringOn;
    protected String displayStringOff;
    protected boolean showAsOffIfDisabled;
    @Nullable protected StyledTextLine textForOn;
    @Nullable protected StyledTextLine textForOff;
    @Nullable protected StyledTextLine fullTextForOn;
    @Nullable protected StyledTextLine fullTextForOff;

    /**
     * Pass -1 as the <b>width</b> to automatically set the width
     * to a value where the ON and OFF state buttons are the same width.
     * @param style The button style to use
     * @param valueStatusSupplier The supplier for the current on/off status of this button
     * @param translationKey The translation key to use for the full button text. It should have one %s format specifier for the current status string. Pass null to use the status string directly, without any other labeling text.
     */
    public OnOffButton(int width, int height, OnOffStyle style,
                       BooleanSupplier valueStatusSupplier,
                       @Nullable String translationKey)
    {
        super(width, height);

        this.translationKey = translationKey;
        this.valueStatusSupplier = valueStatusSupplier;
        this.iconOn = DefaultIcons.SLIDER_GREEN;
        this.iconOff = DefaultIcons.SLIDER_RED;

        this.setDisplayStringSupplier(this::getCurrentDisplayString);
        this.getBorderRenderer().getNormalSettings().setBorderWidthAndColor(1, 0xFF000000);
        this.getBackgroundRenderer().getNormalSettings().setColor(0xFF303030);

        this.setStyle(style);   // This needs to happen before trying to update the texts in updateCachedTexts()
        this.updateCachedTexts();
    }

    @Override
    public void updateButtonState()
    {
        int color = this.getTextColorForRender();
        this.getTextSettings().setTextColor(color);

        super.updateButtonState();
    }

    public OnOffButton setStyle(OnOffStyle style)
    {
        this.style = style;

        boolean isSlider = this.style == OnOffStyle.SLIDER_ON_OFF;
        this.renderButtonBackgroundTexture = isSlider == false;
        this.getBorderRenderer().getNormalSettings().setBorderWidth(isSlider ? 1 : 0);
        this.getBackgroundRenderer().getNormalSettings().setEnabled(isSlider);
        this.getTextSettings().setHoveredTextColor(isSlider ? 0xFFF0F000 : 0xFFFFFFA0);

        return this;
    }

    public void setShowAsOffIfDisabled(boolean showAsOffIfDisabled)
    {
        this.showAsOffIfDisabled = showAsOffIfDisabled;
    }

    protected String getCurrentDisplayString()
    {
        boolean value = this.getCurrentValue();
        return value ? this.displayStringOn : this.displayStringOff;
    }

    public boolean getCurrentValue()
    {
        if (this.showAsOffIfDisabled && this.enabled == false)
        {
            return false;
        }

        return this.valueStatusSupplier != null && this.valueStatusSupplier.getAsBoolean();
    }

    protected String getOnOffStringForState(boolean state)
    {
        return this.style != null ? this.style.getDisplayName(state) : "???";
    }

    protected String buildDisplayStringForState(boolean value)
    {
        String valueStr = this.getOnOffStringForState(value);
        return this.translationKey != null ? StringUtils.translate(this.translationKey, valueStr) : valueStr;
    }

    @Nullable
    @Override
    protected StyledTextLine getTextForRender()
    {
        boolean value = this.getCurrentValue();
        return value ? this.textForOn : this.textForOff;
    }

    @Override
    protected StyledTextLine getFullTextForRender()
    {
        boolean value = this.getCurrentValue();
        return value ? this.fullTextForOn : this.fullTextForOff;
    }

    @Override
    protected int getTextColorForRender()
    {
        boolean value = this.getCurrentValue();
        boolean isSlider = this.style == OnOffStyle.SLIDER_ON_OFF;

        return (isSlider == false || value) ? 0xFFE0E0E0 : 0xFF909090;
    }

    @Override
    protected int getTextColorForRender(boolean hovered)
    {
        if (this.isEnabled())
        {
            return this.getTextColorForRender();
        }

        return super.getTextColorForRender(hovered);
    }

    @Override
    protected void updateDisplayString()
    {
        this.updateCachedTexts();
        super.updateDisplayString();
    }

    protected void updateCachedTexts()
    {
        this.displayStringOn = this.buildDisplayStringForState(true);
        this.displayStringOff = this.buildDisplayStringForState(false);

        this.buildDisplayText(this.displayStringOn);
        this.textForOn = this.text;
        this.fullTextForOn = this.fullText;

        this.buildDisplayText(this.displayStringOff);
        this.textForOff = this.text;
        this.fullTextForOff = this.fullText;
    }

    @Override
    public void updateWidth()
    {
        if (this.automaticWidth)
        {
            int sw1 = this.getStringWidth(this.displayStringOff);
            int sw2 = this.getStringWidth(this.displayStringOn);
            int width = Math.max(sw1, sw2);

            if (this.style == OnOffStyle.SLIDER_ON_OFF)
            {
                width += Math.max(this.iconOn.getWidth(), this.iconOff.getWidth());
            }

            width += this.padding.getHorizontalTotal();

            this.setWidthNoUpdate(width);
        }
    }

    @Override
    protected int getTextPositionX(int baseX, int usableWidth, int textWidth)
    {
        if (this.style == OnOffStyle.SLIDER_ON_OFF)
        {
            boolean value = this.getCurrentValue();
            Icon icon = value ? this.iconOn : this.iconOff;
            int iconWidth = icon.getWidth();

            usableWidth -= iconWidth;

            // The slider is on the left side
            if (value == false)
            {
                baseX += iconWidth;
            }
        }

        return super.getTextPositionX(baseX, usableWidth, textWidth);
    }

    @Override
    protected void renderIcon(int x, int y, float z, int width, int height, boolean hovered, ScreenContext ctx)
    {
        super.renderIcon(x, y, z, width, height, hovered, ctx);

        if (this.style == OnOffStyle.SLIDER_ON_OFF)
        {
            renderOnOffSlider(x, y, z + 0.125f, width, height,
                              this.getCurrentValue(), this.isEnabled(), hovered,
                              this.iconOn, this.iconOff, ctx);
        }
    }

    public static void renderOnOffSlider(int x, int y, float z, int width, int height,
                                         boolean state, boolean enabled, boolean hovered,
                                         Icon iconOn, Icon iconOff,
                                         ScreenContext ctx)
    {
        Icon icon = state ? iconOn : iconOff;

        int iconWidth = icon.getWidth();
        int iconHeight1 = height / 2 - 1;
        int iconHeight2 = (height & 0x1) != 0 ? iconHeight1 + 1 : iconHeight1; // Account for odd height
        int sliderX = state ? x + width - iconWidth - 1 : x + 1;
        int variantIndex = IconWidget.getVariantIndex(enabled, hovered);
        int u = icon.getVariantU(variantIndex);
        int v1 = icon.getVariantV(variantIndex);
        int v2 = v1 + icon.getHeight() - iconHeight2;

        RenderWrap.bindTexture(icon.getTexture());
        VertexBuilder builder = VanillaWrappingVertexBuilder.texturedQuad();
        ShapeRenderUtils.renderTexturedRectangle256(sliderX, y + 1              , z, u, v1, iconWidth, iconHeight1, builder);
        ShapeRenderUtils.renderTexturedRectangle256(sliderX, y + 1 + iconHeight1, z, u, v2, iconWidth, iconHeight2, builder);
        builder.draw();
    }

    public static OnOffButton onOff(int height, String translationKey, BooleanSupplier statusSupplier, EventListener actionListener)
    {
        OnOffButton button = new OnOffButton(-1, height, OnOffStyle.TEXT_ON_OFF, statusSupplier, translationKey);
        button.setActionListener(actionListener);
        return button;
    }

    public static OnOffButton onOff(int height, String translationKey, BooleanSupplier statusSupplier, ButtonActionListener actionListener)
    {
        OnOffButton button = new OnOffButton(-1, height, OnOffStyle.TEXT_ON_OFF, statusSupplier, translationKey);
        button.setActionListener(actionListener);
        return button;
    }

    public static OnOffButton simpleSlider(int height, BooleanSupplier statusSupplier, EventListener actionListener)
    {
        OnOffButton button = new OnOffButton(-1, height, OnOffStyle.SLIDER_ON_OFF, statusSupplier, null);
        button.setActionListener(actionListener);
        return button;
    }

    public enum OnOffStyle
    {
        TEXT_ON_OFF     ("malilib.button.on_off.text_on_off.on",     "malilib.button.on_off.text_on_off.off"),
        TEXT_TRUE_FALSE ("malilib.button.on_off.text_true_false.on", "malilib.button.on_off.text_true_false.off"),
        SLIDER_ON_OFF   ("malilib.button.on_off.slider_on_off.on",   "malilib.button.on_off.slider_on_off.off");

        private final String translationKeyOn;
        private final String translationKeyOff;

        OnOffStyle(String translationKeyOn, String translationKeyOff)
        {
            this.translationKeyOn = translationKeyOn;
            this.translationKeyOff = translationKeyOff;
        }

        public String getDisplayName(boolean state)
        {
            String key = state ? this.translationKeyOn : this.translationKeyOff;
            return StringUtils.translate(key);
        }
    }
}
