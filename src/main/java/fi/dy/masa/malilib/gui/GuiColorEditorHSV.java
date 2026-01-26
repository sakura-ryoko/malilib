package fi.dy.masa.malilib.gui;

import java.awt.*;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import org.apache.commons.lang3.math.Fraction;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.platform.NativeImage;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.IConfigColor;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.gui.wrappers.TextFieldType;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.element.*;
import fi.dy.masa.malilib.util.KeyCodes;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiColorEditorHSV extends GuiDialogBase
{
    protected final IConfigColor config;
    @Nullable protected final IDialogHandler dialogHandler;
    @Nullable protected Element clickedElement;
    @Nullable protected Element currentTextInputElement;
    protected GuiTextFieldGeneric textFieldFullColor;
    protected GuiTextFieldGeneric textFieldH;
    protected GuiTextFieldGeneric textFieldS;
    protected GuiTextFieldGeneric textFieldV;
    protected GuiTextFieldGeneric textFieldR;
    protected GuiTextFieldGeneric textFieldG;
    protected GuiTextFieldGeneric textFieldB;
    protected GuiTextFieldGeneric textFieldA;
    protected boolean mouseDown;
    protected int color;
    protected int xHS;
    protected int yHS;
    protected int xHFullSV;
    protected int xH;
    protected int yH;
    protected int sizeHS;
    protected int widthHFullSV;
    protected int widthSlider;
    protected int heightSlider;
    protected int gapSlider;
    protected float relH;
    protected float relS;
    protected float relV;
    protected float relR;
    protected float relG;
    protected float relB;
    protected float relA;
    private Pair<Identifier, DynamicTexture> dynamicTexture = null;

    public GuiColorEditorHSV(IConfigColor config, @Nullable IDialogHandler dialogHandler, Screen parent)
    {
        this.config = config;
        this.dialogHandler = dialogHandler;

        // When we have a dialog handler, then we are inside the Liteloader config menu.
        // In there we don't want to use the normal "GUI replacement and render parent first" trick.
        // The "dialog handler" stuff is used within the Liteloader config menus,
        // because there we can't change the mc.currentScreen reference to this GUI,
        // because otherwise Liteloader will freak out.
        // So instead we are using a weird wrapper "sub panel" thingy in there, and thus
        // we can NOT try to render the parent GUI here in that case, otherwise it will
        // lead to an infinite recursion loop and a StackOverflowError.
        if (this.dialogHandler == null)
        {
            this.setParent(parent);
        }

        this.title = StringUtils.translate("malilib.gui.title.color_editor");

        this.setWidthAndHeight(300, 180);
        this.centerOnScreen();

        this.init(this.dialogWidth, this.dialogHeight);
    }

    @Override
    public void setPosition(int left, int top)
    {
        super.setPosition(left, top);

        this.xHS = this.dialogLeft + 6;
        this.yHS = this.dialogTop + 24;
        this.xH = this.dialogLeft + 160;
        this.yH = this.dialogTop + 24;
        this.xHFullSV = this.xHS + 110;
        this.sizeHS = 102;
        this.widthHFullSV = 16;
        this.widthSlider = 90;
        this.heightSlider = 12;
        this.gapSlider = 6;
    }

    @Override
    public void initGui()
    {
        this.clearElements();

        int xLabel = this.dialogLeft + 148;
        int xTextField = xLabel + 110;
        int y = this.dialogTop + 24;

        y += this.createComponentElements(xTextField, y, xLabel, Element.H);
        y += this.createComponentElements(xTextField, y, xLabel, Element.S);
        y += this.createComponentElements(xTextField, y, xLabel, Element.V);
        y += this.createComponentElements(xTextField, y, xLabel, Element.R);
        y += this.createComponentElements(xTextField, y, xLabel, Element.G);
        y += this.createComponentElements(xTextField, y, xLabel, Element.B);
        y += this.createComponentElements(xTextField, y, xLabel, Element.A);

        this.addLabel(this.xH - 26, y + 3, 12, 12, 0xFFFFFFFF, "HEX:");
        this.textFieldFullColor = new GuiTextFieldGeneric(this.xH, y + 1, 68, 14, this.font);
        this.textFieldFullColor.setMaxLength(12);
        this.addTextField(this.textFieldFullColor, new TextFieldListener(null, this), TextFieldType.STRING.setMaxLength(12));

        //String str = StringUtils.translate("malilib.gui.label.color_editor.current_color");
        //this.addLabel(this.xHS, this.yHS + this.sizeHS + 10, 60, 12, 0xFFFFFF, str);

        this.setColor(this.config.getIntegerValue()); // Set the text field values
    }

    protected int createComponentElements(int x, int y, int xLabel, Element element)
    {
        TextFieldListener listener = new TextFieldListener(element, this);
        GuiTextFieldInteger textField = new GuiTextFieldInteger(x, y, 32, 12, this.font);

        switch (element)
        {
            case H: this.textFieldH = textField; break;
            case S: this.textFieldS = textField; break;
            case V: this.textFieldV = textField; break;
            case R: this.textFieldR = textField; break;
            case G: this.textFieldG = textField; break;
            case B: this.textFieldB = textField; break;
            case A: this.textFieldA = textField; break;
            default:
        }

        this.addLabel(xLabel, y, 12, 12, 0xFFFFFFFF, element.name() + ":");
        this.addTextField(textField, listener, TextFieldType.INTEGER);

        return this.heightSlider + this.gapSlider;
    }

    @Override
    public void removed()
    {
        this.config.setIntegerValue(this.color);

        this.clearDynamicTexture();
        super.removed();
    }

    @Override
    public void render(@NotNull GuiGraphics drawContext, int mouseX, int mouseY, float partialTicks)
    {
        if (this.getParent() != null)
        {
            this.getParent().render(drawContext, mouseX, mouseY, partialTicks);
        }

        super.render(drawContext, mouseX, mouseY, partialTicks);

        if (this.mouseDown)
        {
            if (this.clickedElement != null)
            {
                this.updateColorFromMouseInput(this.clickedElement, mouseX, mouseY);
            }
        }

        this.drawColorSelector(GuiContext.fromGuiGraphics(drawContext), mouseX, mouseY);
    }

    @Override
    protected void drawScreenBackground(GuiContext ctx, int mouseX, int mouseY)
    {
        RenderUtils.drawOutlinedBox(ctx, this.dialogLeft, this.dialogTop, this.dialogWidth, this.dialogHeight, 0xFF000000, COLOR_HORIZONTAL_BAR);
    }

    @Override
    protected void drawTitle(GuiContext ctx, int mouseX, int mouseY, float partialTicks)
    {
        this.drawStringWithShadow(ctx, this.title, this.dialogLeft + 10, this.dialogTop + 6, COLOR_WHITE);
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent input)
    {
        return this.onKeyTyped(input);
    }

    @Override
    public boolean onKeyTyped(KeyEvent input)
    {
        if (input.key() == KeyCodes.KEY_ESCAPE && this.dialogHandler != null)
        {
            this.dialogHandler.closeDialog();
            return true;
        }
        else
        {
            return super.onKeyTyped(input);
        }
    }

    @Override
    public boolean onMouseClicked(MouseButtonEvent click, boolean doubleClick)
    {
        this.clickedElement = this.getHoveredElement((int) click.x(), (int) click.y());

        if (this.clickedElement != null)
        {
            this.mouseDown = true;
            this.updateColorFromMouseInput(this.clickedElement, (int) click.x(), (int) click.y());
        }

        return super.onMouseClicked(click, doubleClick);
    }

    @Override
    public boolean onMouseReleased(MouseButtonEvent click)
    {
        this.mouseDown = false;
        this.clickedElement = null;
        return super.onMouseReleased(click);
    }

    protected float[] getCurrentColorHSV()
    {
        return this.getColorHSV(this.color);
    }

    protected float[] getColorHSV(int color)
    {
        int r = ((color >>> 16) & 0xFF);
        int g = ((color >>>  8) & 0xFF);
        int b = ( color         & 0xFF);

        float[] hsv = new float[3];
        Color.RGBtoHSB(r, g, b, hsv);

        return hsv;
    }

    protected void setColor(int color)
    {
        this.color = color;

        this.relA = ((color & 0xFF000000) >>> 24) / 255f;

        this.setHSVFromRGB(color);
        this.setRGBFromHSV();

        this.currentTextInputElement = null;
    }

    protected void setHSVFromRGB()
    {
        this.setHSVFromRGB(this.relR, this.relG, this.relB);
    }

    protected void setHSVFromRGB(float r, float g, float b)
    {
        float[] hsv = new float[3];

        int ri = (int) (r * 255f);
        int gi = (int) (g * 255f);
        int bi = (int) (b * 255f);
        int ai = (int) (this.relA * 255f);

        Color.RGBtoHSB(ri, gi, bi, hsv);

        this.relH = hsv[0];
        this.relS = hsv[1];
        this.relV = hsv[2];

        this.color = (ai << 24) | (ri << 16) | (gi << 8) | bi;

        this.updateTextFieldsHSV(this.relH, this.relS, this.relV);
    }

    protected void setHSVFromRGB(int rgb)
    {
        float[] hsv = this.getColorHSV(rgb);

        this.relH = hsv[0];
        this.relS = hsv[1];
        this.relV = hsv[2];

        this.updateTextFieldsHSV(this.relH, this.relS, this.relV);
    }

    protected void setRGBFromHSV()
    {
        this.setRGBFromHSV(this.relH, this.relS, this.relV);
    }

    protected void setRGBFromHSV(float h, float s, float v)
    {
        int rgb = Color.HSBtoRGB(h, s, v);
        int ai = (int) (this.relA * 255f);

        this.color = (ai << 24) | (rgb & 0x00FFFFFF);

        this.relR = (float) ((rgb >>> 16) & 0xFF) / 255f;
        this.relG = (float) ((rgb >>>  8) & 0xFF) / 255f;
        this.relB = (float) ((rgb       ) & 0xFF) / 255f;

        this.updateTextFieldsRGB();
    }

    protected void updateColorFromMouseInput(Element element, int mouseX, int mouseY)
    {
        if (element == Element.SV)
        {
            mouseX = Mth.clamp(mouseX, this.xHS, this.xHS + this.sizeHS);
            mouseY = Mth.clamp(mouseY, this.yHS, this.yHS + this.sizeHS);
            int relX = mouseX - this.xHS;
            int relY = mouseY - this.yHS;
            float saturation = 1f - ((float) relY / (float) this.sizeHS);
            float value = (float) relX / (float) this.sizeHS;

            this.relS = saturation;
            this.relV = value;

            this.setRGBFromHSV();
            this.updateTextField(Element.S);
            this.updateTextField(Element.V);
        }
        else if (element == Element.H_FULL_SV)
        {
            mouseY = Mth.clamp(mouseY, this.yHS, this.yHS + this.sizeHS);
            int relY = mouseY - this.yHS;
            float hue = 1f - ((float) relY / (float) this.sizeHS);

            this.relH = hue;
            this.setRGBFromHSV();
            this.updateTextField(Element.H);
        }
        else
        {
            mouseX = Mth.clamp(mouseX, this.xH, this.xH + this.widthSlider);
            int relX = mouseX - this.xH;
            float relVal = (float) relX / (float) this.widthSlider;

            switch (element)
            {
                case H:
                {
                    this.relH = relVal;
                    this.setRGBFromHSV();
                    this.updateTextField(Element.H);
                    break;
                }
                case S:
                {
                    this.relS = relVal;
                    this.setRGBFromHSV();
                    this.updateTextField(Element.S);
                    break;
                }
                case V:
                {
                    this.relV = relVal;
                    this.setRGBFromHSV();
                    this.updateTextField(Element.V);
                    break;
                }
                case R:
                {
                    this.relR = relVal;
                    this.setHSVFromRGB();
                    this.updateTextField(Element.R);
                    break;
                }
                case G:
                {
                    this.relG = relVal;
                    this.setHSVFromRGB();
                    this.updateTextField(Element.G);
                    break;
                }
                case B:
                {
                    this.relB = relVal;
                    this.setHSVFromRGB();
                    this.updateTextField(Element.B);
                    break;
                }
                case A:
                {
                    this.relA = relVal;
                    this.setHSVFromRGB();
                    this.updateTextField(Element.A);
                    break;
                }
                default:
            }
        }
    }

    protected void updateTextFieldsHSV(float h, float s, float v)
    {
        this.updateTextField(Element.HEX);
        this.updateTextField(Element.H);
        this.updateTextField(Element.S);
        this.updateTextField(Element.V);
    }

    protected void updateTextFieldsRGB()
    {
        this.updateTextField(Element.HEX);
        this.updateTextField(Element.R);
        this.updateTextField(Element.G);
        this.updateTextField(Element.B);
        this.updateTextField(Element.A);
    }

    protected void updateTextField(Element type)
    {
        // Don't update the text field that is currently being written into, as that would
        // make it impossible to type in properly
        if (this.currentTextInputElement != type)
        {
            switch (type)
            {
                case HEX:
                    this.textFieldFullColor.setValue(String.format("#%08X", this.color));
                    break;

                case H:
                    this.textFieldH.setValue(String.valueOf((int) (this.relH * 360)));
                    break;

                case S:
                    this.textFieldS.setValue(String.valueOf((int) (this.relS * 100)));
                    break;

                case V:
                    this.textFieldV.setValue(String.valueOf((int) (this.relV * 100)));
                    break;

                case R:
                    this.textFieldR.setValue(String.valueOf((int) (this.relR * 255)));
                    break;

                case G:
                    this.textFieldG.setValue(String.valueOf((int) (this.relG * 255)));
                    break;

                case B:
                    this.textFieldB.setValue(String.valueOf((int) (this.relB * 255)));
                    break;

                case A:
                    this.textFieldA.setValue(String.valueOf((int) (this.relA * 255)));
                    break;

                default:
            }
        }
    }

    protected void drawColorSelector(GuiContext ctx, int mouseX, int mouseY)
    {
        if (this.minecraft == null) return;

        int x = this.xH - 1;
        int y = this.yH - 1;
        int w = this.widthSlider + 2;
        int h = this.heightSlider + 2;
        int z = 0;
        int yd = this.heightSlider + this.gapSlider;
        int cx = this.xHS;
        int cy = this.yHS + this.sizeHS + 8;
//        int cw = this.sizeHS;
        int cw = 32;
        int ch = 32;

        RenderUtils.drawOutline(ctx, x, y, w, h, 0xC0FFFFFF); // H
        y += yd;
        RenderUtils.drawOutline(ctx, x, y, w, h, 0xC0FFFFFF); // S
        y += yd;
        RenderUtils.drawOutline(ctx, x, y, w, h, 0xC0FFFFFF); // V
        y += yd;
        RenderUtils.drawOutline(ctx, x, y, w, h, 0xC0FFFFFF); // R
        y += yd;
        RenderUtils.drawOutline(ctx, x, y, w, h, 0xC0FFFFFF); // G
        y += yd;
        RenderUtils.drawOutline(ctx, x, y, w, h, 0xC0FFFFFF); // B
        y += yd;
        RenderUtils.drawOutline(ctx, x, y, w, h, 0xC0FFFFFF); // A

        x = this.xHS;
        y = this.yHS;
        w = this.sizeHS;
        h = this.sizeHS;

        RenderUtils.drawOutline(ctx, x - 1, y - 1, w + 2, h + 2, 0xC0FFFFFF);                      // main color selector
        RenderUtils.drawOutline(ctx, cx - 1, cy - 1, cw + 2, ch + 2, 0xC0FFFFFF);                  // current color indicator
        RenderUtils.drawOutline(ctx, this.xHFullSV, y - 1, this.widthHFullSV, this.sizeHS + 2, 0xC0FFFFFF); // Hue vertical/full value

        // Full SV Square --
        int r = (int) (this.relR * 255f);
        int g = (int) (this.relG * 255f);
        int b = (int) (this.relB * 255f);
        int a = 255;
        int c = 255;

        /*
        GlProgramManager.useProgram(SHADER_HUE.getProgram());
        GL20.glUniform1f(GL20.glGetUniformLocation(SHADER_HUE.getProgram(), "hue_value"), this.relH);
         */

//        final int[] colorPair = this.getColorPairForSelector();
        this.generateDynamicTextureForHSVSelector();

        ctx.addSimpleElement(new MaLiLibTexturedGuiElement(
                RenderPipelines.GUI_TEXTURED,
                TextureSetup.singleTexture(
                        this.dynamicTexture.getRight().getTextureView(),
                        this.dynamicTexture.getRight().getSampler()
                ),
                new Matrix3x2f(ctx.pose()),
                x, y,
                x + w, y + h,
                0, 256 * 0.00390625F,
                0, 256 * 0.00390625F,
                -1,
                ctx.peekLastScissor()
        ));

//	    ctx.addSimpleElement(new MaLiLibHSV4ColorGradientGuiElement(
//				RenderPipelines.GUI,
//				TextureSetup.noTexture(),
//				new Matrix3x2f(ctx.pose()),
//				x, x + w, y, y + h,
//				colorPair,
//				ctx.peekLastScissor())
//	    );

        // Element Selectors --
        // Current color indicator
	    ctx.addSimpleElement(new MaLiLibHSV1ColorIndicatorGuiElement(
				RenderPipelines.GUI,
				TextureSetup.noTexture(),
				new Matrix3x2f(ctx.pose()),
				cx, cx + cw,
				cy, cy + ch,
				r, g, b, a,
				ctx.peekLastScissor())
        );

        // SV selection marker for saturation, horizontal marker, vertical range
        int yt = y + (int) ((1 - this.relS) * h);

        // SV selection marker for saturation, horizontal marker, vertical range
	    ctx.addSimpleElement(new MaLiLibHSV1ColorIndicatorGuiElement(
				RenderPipelines.GUI,
				TextureSetup.noTexture(),
				new Matrix3x2f(ctx.pose()),
				x - 1, x + w + 1,
				yt, yt + 1,
				c, c, c, a,
				ctx.peekLastScissor())
        );

        // SV selection marker for value, vertical marker, horizontal range
        int xt = x + (int) (this.relV * w);

        // SV selection marker for value, vertical marker, horizontal range
	    ctx.addSimpleElement(new MaLiLibHSV1ColorIndicatorGuiElement(
				RenderPipelines.GUI,
				TextureSetup.noTexture(),
				new Matrix3x2f(ctx.pose()),
				xt, xt + 1,
				y - 1, y + h + 1,
				c, c, c, a,
				ctx.peekLastScissor())
        );

        x = this.xH;
        w = this.widthSlider;
        h = this.heightSlider;
        yd = this.heightSlider + this.gapSlider;

        // Full value Saturation & Value, Hue slider
        renderHueBarVertical(ctx, this.xHFullSV + 1, this.yHS, z, this.widthHFullSV - 2, this.sizeHS, 1f, 1f);
        renderBarMarkerVerticalBar(ctx, this.xHFullSV, this.yHS, z, this.widthHFullSV, this.sizeHS, this.relH);

        // Hue slider
        renderHueBarHorizontal(ctx, x, y, z, w, h, this.relS, this.relV);
        renderBarMarkerHorizontalBar(ctx, x, y, z, w, h, this.relH);
        y += yd;

        // Saturation slider
        int color1 = Color.HSBtoRGB(this.relH, 0, this.relV);
        int color2 = Color.HSBtoRGB(this.relH, 1, this.relV);
        renderGradientColorBar(ctx, x, y, z, w, h, color1, color2);
        renderBarMarkerHorizontalBar(ctx, x, y, z, w, h, this.relS);
        y += yd;

        // Value/Brightness slider
        color1 = Color.HSBtoRGB(this.relH, this.relS, 0);
        color2 = Color.HSBtoRGB(this.relH, this.relS, 1);
        renderGradientColorBar(ctx, x, y, z, w, h, color1, color2);
        renderBarMarkerHorizontalBar(ctx, x, y, z, w, h, this.relV);
        y += yd;

        // Red slider
        color1 = (this.color & 0xFF00FFFF) | 0xFF000000;
        color2 = this.color | 0xFFFF0000;
        renderGradientColorBar(ctx, x, y, z, w, h, color1, color2);
        renderBarMarkerHorizontalBar(ctx, x, y, z, w, h, (float) r / 255f);
        y += yd;

        // Green slider
        color1 = (this.color & 0xFFFF00FF) | 0xFF000000;
        color2 = this.color | 0xFF00FF00;
        renderGradientColorBar(ctx, x, y, z, w, h, color1, color2);
        renderBarMarkerHorizontalBar(ctx, x, y, z, w, h, (float) g / 255f);
        y += yd;

        // Blue slider
        color1 = (this.color & 0xFFFFFF00) | 0xFF000000;
        color2 = this.color | 0xFF0000FF;
        renderGradientColorBar(ctx, x, y, z, w, h, color1, color2);
        renderBarMarkerHorizontalBar(ctx, x, y, z, w, h, (float) b / 255f);
        y += yd;

        // Alpha slider
        a = (int) (this.relA * 255f);
        color1 = this.color & 0x00FFFFFF;
        color2 = this.color | 0xFF000000;
        renderGradientColorBar(ctx, x, y, z, w, h, color1, color2);
        renderBarMarkerHorizontalBar(ctx, x, y, z, w, h, (float) a / 255f);
        y += yd;
    }

    @Deprecated
    private int[] getColorPairForSelector()
    {
        int color1 = Color.HSBtoRGB(this.relH, 0f, 0f);     // TOP LEFT
        int color2 = Color.HSBtoRGB(this.relH, 1f, 0f);     // TOP RIGHT
        int color3 = Color.HSBtoRGB(this.relH, 0f, 1f);     // BOTTOM RIGHT
        int color4 = Color.HSBtoRGB(this.relH, 1f, 1f);     // BOTTOM LEFT

        return new int[]{ color1, color2, color3, color4 };
    }

    /**
     * This Generates a Dynamic Image for the 4-Point Color Selector;
     * - as opposed to using a custom Fragment shader.
     */
    private void generateDynamicTextureForHSVSelector()
    {
        final int sizeW = 256;
        final int sizeH = 256;

        if (this.dynamicTexture != null)
        {
            // for 1.21.5+ we need to destroy the last texture
            this.clearDynamicTexture();
        }

        try (NativeImage image = new NativeImage(sizeW, sizeH, false))
        {
            Identifier id = Identifier.fromNamespaceAndPath(MaLiLibReference.MOD_ID, UUID.randomUUID().toString());
            this.dynamicTexture = Pair.of(
                    id,
                    new DynamicTexture(id::toString, image)
            );
            this.mc.getTextureManager().register(id, this.dynamicTexture.getRight());

            for (int x = 0; x < sizeW; x++)
            {
                float brightness = Fraction.getFraction(x, sizeW).floatValue();

                for (int y = 0; y < sizeH; y++)
                {
                    float saturation = Fraction.getFraction(y, sizeH).floatValue();

                    // inverted Y (?)
                    image.setPixel(x, ((sizeH - 1) - y), Color.HSBtoRGB(this.relH, saturation, brightness));
                }
            }

//            if (MaLiLibReference.DEBUG_MODE)
//            {
//                Path dir = MaLiLibReference.CONFIG_DIR.resolve(id.getNamespace());
//                FileUtils.createDirectoriesIfMissing(dir);
//                Path file = dir.resolve(id.getPath() + ".png");
//                image.writeToFile(file);
//            }

            this.dynamicTexture.getRight().upload();
        }
        catch (Throwable err)
        {
            MaLiLib.LOGGER.error("GuiColorEditorHSV: generate native image failed; {}", err.getLocalizedMessage());
        }
    }

    private void clearDynamicTexture()
    {
        if (this.dynamicTexture != null)
        {
            this.mc.getTextureManager().release(this.dynamicTexture.getLeft());
            this.dynamicTexture.getRight().close();
            this.dynamicTexture = null;
        }
    }

    public static void renderGradientColorBar(GuiContext ctx, int x, int y, float z, int width, int height, int colorStart, int colorEnd)
    {
	    ctx.addSimpleElement(new MaLiLibHSV2ColorGradientGuiElement(
				RenderPipelines.GUI,
				TextureSetup.noTexture(),
				new Matrix3x2f(ctx.pose()),
				x, x + width,
				y, y + height,
				colorStart, colorEnd,
				ctx.peekLastScissor())
        );
    }

    public static void renderHueBarHorizontal(GuiContext ctx, int x, int y, float z, int width, int height, float saturation, float value)
    {
        renderHueBar(ctx, x, y, z, 0, height, width / 6, 0, saturation, value);
    }

    public static void renderHueBarVertical(GuiContext ctx, int x, int y, float z, int width, int height, float saturation, float value)
    {
        y = y + height - height / 6;
        renderHueBar(ctx, x, y, z, width, 0, 0, height / 6, saturation, value);
    }

    public static void renderHueBar(GuiContext ctx, int x, int y, float z, int width, int height, int segmentWidth, int segmentHeight, float saturation, float value)
    {
        int color1 = Color.HSBtoRGB(0f   , saturation, value);
        int color2 = Color.HSBtoRGB(1f/6f, saturation, value);
        renderHueBarSegment(ctx, x, y, z, width, height, segmentWidth, segmentHeight, color1, color2);
        x += segmentWidth;
        y -= segmentHeight;

        color1 = Color.HSBtoRGB(1f/6f, saturation, value);
        color2 = Color.HSBtoRGB(2f/6f, saturation, value);
        renderHueBarSegment(ctx, x, y, z, width, height, segmentWidth, segmentHeight, color1, color2);
        x += segmentWidth;
        y -= segmentHeight;

        color1 = Color.HSBtoRGB(2f/6f, saturation, value);
        color2 = Color.HSBtoRGB(3f/6f, saturation, value);
        renderHueBarSegment(ctx, x, y, z, width, height, segmentWidth, segmentHeight, color1, color2);
        x += segmentWidth;
        y -= segmentHeight;

        color1 = Color.HSBtoRGB(3f/6f, saturation, value);
        color2 = Color.HSBtoRGB(4f/6f, saturation, value);
        renderHueBarSegment(ctx, x, y, z, width, height, segmentWidth, segmentHeight, color1, color2);
        x += segmentWidth;
        y -= segmentHeight;

        color1 = Color.HSBtoRGB(4f/6f, saturation, value);
        color2 = Color.HSBtoRGB(5f/6f, saturation, value);
        renderHueBarSegment(ctx, x, y, z, width, height, segmentWidth, segmentHeight, color1, color2);
        x += segmentWidth;
        y -= segmentHeight;

        color1 = Color.HSBtoRGB(5f/6f, saturation, value);
        color2 = Color.HSBtoRGB(6f/6f, saturation, value);
        renderHueBarSegment(ctx, x, y, z, width, height, segmentWidth, segmentHeight, color1, color2);
    }

    public static void renderHueBarSegment(GuiContext ctx, int x, int y, float z, int width, int height,
            int segmentWidth, int segmentHeight, int color1, int color2)
    {
	    ctx.addSimpleElement(new MaLiLibHSV2ColorSegmentedHueGuiElement(
				RenderPipelines.GUI,
				TextureSetup.noTexture(),
				new Matrix3x2f(ctx.pose()),
				x, y,
				width, height,
				segmentWidth, segmentHeight,
				color1, color2,
				ctx.peekLastScissor())
        );
    }

    public static void renderHSSelector(GuiContext ctx, int xStart, int yStart, float z, int width, int height, float hue)
    {
	    ctx.addSimpleElement(new MaLiLibHSVColorSelectorGuiElement(
				RenderPipelines.GUI,
				TextureSetup.noTexture(),
				new Matrix3x2f(ctx.pose()),
				xStart, yStart,
				width, height,
				hue,
				ctx.peekLastScissor())
        );
    }

    public static void renderBarMarkerHorizontalBar(GuiContext ctx, int x, int y, float z, int barWidth, int barHeight, float value)
    {
	    ctx.addSimpleElement(new MaLiLibHSVColorHorizontalBarMarkerGuiElement(
				RenderPipelines.GUI,
				TextureSetup.noTexture(),
				new Matrix3x2f(ctx.pose()),
				x, y,
				barWidth, barHeight,
				value,
				ctx.peekLastScissor())
        );
    }

    public static void renderBarMarkerVerticalBar(GuiContext ctx, int x, int y, float z, int barWidth, int barHeight, float value)
    {
	    ctx.addSimpleElement(new MaLiLibHSVColorVerticalBarMarkerGuiElement(
				RenderPipelines.GUI,
				TextureSetup.noTexture(),
				new Matrix3x2f(ctx.pose()),
				x, y,
				barWidth, barHeight,
				value,
				ctx.peekLastScissor())
        );
    }

    @Nullable
    protected Element getHoveredElement(int mouseX, int mouseY)
    {
        if (mouseX >= this.xHS && mouseX <= this.xHS + this.sizeHS &&
            mouseY >= this.yHS && mouseY <= this.yHS + this.sizeHS)
        {
            return Element.SV;
        }
        else if (mouseX >= this.xHFullSV && mouseX <= this.xHFullSV + this.widthHFullSV &&
                 mouseY >= this.yHS && mouseY <= this.yHS + this.sizeHS)
        {
            return Element.H_FULL_SV;
        }
        else if (mouseX >= this.xH && mouseX <= this.xH + this.widthSlider)
        {
            int h = this.heightSlider + this.gapSlider;

            if (mouseY >= this.yH && mouseY <= this.yH + h * 7 - this.gapSlider)
            {
                int relY = mouseY - this.yH;
                int index = relY / h;

                if (index < 7 && (relY % h) < this.heightSlider)
                {
                    return Element.values()[index];
                }
            }
        }

        return null;
    }

	protected record TextFieldListener(@Nullable Element type,
	                                   GuiColorEditorHSV gui) implements ITextFieldListener<GuiTextFieldGeneric>
	{
		@Override
		public boolean onTextChange(GuiTextFieldGeneric textField)
		{
			int colorOld = this.gui.color;

			// Entire color code
			if (this.type == null)
			{
				this.gui.currentTextInputElement = Element.HEX;
				this.gui.setColor(StringUtils.getColor(textField.getValue(), colorOld));
			}
			else
			{
				try
				{
					int val = Integer.parseInt(textField.getValue());
					float[] hsv = this.gui.getCurrentColorHSV();
					int colorNew = colorOld;

					switch (this.type)
					{
						case H:
							val = Mth.clamp(val, 0, 360);
							float h = (float) val / 360f;
							colorNew = Color.HSBtoRGB(h, hsv[1], hsv[2]);
							break;
						case S:
							val = Mth.clamp(val, 0, 100);
							float s = (float) val / 100f;
							colorNew = Color.HSBtoRGB(hsv[0], s, hsv[2]);
							break;
						case V:
							val = Mth.clamp(val, 0, 100);
							float v = (float) val / 100f;
							colorNew = Color.HSBtoRGB(hsv[0], hsv[1], v);
							break;
						case R:
							val = Mth.clamp(val, 0, 255);
							colorNew = (colorOld & 0x00FFFF) | (val << 16);
							break;
						case G:
							val = Mth.clamp(val, 0, 255);
							colorNew = (colorOld & 0xFF00FF) | (val << 8);
							break;
						case B:
							val = Mth.clamp(val, 0, 255);
							colorNew = (colorOld & 0xFFFF00) | val;
							break;
						case A:
							val = Mth.clamp(val, 0, 255);
							colorNew = (colorOld & 0x00FFFFFF) | (val << 24);
							break;
						default:
							return false;
					}

					if (colorNew != colorOld)
					{
						this.gui.currentTextInputElement = this.type;
						this.gui.setColor(colorNew);
					}

					return true;
				}
				catch (Exception ignored)
				{
				}
			}

			return false;
		}
	}

    protected enum Element
    {
        // NOTE: The individual H, S, V, R, G, B values are used by their index in getHoveredElement()
        // So the compound/other types must come after them.
        H,
        S,
        V,
        R,
        G,
        B,
        A,
        SV,
        H_FULL_SV,
        HEX
    }
}
