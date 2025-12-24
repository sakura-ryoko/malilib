package fi.dy.masa.malilib.gui;

import java.awt.*;
import java.nio.file.Path;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import org.apache.commons.lang3.math.Fraction;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix3x2f;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.IConfigColor;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.element.*;
import fi.dy.masa.malilib.util.FileUtils;
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
    private Pair<Identifier, NativeImageBackedTexture> dynamicTexture = null;

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

        this.init(this.mc, this.dialogWidth, this.dialogHeight);
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
        this.textFieldFullColor = new GuiTextFieldGeneric(this.xH, y + 1, 68, 14, this.textRenderer);
        this.textFieldFullColor.setMaxLength(12);
        this.addTextField(this.textFieldFullColor, new TextFieldListener(null, this));

        //String str = StringUtils.translate("malilib.gui.label.color_editor.current_color");
        //this.addLabel(this.xHS, this.yHS + this.sizeHS + 10, 60, 12, 0xFFFFFF, str);

        this.setColor(this.config.getIntegerValue()); // Set the text field values
    }

    protected int createComponentElements(int x, int y, int xLabel, Element element)
    {
        TextFieldListener listener = new TextFieldListener(element, this);
        GuiTextFieldInteger textField = new GuiTextFieldInteger(x, y, 32, 12, this.textRenderer);

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
        this.addTextField(textField, listener);

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
    public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks)
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

        this.drawColorSelector(drawContext, mouseX, mouseY);
    }

    @Override
    protected void drawScreenBackground(DrawContext drawContext, int mouseX, int mouseY)
    {
        RenderUtils.drawOutlinedBox(drawContext, this.dialogLeft, this.dialogTop, this.dialogWidth, this.dialogHeight, 0xFF000000, COLOR_HORIZONTAL_BAR);
    }

    @Override
    protected void drawTitle(DrawContext drawContext, int mouseX, int mouseY, float partialTicks)
    {
        this.drawStringWithShadow(drawContext, this.title, this.dialogLeft + 10, this.dialogTop + 6, COLOR_WHITE);
    }

    @Override
    public boolean keyPressed(KeyInput input)
    {
        return this.onKeyTyped(input);
    }

    @Override
    public boolean onKeyTyped(KeyInput input)
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
    public boolean onMouseClicked(Click click, boolean doubleClick)
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
    public boolean onMouseReleased(Click click)
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
            mouseX = MathHelper.clamp(mouseX, this.xHS, this.xHS + this.sizeHS);
            mouseY = MathHelper.clamp(mouseY, this.yHS, this.yHS + this.sizeHS);
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
            mouseY = MathHelper.clamp(mouseY, this.yHS, this.yHS + this.sizeHS);
            int relY = mouseY - this.yHS;
            float hue = 1f - ((float) relY / (float) this.sizeHS);

            this.relH = hue;
            this.setRGBFromHSV();
            this.updateTextField(Element.H);
        }
        else
        {
            mouseX = MathHelper.clamp(mouseX, this.xH, this.xH + this.widthSlider);
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
                    this.textFieldFullColor.setText(String.format("#%08X", this.color));
                    break;

                case H:
                    this.textFieldH.setText(String.valueOf((int) (this.relH * 360)));
                    break;

                case S:
                    this.textFieldS.setText(String.valueOf((int) (this.relS * 100)));
                    break;

                case V:
                    this.textFieldV.setText(String.valueOf((int) (this.relV * 100)));
                    break;

                case R:
                    this.textFieldR.setText(String.valueOf((int) (this.relR * 255)));
                    break;

                case G:
                    this.textFieldG.setText(String.valueOf((int) (this.relG * 255)));
                    break;

                case B:
                    this.textFieldB.setText(String.valueOf((int) (this.relB * 255)));
                    break;

                case A:
                    this.textFieldA.setText(String.valueOf((int) (this.relA * 255)));
                    break;

                default:
            }
        }
    }

    protected void drawColorSelector(DrawContext drawContext, int mouseX, int mouseY)
    {
        if (this.client == null) return;

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

        RenderUtils.drawOutline(drawContext, x, y, w, h, 0xC0FFFFFF); // H
        y += yd;
        RenderUtils.drawOutline(drawContext, x, y, w, h, 0xC0FFFFFF); // S
        y += yd;
        RenderUtils.drawOutline(drawContext, x, y, w, h, 0xC0FFFFFF); // V
        y += yd;
        RenderUtils.drawOutline(drawContext, x, y, w, h, 0xC0FFFFFF); // R
        y += yd;
        RenderUtils.drawOutline(drawContext, x, y, w, h, 0xC0FFFFFF); // G
        y += yd;
        RenderUtils.drawOutline(drawContext, x, y, w, h, 0xC0FFFFFF); // B
        y += yd;
        RenderUtils.drawOutline(drawContext, x, y, w, h, 0xC0FFFFFF); // A

        x = this.xHS;
        y = this.yHS;
        w = this.sizeHS;
        h = this.sizeHS;

        RenderUtils.drawOutline(drawContext, x - 1, y - 1, w + 2, h + 2, 0xC0FFFFFF);                      // main color selector
        RenderUtils.drawOutline(drawContext, cx - 1, cy - 1, cw + 2, ch + 2, 0xC0FFFFFF);                  // current color indicator
        RenderUtils.drawOutline(drawContext, this.xHFullSV, y - 1, this.widthHFullSV, this.sizeHS + 2, 0xC0FFFFFF); // Hue vertical/full value

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

        RenderUtils.addSimpleElement(drawContext,
                                     new MaLiLibTexturedGuiElement(
                                     RenderPipelines.GUI_TEXTURED,
                                     TextureSetup.of(
                                        this.dynamicTexture.getRight().getGlTextureView()
                                    ),
                                    new Matrix3x2f(drawContext.getMatrices()),
                                    x, y,
                                    x + w, y + h,
                                    0, 256 * 0.00390625F,
                                    0, 256 * 0.00390625F,
                                    -1,
                                    RenderUtils.peekLastScissor(drawContext)
                                    ));

//        RenderUtils.addSimpleElement(drawContext,
//                                     new MaLiLibHSV4ColorGradientGuiElement(
//                                             RenderPipelines.GUI,
//                                             TextureSetup.empty(),
//                                             new Matrix3x2f(drawContext.getMatrices()),
//                                             x, x + w, y, y + h,
//                                             colorPair,
//                                             RenderUtils.peekLastScissor(drawContext))
//                                     );

        // Element Selectors --
        // Current color indicator
        RenderUtils.addSimpleElement(drawContext,
                                     new MaLiLibHSV1ColorIndicatorGuiElement(
                                             RenderPipelines.GUI,
                                             TextureSetup.empty(),
                                             new Matrix3x2f(drawContext.getMatrices()),
                                             cx, cx + cw,
                                             cy, cy + ch,
                                             r, g, b, a,
                                             RenderUtils.peekLastScissor(drawContext))
        );

        // SV selection marker for saturation, horizontal marker, vertical range
        int yt = y + (int) ((1 - this.relS) * h);

        // SV selection marker for saturation, horizontal marker, vertical range
        RenderUtils.addSimpleElement(drawContext,
                                     new MaLiLibHSV1ColorIndicatorGuiElement(
                                             RenderPipelines.GUI,
                                             TextureSetup.empty(),
                                             new Matrix3x2f(drawContext.getMatrices()),
                                             x - 1, x + w + 1,
                                             yt, yt + 1,
                                             c, c, c, a,
                                             RenderUtils.peekLastScissor(drawContext))
        );

        // SV selection marker for value, vertical marker, horizontal range
        int xt = x + (int) (this.relV * w);

        // SV selection marker for value, vertical marker, horizontal range
        RenderUtils.addSimpleElement(drawContext,
                                     new MaLiLibHSV1ColorIndicatorGuiElement(
                                             RenderPipelines.GUI,
                                             TextureSetup.empty(),
                                             new Matrix3x2f(drawContext.getMatrices()),
                                             xt, xt + 1,
                                             y - 1, y + h + 1,
                                             c, c, c, a,
                                             RenderUtils.peekLastScissor(drawContext))
        );

        x = this.xH;
        w = this.widthSlider;
        h = this.heightSlider;
        yd = this.heightSlider + this.gapSlider;

        // Full value Saturation & Value, Hue slider
        renderHueBarVertical(drawContext, this.xHFullSV + 1, this.yHS, z, this.widthHFullSV - 2, this.sizeHS, 1f, 1f);
        renderBarMarkerVerticalBar(drawContext, this.xHFullSV, this.yHS, z, this.widthHFullSV, this.sizeHS, this.relH);

        // Hue slider
        renderHueBarHorizontal(drawContext, x, y, z, w, h, this.relS, this.relV);
        renderBarMarkerHorizontalBar(drawContext, x, y, z, w, h, this.relH);
        y += yd;

        // Saturation slider
        int color1 = Color.HSBtoRGB(this.relH, 0, this.relV);
        int color2 = Color.HSBtoRGB(this.relH, 1, this.relV);
        renderGradientColorBar(drawContext, x, y, z, w, h, color1, color2);
        renderBarMarkerHorizontalBar(drawContext, x, y, z, w, h, this.relS);
        y += yd;

        // Value/Brightness slider
        color1 = Color.HSBtoRGB(this.relH, this.relS, 0);
        color2 = Color.HSBtoRGB(this.relH, this.relS, 1);
        renderGradientColorBar(drawContext, x, y, z, w, h, color1, color2);
        renderBarMarkerHorizontalBar(drawContext, x, y, z, w, h, this.relV);
        y += yd;

        // Red slider
        color1 = (this.color & 0xFF00FFFF) | 0xFF000000;
        color2 = this.color | 0xFFFF0000;
        renderGradientColorBar(drawContext, x, y, z, w, h, color1, color2);
        renderBarMarkerHorizontalBar(drawContext, x, y, z, w, h, (float) r / 255f);
        y += yd;

        // Green slider
        color1 = (this.color & 0xFFFF00FF) | 0xFF000000;
        color2 = this.color | 0xFF00FF00;
        renderGradientColorBar(drawContext, x, y, z, w, h, color1, color2);
        renderBarMarkerHorizontalBar(drawContext, x, y, z, w, h, (float) g / 255f);
        y += yd;

        // Blue slider
        color1 = (this.color & 0xFFFFFF00) | 0xFF000000;
        color2 = this.color | 0xFF0000FF;
        renderGradientColorBar(drawContext, x, y, z, w, h, color1, color2);
        renderBarMarkerHorizontalBar(drawContext, x, y, z, w, h, (float) b / 255f);
        y += yd;

        // Alpha slider
        a = (int) (this.relA * 255f);
        color1 = this.color & 0x00FFFFFF;
        color2 = this.color | 0xFF000000;
        renderGradientColorBar(drawContext, x, y, z, w, h, color1, color2);
        renderBarMarkerHorizontalBar(drawContext, x, y, z, w, h, (float) a / 255f);
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
            Identifier id = Identifier.of(MaLiLibReference.MOD_ID, UUID.randomUUID().toString());
            this.dynamicTexture = Pair.of(
                    id,
                    new NativeImageBackedTexture(id::toString, image)
            );
            this.mc.getTextureManager().registerTexture(id, this.dynamicTexture.getRight());

            for (int x = 0; x < sizeW; x++)
            {
                float brightness = Fraction.getFraction(x, sizeW).floatValue();

                for (int y = 0; y < sizeH; y++)
                {
                    float saturation = Fraction.getFraction(y, sizeH).floatValue();

                    // inverted Y (?)
                    image.setColor(x, ((sizeH - 1) - y), ColorHelper.toAbgr(Color.HSBtoRGB(this.relH, saturation, brightness)));
                }
            }

//            if (MaLiLibReference.DEBUG_MODE)
//            {
//                Path dir = MaLiLibReference.CONFIG_DIR.resolve(id.getNamespace());
//                FileUtils.createDirectoriesIfMissing(dir);
//                Path file = dir.resolve(id.getPath() + ".png");
//                image.writeTo(file);
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
            this.mc.getTextureManager().destroyTexture(this.dynamicTexture.getLeft());
            this.dynamicTexture.getRight().close();
            this.dynamicTexture = null;
        }
    }

    public static void renderGradientColorBar(DrawContext drawContext, int x, int y, float z, int width, int height, int colorStart, int colorEnd)
    {
        RenderUtils.addSimpleElement(drawContext,
                                     new MaLiLibHSV2ColorGradientGuiElement(
                                             RenderPipelines.GUI,
                                             TextureSetup.empty(),
                                             new Matrix3x2f(drawContext.getMatrices()),
                                             x, x + width,
                                             y, y + height,
                                             colorStart, colorEnd,
                                             RenderUtils.peekLastScissor(drawContext))
        );
    }

    public static void renderHueBarHorizontal(DrawContext drawContext, int x, int y, float z, int width, int height, float saturation, float value)
    {
        renderHueBar(drawContext, x, y, z, 0, height, width / 6, 0, saturation, value);
    }

    public static void renderHueBarVertical(DrawContext drawContext, int x, int y, float z, int width, int height, float saturation, float value)
    {
        y = y + height - height / 6;
        renderHueBar(drawContext, x, y, z, width, 0, 0, height / 6, saturation, value);
    }

    public static void renderHueBar(DrawContext drawContext, int x, int y, float z, int width, int height, int segmentWidth, int segmentHeight, float saturation, float value)
    {
        int color1 = Color.HSBtoRGB(0f   , saturation, value);
        int color2 = Color.HSBtoRGB(1f/6f, saturation, value);
        renderHueBarSegment(drawContext, x, y, z, width, height, segmentWidth, segmentHeight, color1, color2);
        x += segmentWidth;
        y -= segmentHeight;

        color1 = Color.HSBtoRGB(1f/6f, saturation, value);
        color2 = Color.HSBtoRGB(2f/6f, saturation, value);
        renderHueBarSegment(drawContext, x, y, z, width, height, segmentWidth, segmentHeight, color1, color2);
        x += segmentWidth;
        y -= segmentHeight;

        color1 = Color.HSBtoRGB(2f/6f, saturation, value);
        color2 = Color.HSBtoRGB(3f/6f, saturation, value);
        renderHueBarSegment(drawContext, x, y, z, width, height, segmentWidth, segmentHeight, color1, color2);
        x += segmentWidth;
        y -= segmentHeight;

        color1 = Color.HSBtoRGB(3f/6f, saturation, value);
        color2 = Color.HSBtoRGB(4f/6f, saturation, value);
        renderHueBarSegment(drawContext, x, y, z, width, height, segmentWidth, segmentHeight, color1, color2);
        x += segmentWidth;
        y -= segmentHeight;

        color1 = Color.HSBtoRGB(4f/6f, saturation, value);
        color2 = Color.HSBtoRGB(5f/6f, saturation, value);
        renderHueBarSegment(drawContext, x, y, z, width, height, segmentWidth, segmentHeight, color1, color2);
        x += segmentWidth;
        y -= segmentHeight;

        color1 = Color.HSBtoRGB(5f/6f, saturation, value);
        color2 = Color.HSBtoRGB(6f/6f, saturation, value);
        renderHueBarSegment(drawContext, x, y, z, width, height, segmentWidth, segmentHeight, color1, color2);
    }

    public static void renderHueBarSegment(DrawContext drawContext, int x, int y, float z, int width, int height,
            int segmentWidth, int segmentHeight, int color1, int color2)
    {
        RenderUtils.addSimpleElement(drawContext,
                                     new MaLiLibHSV2ColorSegmentedHueGuiElement(
                                             RenderPipelines.GUI,
                                             TextureSetup.empty(),
                                             new Matrix3x2f(drawContext.getMatrices()),
                                             x, y,
                                             width, height,
                                             segmentWidth, segmentHeight,
                                             color1, color2,
                                             RenderUtils.peekLastScissor(drawContext))
        );
    }

    public static void renderHSSelector(DrawContext drawContext, int xStart, int yStart, float z, int width, int height, float hue)
    {
        RenderUtils.addSimpleElement(drawContext,
                                     new MaLiLibHSVColorSelectorGuiElement(
                                             RenderPipelines.GUI,
                                             TextureSetup.empty(),
                                             new Matrix3x2f(drawContext.getMatrices()),
                                             xStart, yStart,
                                             width, height,
                                             hue,
                                             RenderUtils.peekLastScissor(drawContext))
        );
    }

    public static void renderBarMarkerHorizontalBar(DrawContext drawContext, int x, int y, float z, int barWidth, int barHeight, float value)
    {
        RenderUtils.addSimpleElement(drawContext,
                                     new MaLiLibHSVColorHorizontalBarMarkerGuiElement(
                                             RenderPipelines.GUI,
                                             TextureSetup.empty(),
                                             new Matrix3x2f(drawContext.getMatrices()),
                                             x, y,
                                             barWidth, barHeight,
                                             value,
                                             RenderUtils.peekLastScissor(drawContext))
        );
    }

    public static void renderBarMarkerVerticalBar(DrawContext drawContext, int x, int y, float z, int barWidth, int barHeight, float value)
    {
        RenderUtils.addSimpleElement(drawContext,
                                     new MaLiLibHSVColorVerticalBarMarkerGuiElement(
                                             RenderPipelines.GUI,
                                             TextureSetup.empty(),
                                             new Matrix3x2f(drawContext.getMatrices()),
                                             x, y,
                                             barWidth, barHeight,
                                             value,
                                             RenderUtils.peekLastScissor(drawContext))
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

    protected static class TextFieldListener implements ITextFieldListener<GuiTextFieldGeneric>
    {
        protected final GuiColorEditorHSV gui;
        @Nullable protected final Element type;

        protected TextFieldListener(@Nullable Element type, GuiColorEditorHSV gui)
        {
            this.gui = gui;
            this.type = type;
        }

        @Override
        public boolean onTextChange(GuiTextFieldGeneric textField)
        {
            int colorOld = this.gui.color;

            // Entire color code
            if (this.type == null)
            {
                this.gui.currentTextInputElement = Element.HEX;
                this.gui.setColor(StringUtils.getColor(textField.getText(), colorOld));
            }
            else
            {
                try
                {
                    int val = Integer.parseInt(textField.getText());
                    float[] hsv = this.gui.getCurrentColorHSV();
                    int colorNew = colorOld;

                    switch (this.type)
                    {
                        case H:
                            val = MathHelper.clamp(val, 0, 360);
                            float h = (float) val / 360f;
                            colorNew = Color.HSBtoRGB(h, hsv[1], hsv[2]);
                            break;
                        case S:
                            val = MathHelper.clamp(val, 0, 100);
                            float s = (float) val / 100f;
                            colorNew = Color.HSBtoRGB(hsv[0], s, hsv[2]);
                            break;
                        case V:
                            val = MathHelper.clamp(val, 0, 100);
                            float v = (float) val / 100f;
                            colorNew = Color.HSBtoRGB(hsv[0], hsv[1], v);
                            break;
                        case R:
                            val = MathHelper.clamp(val, 0, 255);
                            colorNew = (colorOld & 0x00FFFF) | (val << 16);
                            break;
                        case G:
                            val = MathHelper.clamp(val, 0, 255);
                            colorNew = (colorOld & 0xFF00FF) | (val <<  8);
                            break;
                        case B:
                            val = MathHelper.clamp(val, 0, 255);
                            colorNew = (colorOld & 0xFFFF00) | val;
                            break;
                        case A:
                            val = MathHelper.clamp(val, 0, 255);
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
                catch (Exception ignored) {}
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
