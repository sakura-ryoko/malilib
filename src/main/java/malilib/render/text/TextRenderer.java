package malilib.render.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

import malilib.render.RenderContext;
import malilib.render.ShapeRenderUtils;
import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.util.data.Color4f;
import malilib.util.data.FloatUnaryOperator;
import malilib.util.data.Identifier;
import malilib.util.game.wrap.GameWrap;
import malilib.util.game.wrap.RenderWrap;

public class TextRenderer implements IResourceManagerReloadListener
{
    public static final String VALID_ASCII_CHARACTERS = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
    public static final String VANILLA_COLOR_CODES = "0123456789abcdef";
    public static final Identifier ASCII_TEXTURE = new Identifier("textures/font/ascii.png");

    protected static final Glyph EMPTY_GLYPH = new Glyph(ASCII_TEXTURE, 0, 0, 0, 0, 4, 8, 4, true, ' ');
    protected static final Identifier[] UNICODE_PAGE_LOCATIONS = new Identifier[256];

    // This needs to be below the other static fields, because the resource manager reload will access the  other fields!
    public static final TextRenderer INSTANCE = new TextRenderer(ASCII_TEXTURE, false, false);

    protected final Random rand = new Random();
    protected final VertexBuilder textBuffer = VanillaWrappingVertexBuilder.create(32768, GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
    protected final VertexBuilder styleBuffer = VanillaWrappingVertexBuilder.create(8192, GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
    protected final Identifier asciiTexture;

    protected final Char2ObjectOpenHashMap<Glyph> glyphs = new Char2ObjectOpenHashMap<>();
    protected final HashMap<Pair<Identifier, Integer>, List<Glyph>> glyphsBySize = new HashMap<>();
    protected final byte[] glyphWidth = new byte[65536];
    protected final byte[] asciiCharacterWidths = new byte[65536];
    protected final int[] charWidth = new int[256];
    protected final int[] colorCode = new int[32];
    @Nullable protected Identifier currentFontTexture;
    protected boolean anaglyph;
    protected boolean unicode;
    protected int fontHeight = 8;
    protected int lineHeight = 10;
    protected int asciiGlyphWidth = 8;
    protected int asciiGlyphHeight = 8;

    public TextRenderer(Identifier asciiTexture, boolean unicode, boolean anaglyph)
    {
        this.asciiTexture = asciiTexture;
        this.unicode = unicode;
        this.anaglyph = anaglyph;

        this.setColorCodes(anaglyph);

        ((IReloadableResourceManager) GameWrap.getClient().getResourceManager()).registerReloadListener(this);
    }

    protected void setColorCodes(boolean anaglyph)
    {
        TextRendererUtils.setColorCodes(this.colorCode, anaglyph);
    }

    protected void readGlyphSizes()
    {
        TextRendererUtils.readGlyphSizes(this.glyphWidth);
    }

    protected void readFontTexture()
    {
        TextRendererUtils.readCharacterWidthsFromFontTexture(this.asciiTexture, this.charWidth,
                                                             this::setAsciiGlyphWidth, this::setAsciiGlyphHeight);

        int len = Math.min(VALID_ASCII_CHARACTERS.length(), this.charWidth.length);

        for (int i = 0; i < len; ++i)
        {
            char c = VALID_ASCII_CHARACTERS.charAt(i);
            this.asciiCharacterWidths[c] = (byte) this.charWidth[i];
            // generate all the glyphs so that the randomization always has all the valid alternatives available
            this.getGlyphFor(c);
        }
    }

    protected void setAsciiGlyphWidth(int width)
    {
        this.asciiGlyphWidth = width;
    }

    protected void setAsciiGlyphHeight(int height)
    {
        this.asciiGlyphHeight = height;
    }

    protected Identifier getUnicodePageLocation(int page)
    {
        if (UNICODE_PAGE_LOCATIONS[page] == null)
        {
            UNICODE_PAGE_LOCATIONS[page] = new Identifier(String.format("textures/font/unicode_page_%02x.png", page));
        }

        return UNICODE_PAGE_LOCATIONS[page];
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager)
    {
        this.unicode = GameWrap.isUnicode();
        this.anaglyph = GameWrap.getOptions().anaglyph;
        this.setColorCodes(this.anaglyph);

        StyledText.clearCache();
        this.glyphs.clear();
        this.glyphsBySize.clear();
        Arrays.fill(this.charWidth, 0);
        Arrays.fill(this.glyphWidth, (byte) 0);
        Arrays.fill(this.asciiCharacterWidths, (byte) 0);

        this.readGlyphSizes();
        this.readFontTexture();
    }

    public int getFontHeight()
    {
        return this.fontHeight;
    }

    public int getLineHeight()
    {
        return this.lineHeight;
    }

    public int getColorCode(char colorCodeChar)
    {
        int index = VANILLA_COLOR_CODES.indexOf(colorCodeChar);
        return index >= 0 && index < 16 ? this.colorCode[index] : 0;
    }

    public int getRenderWidth(String str)
    {
        return StyledText.parse(str).getRenderWidth();
    }

    public Glyph getGlyphFor(char c)
    {
        Glyph glyph = this.glyphs.get(c);

        if (glyph == null)
        {
            if (c == ' ')
            {
                glyph = EMPTY_GLYPH;
            }
            else if (c > 0 && this.unicode == false && this.asciiCharacterWidths[c] != 0)
            {
                glyph = this.generateAsciiCharacterGlyph(c);
            }
            else if (this.glyphWidth[c] != 0)
            {
                glyph = this.generateUnicodeCharacterGlyph(c);
            }
            else
            {
                glyph = EMPTY_GLYPH;
            }

            this.glyphs.put(c, glyph);

            Pair<Identifier, Integer> key = Pair.of(glyph.texture, glyph.renderWidth);
            this.glyphsBySize.computeIfAbsent(key, (k) -> new ArrayList<>()).add(glyph);
        }

        return glyph;
    }

    public List<Glyph> getRandomizedGlyphsFromSameTexture(Identifier texture, List<Glyph> originalGlyphs)
    {
        MutablePair<Identifier, Integer> key = new MutablePair<>();
        List<Glyph> glyphs = new ArrayList<>();
        key.setLeft(texture);

        for (Glyph glyph : originalGlyphs)
        {
            key.setRight(glyph.renderWidth);
            List<Glyph> list = this.glyphsBySize.get(key);

            // This seems to be a necessary precaution during resource manager reloads
            if (list != null)
            {
                glyphs.add(list.get(this.rand.nextInt(list.size())));
            }
            else
            {
                glyphs.add(glyph);
            }
        }

        return glyphs;
    }

    protected Glyph generateAsciiCharacterGlyph(char c)
    {
        int width = this.asciiCharacterWidths[c];

        // Get the index of the character on the font sheet
        int characterLocation = VALID_ASCII_CHARACTERS.indexOf(c);

        if (characterLocation == -1)
        {
            return EMPTY_GLYPH;
        }

        boolean whiteSpace = c == ' ' || c == '\t' || c == '\n';

        // 16 characters per row and column
        float u1 = (float) (characterLocation % 16) / 16.0F;
        float v1 = (float) (characterLocation / 16) / 16.0F;

        // The character widths are scaled to be in the range 0 ... 8 in
        // textRendererUtils.readCharacterWidthsFromFontTexture().
        // So the sheet here is equivalent to 128 wide, even if it's actually a different width.
        float u2 = u1 + ((float) width / 128.0F);
        float v2 = v1 + 0.0625F;

        return new Glyph(this.asciiTexture, u1, v1, u2, v2, width, 8, whiteSpace, c);
    }

    protected Glyph generateUnicodeCharacterGlyph(char c)
    {
        int data = this.glyphWidth[c] & 0xFF;

        if (data == 0)
        {
            return EMPTY_GLYPH;
        }

        // FIXME: unicode glyph and sheet width and height
        float sheetWidth = 256.0F;
        float sheetHeight = 256.0F;

        int startPos = data >>> 4;
        int endPos = data & 0xF;
        int width = endPos - startPos + 1;
        int height = (int) (sheetHeight / 16.0F);

        // 16 glyphs per row and column
        float u1 = (((c & 0x0F) * (sheetWidth / 16.0F)) + startPos) / sheetWidth;
        float v1 = (float) (c & 0xF0) / sheetWidth;
        float u2 = u1 + (float) width / sheetWidth;
        float v2 = v1 + 0.0625F;
        boolean whiteSpace = c == ' ' || c == '\t' || c == '\n';

        return new Glyph(this.getUnicodePageLocation(c >> 8), u1, v1, u2, v2,
                         width / 2, height / 2, width / 2 + 1, whiteSpace, c);
    }

    public void startBuffers()
    {
        if (this.unicode != GameWrap.isUnicode())
        {
            this.onResourceManagerReload(GameWrap.getClient().getResourceManager());
        }

        this.textBuffer.start();
        this.styleBuffer.start();
    }

    public void renderBuffers()
    {
        this.renderTextBuffer();
        this.renderStyleBuffer();

        RenderWrap.enableTexture2D();
    }

    protected void renderTextBuffer()
    {
        if (this.currentFontTexture != null)
        {
            RenderWrap.bindTexture(this.currentFontTexture);
            this.textBuffer.draw();
        }

        this.currentFontTexture = null;
    }

    protected void renderStyleBuffer()
    {
        this.styleBuffer.draw();
    }

    public void renderText(int x, int y, float z,
                           int defaultColor, boolean shadow,
                           StyledText text, RenderContext ctx)
    {
        this.renderText(x, y, z, defaultColor, shadow, this.lineHeight, text, null, ctx);
    }

    public void renderText(int x, int y, float z,
                           int defaultColor, boolean shadow, int lineHeight,
                           StyledText text, RenderContext ctx)
    {
        this.renderText(x, y, z, defaultColor, shadow, lineHeight, text, null, ctx);
    }

    /**
     * @param alphaModifier allows modifying the alpha value of the color per text segment
     */
    public void renderText(int x, int y, float z,
                           int defaultColor, boolean shadow, int lineHeight,
                           StyledText text, @Nullable FloatUnaryOperator alphaModifier, RenderContext ctx)
    {
        this.startBuffers();

        for (StyledTextLine line : text.lines)
        {
            this.renderLineToBuffer(x, y, z, defaultColor, shadow, line, alphaModifier, ctx);
            y += lineHeight;
        }

        this.renderBuffers();
    }

    public void renderLine(int x, int y, float z,
                           int defaultColor, boolean shadow,
                           StyledTextLine line, RenderContext ctx)
    {
        this.startBuffers();
        this.renderLineToBuffer(x, y, z, defaultColor, shadow, line, ctx);
        this.renderBuffers();
    }

    public void renderLineToBuffer(int x, int y, float z,
                                   int defaultColor, boolean shadow,
                                   StyledTextLine line, RenderContext ctx)
    {
        this.renderLineToBuffer(x, y, z, defaultColor, shadow, line, null, ctx);
    }

    /**
     * @param alphaModifier allows modifying the alpha value of the color per text segment
     */
    public void renderLineToBuffer(int x, int y, float z, int defaultColor,
                                   boolean shadow, StyledTextLine line,
                                   @Nullable FloatUnaryOperator alphaModifier, RenderContext ctx)
    {
        if (this.textBuffer.isStarted())
        {
            int segmentX = x;
            Color4f defaultColor4f = Color4f.fromColor(defaultColor);
            RenderWrap.color(1f, 1f, 1f, 1f);
            RenderWrap.setupBlendSeparate();

            for (StyledTextSegment segment : line.segments)
            {
                segmentX += this.renderTextSegment(segmentX, y, z, defaultColor4f, shadow, segment, alphaModifier, ctx);
            }
        }
    }

    protected int renderTextSegment(int x, int y, float z, Color4f defaultColor,
                                    boolean shadow, StyledTextSegment segment,
                                    @Nullable FloatUnaryOperator alphaModifier, RenderContext ctx)
    {
        TextStyle style = segment.style;
        Color4f color = style.color != null ? style.color : defaultColor;

        if (alphaModifier != null)
        {
            color = color.withAlpha(alphaModifier.apply(color.a));
        }

        // Reference equality is fine here, as the sheets are fixed/pre-determined
        if (this.currentFontTexture != segment.texture)
        {
            if (this.currentFontTexture != null)
            {
                this.renderTextBuffer();
                this.startBuffers();
            }

            this.currentFontTexture = segment.texture;
        }

        if (style.shadow != null)
        {
            shadow = style.shadow;
        }

        if (shadow)
        {
            Color4f shadowColor = style.shadowColor != null ? style.shadowColor : TextStyle.getDefaultShadowColor(color);

            if (alphaModifier != null)
            {
                shadowColor = shadowColor.withAlpha(alphaModifier.apply(shadowColor.a));
            }

            float offset = this.unicode ? 0.5F : 1.0F;
            this.renderTextSegmentAndStylesWithColor(x + offset, y + offset, z, shadowColor, segment);
        }

        return this.renderTextSegmentAndStylesWithColor(x, y, z, color, segment);
    }

    protected int renderTextSegmentAndStylesWithColor(float x, float y, float z,
                                                      Color4f color, StyledTextSegment segment)
    {
        TextStyle style = segment.style;

        if (style.underline)
        {
            float lineHeight = this.unicode ? 0.5F : 1.0F;
            ShapeRenderUtils.renderRectangle(x - 1F, y + this.fontHeight, z,
                                             segment.renderWidth, lineHeight, color, this.styleBuffer);
        }

        if (style.strikeThrough)
        {
            float lineHeight = this.unicode ? 0.5F : 1.0F;
            ShapeRenderUtils.renderRectangle(x - 1F, y + this.fontHeight / 2.0F - 1F, z,
                                             segment.renderWidth + 1, lineHeight, color, this.styleBuffer);
        }

        return this.renderTextSegmentWithColor(x, y, z, segment, color, this.textBuffer);
    }

    protected int renderTextSegmentWithColor(float x, float y, float z, StyledTextSegment segment,
                                             Color4f color, VertexBuilder builder)
    {
        TextStyle style = segment.style;
        List<Glyph> glyphs = segment.getGlyphsForRender();
        int renderWidth = 0;

        for (Glyph glyph : glyphs)
        {
            renderWidth += this.renderGlyph(x + renderWidth, y, z, glyph, color, style, builder);
        }

        return renderWidth;
    }

    protected int renderGlyph(float x, float y, float z, Glyph glyph, Color4f color,
                              TextStyle style, VertexBuilder builder)
    {
        int renderWidth = glyph.getRenderWidthWithStyle(style);

        if (glyph == EMPTY_GLYPH)
        {
            return renderWidth;
        }

        float slant = style.italic ? 1.0F : 0.0F;
        float w = (float) glyph.width;
        float h = (float) glyph.height;
        float u1 = glyph.u1;
        float u2 = glyph.u2;
        float v1 = glyph.v1;
        float v2 = glyph.v2;

        // These weird offsets are for some reason needed in the unicode mode,
        // otherwise the text looks all distorted and broken...
        if (this.unicode)
        {
            u2 -= 0.00102F;
            v2 -= 0.00102F;
        }

        builder.posUvColor(x     + slant, y    , z, u1, v1, color);
        builder.posUvColor(x     - slant, y + h, z, u1, v2, color);
        builder.posUvColor(x + w - slant, y + h, z, u2, v2, color);
        builder.posUvColor(x + w + slant, y    , z, u2, v1, color);

        if (style.bold)
        {
            x += this.unicode ? 0.5F : 1.0F;

            builder.posUvColor(x     + slant, y    , z, u1, v1, color);
            builder.posUvColor(x     - slant, y + h, z, u1, v2, color);
            builder.posUvColor(x + w - slant, y + h, z, u2, v2, color);
            builder.posUvColor(x + w + slant, y    , z, u2, v1, color);
        }

        return renderWidth;
    }
}
