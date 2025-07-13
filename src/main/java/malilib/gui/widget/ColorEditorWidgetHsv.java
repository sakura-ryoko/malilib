package malilib.gui.widget;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import com.google.common.collect.ImmutableList;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import malilib.gui.callback.FloatSliderCallback;
import malilib.gui.callback.IntegerSliderCallback;
import malilib.gui.util.ScreenContext;
import malilib.render.ShapeRenderUtils;
import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.render.shader.ShaderProgram;
import malilib.util.MathUtils;
import malilib.util.data.Color4f;
import malilib.util.data.FloatStorage;
import malilib.util.data.FloatSupplier;
import malilib.util.data.WrapperFloatStorage;
import malilib.util.data.WrapperIntStorage;
import malilib.util.game.wrap.RenderWrap;
import malilib.util.game.wrap.RenderWrap.BlendDestFactor;
import malilib.util.game.wrap.RenderWrap.BlendSourceFactor;

public class ColorEditorWidgetHsv extends ContainerWidget
{
    protected final IntConsumer colorConsumer;
    protected final int colorIn;
    protected final IntegerEditWidget editH;
    protected final IntegerEditWidget editS;
    protected final IntegerEditWidget editV;
    protected final IntegerEditWidget editR;
    protected final IntegerEditWidget editG;
    protected final IntegerEditWidget editB;
    protected final IntegerEditWidget editA;
    protected final IntegerEditWidget editHex;
    protected final HorizontalColorSliderWidget saturationSlider;
    protected final HorizontalColorSliderWidget valueSlider;
    protected final HorizontalColorSliderWidget rSlider;
    protected final HorizontalColorSliderWidget gSlider;
    protected final HorizontalColorSliderWidget bSlider;
    protected final HorizontalColorSliderWidget aSlider;
    protected final SvSelectorWidget svSelector;
    protected final VerticalHueSliderWidget fullSVHueSlider;
    protected final HorizontalHueSliderWidget currentSVHueSlider;
    protected final ColorIndicatorWidget colorIndicator;
    protected final LabelWidget componentLabels;
    protected final ImmutableList<IntegerEditWidget> editWidgets;
    protected int color;
    protected int lastAppliedColor;
    protected int r;
    protected int g;
    protected int b;
    protected int a;
    protected float h;
    protected float s;
    protected float v;

    public ColorEditorWidgetHsv(Color4f colorIn, Consumer<Color4f> colorConsumer)
    {
        this(colorIn.intValue, i -> colorConsumer.accept(Color4f.fromColor(i)));
    }

    public ColorEditorWidgetHsv(int colorIn, IntConsumer colorConsumer)
    {
        super(290, 154);

        this.colorConsumer = colorConsumer;
        this.colorIn = colorIn;

        this.lastAppliedColor = colorIn;
        this.canReceiveMouseClicks = true;

        this.editH = this.createIntegerEditWidgetHsv(v -> this.h = v / 360f, () -> (int) (this.h * 360), 360);
        this.editS = this.createIntegerEditWidgetHsv(v -> this.s = v / 100f, () -> (int) (this.s * 100), 100);
        this.editV = this.createIntegerEditWidgetHsv(v -> this.v = v / 100f, () -> (int) (this.v * 100), 100);
        this.editR = this.createIntegerEditWidgetRgb(v -> this.r = v, () -> this.r);
        this.editG = this.createIntegerEditWidgetRgb(v -> this.g = v, () -> this.g);
        this.editB = this.createIntegerEditWidgetRgb(v -> this.b = v, () -> this.b);
        this.editA = this.createIntegerEditWidget(v -> this.a = v, () -> this.a, 255);
        this.componentLabels = new LabelWidget("malilib.label.color_editor.hsv.component_labels");

        // FIXME the text field is buggy and won't show the entire text by default even though it can fit with width 90
        this.editHex = new IntegerEditWidget(106, 14, this::setColor, () -> this.color);
        this.editHex.setLabelText("HEX:");
        this.editHex.setLabelFixedWidth(21);
        this.editHex.getLabelWidget().setNormalTextColor(0xFFBBBBBB);
        this.editHex.setToStringFunction(v -> String.format("#%08X", v));
        this.editHex.getTextField().setTextValidator(BaseTextFieldWidget.VALIDATOR_HEX_COLOR_8);
        this.editHex.setAddValueAdjustButton(false);
        this.editHex.setShowRangeTooltip(false);

        this.editWidgets = ImmutableList.of(this.editH, this.editS, this.editV,
                                            this.editR, this.editG, this.editB, this.editA, this.editHex);

        this.saturationSlider = new HorizontalColorSliderWidget(92, 14, () -> Color.HSBtoRGB(this.h, 0, this.v), () -> Color.HSBtoRGB(this.h, 1, this.v), new FloatSliderCallback(new WrapperFloatStorage(0, 1f, () -> this.s, v -> { this.s = v; this.updateColorFromHsvComponents(); }), this::updateEditWidgets));
        this.valueSlider      = new HorizontalColorSliderWidget(92, 14, () -> Color.HSBtoRGB(this.h, this.s, 0), () -> Color.HSBtoRGB(this.h, this.s, 1), new FloatSliderCallback(new WrapperFloatStorage(0, 1f, () -> this.v, v -> { this.v = v; this.updateColorFromHsvComponents(); }), this::updateEditWidgets));
        this.rSlider          = new HorizontalColorSliderWidget(92, 14, () -> (this.color & 0xFF00FFFF) | 0xFF000000, () -> this.color | 0xFFFF0000, new IntegerSliderCallback(new WrapperIntStorage(0, 255, () -> this.r, v -> { this.r = v; this.updateColorFromRgbComponents(); }), this::updateEditWidgets));
        this.gSlider          = new HorizontalColorSliderWidget(92, 14, () -> (this.color & 0xFFFF00FF) | 0xFF000000, () -> this.color | 0xFF00FF00, new IntegerSliderCallback(new WrapperIntStorage(0, 255, () -> this.g, v -> { this.g = v; this.updateColorFromRgbComponents(); }), this::updateEditWidgets));
        this.bSlider          = new HorizontalColorSliderWidget(92, 14, () -> (this.color & 0xFFFFFF00) | 0xFF000000, () -> this.color | 0xFF0000FF, new IntegerSliderCallback(new WrapperIntStorage(0, 255, () -> this.b, v -> { this.b = v; this.updateColorFromRgbComponents(); }), this::updateEditWidgets));
        this.aSlider          = new HorizontalColorSliderWidget(92, 14, () -> this.color & 0x00FFFFFF,                () -> this.color | 0xFF000000, new IntegerSliderCallback(new WrapperIntStorage(0, 255, () -> this.a, v -> { this.a = v; this.updateColorFromRgbComponents(); }), this::updateEditWidgets));

        this.currentSVHueSlider = new HorizontalHueSliderWidget(92, 14, () -> this.s, () -> this.v, new FloatSliderCallback(new WrapperFloatStorage(0, 1f, () -> this.h, v -> { this.h = v; this.updateColorFromHsvComponents(); }), this::updateEditWidgets));
        this.fullSVHueSlider    = new VerticalHueSliderWidget(16, 122, () -> 1f, () -> 1f, new FloatSliderCallback(new WrapperFloatStorage(0, 1f, () -> 1f - this.h, v -> { this.h = 1f - v; this.updateColorFromHsvComponents(); }), this::updateEditWidgets));

        this.svSelector = new SvSelectorWidget(104, 104, new WrapperFloatStorage(0.0F, 1.0F, () -> this.s, v -> {this.s = v; this.updateColorFromHsvComponents(); this.updateEditWidgets(); }),
                                                         new WrapperFloatStorage(0.0F, 1.0F, () -> this.v, v -> {this.v = v; this.updateColorFromHsvComponents(); this.updateEditWidgets(); }),
                                                         () -> this.h);
        this.colorIndicator = new ColorIndicatorWidget(40, 40, () -> this.color, c -> this.color = c);
        this.colorIndicator.setCanEdit(false);

        this.setColor(colorIn);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        for (IntegerEditWidget widget : this.editWidgets)
        {
            this.addWidget(widget);
        }

        this.addWidget(this.svSelector);
        this.addWidget(this.componentLabels);
        this.addWidget(this.saturationSlider);
        this.addWidget(this.valueSlider);
        this.addWidget(this.currentSVHueSlider);
        this.addWidget(this.fullSVHueSlider);
        this.addWidget(this.rSlider);
        this.addWidget(this.gSlider);
        this.addWidget(this.bSlider);
        this.addWidget(this.aSlider);

        this.addWidget(this.colorIndicator);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX() + 2;
        int y = this.getY() + 2;
        int sw = 99;
        int gap = 18;

        this.svSelector.setPosition(x + 1, y);
        x += 155;

        this.fullSVHueSlider.setPosition(x - 43, y);

        this.componentLabels.setPosition(x - 11, y + 3);
        this.componentLabels.setLineHeight(gap);

        this.currentSVHueSlider.setPosition(x, y);
        this.editH.setPosition(x + sw, y);
        y += gap;

        this.saturationSlider.setPosition(x, y);
        this.editS.setPosition(x + sw, y);
        y += gap;

        this.valueSlider.setPosition(x, y);
        this.editV.setPosition(x + sw, y);
        y += gap;

        this.rSlider.setPosition(x, y);
        this.editR.setPosition(x + sw, y);
        y += gap;

        this.gSlider.setPosition(x, y);
        this.editG.setPosition(x + sw, y);
        y += gap;

        this.bSlider.setPosition(x, y);
        this.editB.setPosition(x + sw, y);
        y += gap;

        this.aSlider.setPosition(x, y);
        this.editA.setPosition(x + sw, y);

        y += 20;
        this.editHex.setPosition(x - 23, y);

        this.colorIndicator.setPosition(this.svSelector.getX(), this.aSlider.getY());
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (this.lastAppliedColor != this.color)
        {
            this.colorConsumer.accept(this.color);
            this.lastAppliedColor = this.color;
        }
    }

    protected IntegerEditWidget createIntegerEditWidgetHsv(IntConsumer consumer,
                                                           IntSupplier supplier,
                                                           int maxValue)
    {
        IntConsumer chainedConsumer = v -> this.setHsvComponentValue(v, consumer);
        return this.createIntegerEditWidget(chainedConsumer, supplier, maxValue);
    }

    protected IntegerEditWidget createIntegerEditWidgetRgb(IntConsumer consumer,
                                                           IntSupplier supplier)
    {
        IntConsumer chainedConsumer = v -> this.setRgbComponentValue(v, consumer);
        return this.createIntegerEditWidget(chainedConsumer, supplier, 255);
    }

    protected IntegerEditWidget createIntegerEditWidget(IntConsumer chainedConsumer,
                                                        IntSupplier supplier,
                                                        int maxValue)
    {
        IntegerEditWidget widget = new IntegerEditWidget(32, 14, chainedConsumer);

        widget.setLabelFixedWidth(10);
        widget.setSupplier(supplier);
        widget.setAddValueAdjustButton(false);
        widget.setValidRange(0, maxValue);

        return widget;
    }

    protected void updateEditWidgets()
    {
        for (IntegerEditWidget widget : this.editWidgets)
        {
            // Don't update the focused widget, as that would make it annoying to try to type
            if (widget.getTextField().isFocused() == false)
            {
                widget.setValueFromSupplier();
            }
        }
    }

    public int getColor()
    {
        return this.color;
    }

    protected void setRgbComponentValue(int value, IntConsumer consumer)
    {
        consumer.accept(value);
        this.updateColorFromRgbComponents();
        this.updateEditWidgets();
    }

    protected void setHsvComponentValue(int value, IntConsumer consumer)
    {
        consumer.accept(value);
        this.updateColorFromHsvComponents();
        this.updateEditWidgets();
    }

    protected void onColorChanged()
    {
        // Update the widget hover text
        this.colorIndicator.updateWidgetState();
    }

    protected void setColorInt(int color)
    {
        this.color = color;
        this.onColorChanged();
    }

    protected void setColor(int color)
    {
        this.a = (color >> 24) & 0xFF;
        this.r = (color >> 16) & 0xFF;
        this.g = (color >>  8) & 0xFF;
        this.b = (color      ) & 0xFF;

        this.updateColorFromRgbComponents();
        this.updateEditWidgets();
    }

    protected void updateColorFromRgbComponents()
    {
        float[] hsv = new float[3];

        Color.RGBtoHSB(this.r, this.g, this.b, hsv);

        this.h = hsv[0];
        this.s = hsv[1];
        this.v = hsv[2];

        this.setColorInt((this.a << 24) | (this.r << 16) | (this.g << 8) | this.b);
    }

    protected void updateColorFromHsvComponents()
    {
        int rgb = Color.HSBtoRGB(this.h, this.s, this.v);

        this.r = (rgb >> 16) & 0xFF;
        this.g = (rgb >>  8) & 0xFF;
        this.b = (rgb      ) & 0xFF;

        this.setColorInt((this.a << 24) | (rgb & 0x00FFFFFF));
    }

    public static class SvSelectorWidget extends InteractableWidget
    {
        protected static final ShaderProgram SHADER_HUE = new ShaderProgram("malilib", null, "shaders/sv_selector.frag");

        protected final FloatStorage saturation;
        protected final FloatStorage value;
        protected final FloatSupplier hue;
        protected boolean dragging;

        public SvSelectorWidget(int width, int height, FloatStorage saturation, FloatStorage value, FloatSupplier hue)
        {
            super(width, height);

            this.canReceiveMouseClicks = true;
            this.canReceiveMouseMoves = true;
            this.saturation = saturation;
            this.value = value;
            this.hue = hue;

            this.getBorderRenderer().getNormalSettings().setBorderWidthAndColor(1, 0xFFD0D0D0);
        }

        @Override
        protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
        {
            if (mouseButton == 0)
            {
                this.dragging = true;
                this.setValuesForPosition(mouseX, mouseY);
            }

            return true;
        }

        @Override
        public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
        {
            if (mouseButton == 0)
            {
                this.dragging = false;
            }
        }

        @Override
        public boolean onMouseMoved(int mouseX, int mouseY)
        {
            if (this.dragging)
            {
                this.setValuesForPosition(mouseX, mouseY);
                return true;
            }

            return false;
        }

        protected void setValuesForPosition(int mouseX, int mouseY)
        {
            float relX = MathUtils.clamp((mouseX - this.getX() - 1) / (float) (this.getWidth() - 2), 0.0F, 1.0F);
            float relY = 1.0F - MathUtils.clamp((mouseY - this.getY() - 1) / (float) (this.getHeight() - 2), 0.0F, 1.0F);

            this.value.setFloatValue(relX);
            this.saturation.setFloatValue(relY);
        }

        @Override
        public void renderAt(int x, int y, float z, ScreenContext ctx)
        {
            super.renderAt(x, y, z, ctx);

            this.renderColor(x, y, z, ctx);
            this.renderMarker(x, y, z, ctx);
        }

        protected void renderMarker(int x, int y, float z, ScreenContext ctx)
        {
            int w = this.getWidth() - 2;
            int h = this.getHeight() - 2;

            x += 1;
            y += 1;
            z += 0.025f;

            VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();

            double x1 = x + this.value.getFloatValue() * (w - 1);
            double y1 = y + (1.0f - this.saturation.getFloatValue()) * (h - 1);
            ShapeRenderUtils.renderRectangle(x , y1, z, w, 1, 0xFFFFFFFF, builder);
            ShapeRenderUtils.renderRectangle(x1, y , z, 1, h, 0xFFFFFFFF, builder);

            builder.draw();
        }

        protected void renderColor(int x, int y, float z, ScreenContext ctx)
        {
            int w = this.getWidth() - 2;
            int h = this.getHeight() - 2;

            x += 1;
            y += 1;
            z += 0.0125f;

            VertexBuilder builder = VanillaWrappingVertexBuilder.texturedQuad();

            builder.posUv(x    , y    , z, 1.0F, 0.0F);
            builder.posUv(x    , y + h, z, 0.0F, 0.0F);
            builder.posUv(x + w, y + h, z, 0.0F, 1.0F);
            builder.posUv(x + w, y    , z, 1.0F, 1.0F);

            RenderWrap.enableBlend();
            RenderWrap.disableTexture2D();
            RenderWrap.tryBlendFuncSeparate(BlendSourceFactor.SRC_ALPHA,
                                            BlendDestFactor.ONE_MINUS_SRC_ALPHA,
                                            BlendSourceFactor.ONE,
                                            BlendDestFactor.ZERO);

            RenderWrap.disableRescaleNormal();
            RenderWrap.disableAlpha();
            RenderWrap.shadeModel(GL11.GL_SMOOTH);
            RenderWrap.alphaFunc(GL11.GL_GREATER, 0.01F);

            GL20.glUseProgram(SHADER_HUE.getProgram());
            GL20.glUniform1f(GL20.glGetUniformLocation(SHADER_HUE.getProgram(), "hue_value"), this.hue.getAsFloat());

            builder.draw();

            GL20.glUseProgram(0);
            RenderWrap.shadeModel(GL11.GL_FLAT);
        }
    }
}
