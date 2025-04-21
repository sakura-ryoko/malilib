package malilib.render.buffer;

import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

import malilib.util.game.wrap.RenderWrap;

public class VanillaWrappingVertexBuilder implements VertexBuilder
{
    protected static final WorldVertexBufferUploader VBO_UPLOADER = new WorldVertexBufferUploader();

    protected final BufferBuilder buffer;
    protected final boolean hasTexture;
    protected VertexFormat vertexFormat;
    protected boolean started;
    protected int glMode;

    public VanillaWrappingVertexBuilder(BufferBuilder buffer)
    {
        this(buffer, buffer.getDrawMode(), buffer.getVertexFormat());
    }

    public VanillaWrappingVertexBuilder(BufferBuilder buffer, int glMode, VertexFormat vertexFormat)
    {
        this.buffer = buffer;
        this.glMode = glMode;
        this.vertexFormat = vertexFormat;
        this.hasTexture = this.vertexFormat.getElements().stream().anyMatch((el) -> el.getUsage() == VertexFormatElement.EnumUsage.UV);
    }

    @Override
    public VertexBuilder posColor(double x, double y, double z, int r, int g, int b, int a)
    {
        this.buffer.pos(x, y, z).color(r, g, b, a).endVertex();
        return this;
    }

    @Override
    public VertexBuilder posUv(double x, double y, double z, float u, float v)
    {
        this.buffer.pos(x, y, z).tex(u, v).endVertex();
        return this;
    }

    @Override
    public VertexBuilder posUvColor(double x, double y, double z, float u, float v, int r, int g, int b, int a)
    {
        this.buffer.pos(x, y, z).tex(u, v).color(r, g, b, a).endVertex();
        return this;
    }

    @Override
    public boolean isStarted()
    {
        return this.started;
    }

    @Override
    public VertexBuilder start()
    {
        if (this.started == false)
        {
            this.buffer.begin(this.glMode, this.vertexFormat);
            this.started = true;
        }

        return this;
    }

    @Override
    public void finishDrawing()
    {
        if (this.started)
        {
            this.buffer.finishDrawing();
        }

        this.started = false;
    }

    @Override
    public void draw()
    {
        if (this.started)
        {
            if (this.hasTexture)
            {
                RenderWrap.enableTexture2D();
            }
            else
            {
                RenderWrap.disableTexture2D();
            }

            RenderWrap.setupBlendSeparate();
            this.buffer.finishDrawing();
            VBO_UPLOADER.draw(this.buffer);

            RenderWrap.enableTexture2D();
        }

        this.started = false;
    }

    @Override
    public void reset()
    {
        this.buffer.reset();
    }

    @Override
    public ByteBuffer getByteBuffer()
    {
        return this.buffer.getByteBuffer();
    }

    @Override
    public BufferBuilder.State getVertexData()
    {
        return this.buffer.getVertexState();
    }

    @Override
    public void setVertexData(BufferBuilder.State date)
    {
        this.buffer.setVertexState(date);
    }

    @Override
    public void addVertexData(int[] data)
    {
        this.buffer.addVertexData(data);
    }

    @Override
    public void putPosition(double x, double y, double z)
    {
        this.buffer.putPosition(x, y, z);
    }

    @Override
    public void putNormal(float x, float y, float z)
    {
        this.buffer.putNormal(x, y, z);
    }

    @Override
    public void putColorMultiplier(float r, float g, float b, int vertexIndex)
    {
        this.buffer.putColorMultiplier(r, g, b, vertexIndex);
    }

    @Override
    public void putQuadColor(int argb)
    {
        this.buffer.putColor4(argb);
    }

    @Override
    public void putQuadColor(float r, float g, float b)
    {
        this.buffer.putColorRGB_F4(r, g, b);
    }

    @Override
    public void putBrightness(int vertex0, int vertex1, int vertex2, int vertex3)
    {
        this.buffer.putBrightness4(vertex0, vertex1, vertex2, vertex3);
    }

    @Override
    public void sortVertexData(float cameraX, float cameraY, float cameraZ)
    {
        this.buffer.sortVertexData(cameraX, cameraY, cameraZ);
    }

    public static VertexBuilder coloredLines()
    {
        return create(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
    }

    public static VertexBuilder coloredLineStrip()
    {
        return create(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
    }

    public static VertexBuilder coloredLineLoop()
    {
        return create(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
    }

    public static VertexBuilder coloredQuads()
    {
        return create(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
    }

    public static VertexBuilder texturedQuad()
    {
        return create(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
    }

    public static VertexBuilder tintedTexturedQuad()
    {
        return create(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
    }

    public static VertexBuilder coloredTriangles()
    {
        return create(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
    }

    public static VertexBuilder coloredTriangleStrip()
    {
        return create(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
    }

    public static VertexBuilder create(int glMode, VertexFormat vertexFormat)
    {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        return create(buffer, glMode, vertexFormat);
    }

    /**
     * Creates and returns a VertexBuilder using the given BufferBuilder.
     * Note: The buffer is also started using the given modes.
     */
    public static VertexBuilder create(BufferBuilder buffer, int glMode, VertexFormat vertexFormat)
    {
        VanillaWrappingVertexBuilder builder = new VanillaWrappingVertexBuilder(buffer, glMode, vertexFormat);
        builder.start();

        return builder;
    }

    /**
     * Create a VertexBuilder using a separate BufferBuilder.
     * Note: The buffer is also started using the given modes.
     */
    public static VertexBuilder create(int bufferCapacity, int glMode, VertexFormat vertexFormat)
    {
        return create(new BufferBuilder(bufferCapacity), glMode, vertexFormat);
    }
}
