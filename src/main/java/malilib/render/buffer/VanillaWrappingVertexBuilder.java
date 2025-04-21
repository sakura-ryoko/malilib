package malilib.render.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.block.model.BakedQuad;

import malilib.MaLiLib;
import malilib.util.MathUtils;
import malilib.util.game.wrap.RenderWrap;

public class VanillaWrappingVertexBuilder implements VertexBuilder
{
    private static final ByteBuffer BYTE_BUFFER = allocateDirectByteBuffer(1048576 * 2);

    protected ByteBuffer byteBuffer;
    protected IntBuffer rawIntBuffer;
    protected ShortBuffer rawShortBuffer;
    protected FloatBuffer rawFloatBuffer;
    protected malilib.render.buffer.VertexFormat vertexFormat;
    protected boolean hasTexture;
    protected boolean started;
    protected int glDrawMode;
    protected int vertexCount;
    protected int vertexSize;

    public VanillaWrappingVertexBuilder(ByteBuffer buffer, int glDrawMode, malilib.render.buffer.VertexFormat vertexFormat)
    {
        this.byteBuffer = buffer;
        buffer.rewind(); // The derived FooBuffer for some reason gets a capacity from the position to limit
        buffer.limit(buffer.capacity());
        this.rawIntBuffer = buffer.asIntBuffer();
        this.rawShortBuffer = buffer.asShortBuffer();
        this.rawFloatBuffer = buffer.asFloatBuffer().asReadOnlyBuffer();
        this.glDrawMode = glDrawMode;
        this.vertexFormat = vertexFormat;
        this.vertexSize = vertexFormat.getSize();
        this.hasTexture = this.vertexFormat.hasTexture();
    }

    @Override
    public VertexBuilder pos(double x, double y, double z)
    {
        this.putPos(x, y, z);
        this.onVertexAdded();
        return this;
    }

    @Override
    public VertexBuilder posColor(double x, double y, double z, int r, int g, int b, int a)
    {
        this.putPos(x, y, z);
        this.putColor(r, g, b, a);
        this.onVertexAdded();
        return this;
    }

    @Override
    public VertexBuilder posUv(double x, double y, double z, float u, float v)
    {
        this.putPos(x, y, z);
        this.putUv(u, v);
        this.onVertexAdded();
        return this;
    }

    @Override
    public VertexBuilder posUvColor(double x, double y, double z, float u, float v, int r, int g, int b, int a)
    {
        this.putPos(x, y, z);
        this.putUv(u, v);
        this.putColor(r, g, b, a);
        this.onVertexAdded();
        return this;
    }

    @Override
    public VertexBuilder putBakedQuad(double x, double y, double z, BakedQuad quad, int colorARGB)
    {
        this.putBakedQuad(quad, colorARGB);
        this.addToQuadPosition(x, y, z);
        return this;
    }

    @Override
    public VertexBuilder putBakedQuad(BakedQuad quad, int colorARGB, int colorMultiplier)
    {
        int ca = (colorARGB >> 24) & 0xFF;
        int cr = (colorARGB >> 16) & 0xFF;
        int cg = (colorARGB >>  8) & 0xFF;
        int cb = (colorARGB      ) & 0xFF;
        int ma = (colorMultiplier >> 24) & 0xFF;
        int mr = (colorMultiplier >> 16) & 0xFF;
        int mg = (colorMultiplier >>  8) & 0xFF;
        int mb = (colorMultiplier      ) & 0xFF;
        int a = ca * ma / 255;
        int r = cr * mr / 255;
        int g = cg * mg / 255;
        int b = cb * mb / 255;
        int color = (a << 24) | (r << 16) | (g << 8) | b;

        return this.putBakedQuad(quad, color);
    }

    @Override
    public VertexBuilder putBakedQuad(BakedQuad quad, int colorARGB)
    {
        this.addQuadVertexData(quad.getVertexData());
        //this.onVertexAdded();
        net.minecraft.util.math.Vec3i normal = quad.getFace().getDirectionVec();
        this.putNormalForQuad(normal.getX(), normal.getY(), normal.getZ());
        this.putColorForQuad(colorARGB);
        return this;
    }

    @Override
    public VertexBuilder putBlockQuad(double x, double y, double z, BakedQuad quad, int colorARGB)
    {
        this.addQuadVertexData(quad.getVertexData());
        //this.onVertexAdded();
        this.addToQuadPosition(x, y, z);
        this.putColorForQuad(colorARGB);
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
        return this.start(this.glDrawMode, this.vertexFormat);
    }

    @Override
    public VertexBuilder start(int glDrawMode, malilib.render.buffer.VertexFormat format)
    {
        if (this.started == false)
        {
            this.started = true;
            this.glDrawMode = glDrawMode;
            this.vertexFormat = format;
            this.vertexSize = format.getSize();
            this.hasTexture = format.hasTexture();
            this.byteBuffer.limit(this.byteBuffer.capacity());
            this.reset();
        }

        return this;
    }

    @Override
    public void finishDrawing()
    {
        if (this.started)
        {
            this.byteBuffer.position(0);
            this.byteBuffer.limit(this.vertexCount * this.vertexSize);
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

            this.drawNoModeChanges();

            RenderWrap.enableTexture2D();
        }
    }

    @Override
    public void drawNoModeChanges()
    {
        if (this.started)
        {
            this.finishDrawing();

            if (this.vertexCount > 0)
            {
                this.vertexFormat.setupDraw(this.byteBuffer);
                RenderWrap.glDrawArrays(this.glDrawMode, 0, this.vertexCount);
                this.vertexFormat.disableAfterDraw();
            }

            this.reset();
        }
    }

    @Override
    public void reset()
    {
        this.vertexCount = 0;
    }

    @Override
    public int getGlDrawMode()
    {
        return this.glDrawMode;
    }

    @Override
    public int getVertexCount()
    {
        return this.vertexCount;
    }

    @Override
    public malilib.render.buffer.VertexFormat getVertexFormat()
    {
        return this.vertexFormat;
    }

    @Override
    public ByteBuffer getByteBuffer()
    {
        return this.byteBuffer;
    }

    @Override
    public void uploadVertexData()
    {
    }

    /*
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
    */

    protected void putPos(double x, double y, double z)
    {
        int startIndex = this.vertexCount * this.vertexSize + this.vertexFormat.getPositionOffset();
        this.byteBuffer.putFloat(startIndex    , (float) x);
        this.byteBuffer.putFloat(startIndex + 4, (float) y);
        this.byteBuffer.putFloat(startIndex + 8, (float) z);
    }

    protected void putColor(int r, int g, int b, int a)
    {
        int startIndex = this.vertexCount * this.vertexSize + this.vertexFormat.getColorOffset();

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
        {
            this.byteBuffer.put(startIndex    , (byte) r);
            this.byteBuffer.put(startIndex + 1, (byte) g);
            this.byteBuffer.put(startIndex + 2, (byte) b);
            this.byteBuffer.put(startIndex + 3, (byte) a);
        }
        else
        {
            this.byteBuffer.put(startIndex    , (byte) a);
            this.byteBuffer.put(startIndex + 1, (byte) b);
            this.byteBuffer.put(startIndex + 2, (byte) g);
            this.byteBuffer.put(startIndex + 3, (byte) r);
        }
    }

    protected void putUv(float u, float v)
    {
        int startIndex = this.vertexCount * this.vertexSize + this.vertexFormat.getUvOffset();
        this.byteBuffer.putFloat(startIndex    , u);
        this.byteBuffer.putFloat(startIndex + 4, v);
    }

    protected void addQuadVertexData(int[] vertexData)
    {
        // TODO verify the array length?
        this.growBuffer(vertexData.length * 4);
        this.rawIntBuffer.position(this.vertexCount * (this.vertexSize >> 2));
        this.rawIntBuffer.put(vertexData);
        this.vertexCount += vertexData.length / (this.vertexSize >> 2);
    }

    protected void putIntValueForLastQuad(int value, int offsetInVertexAsBytes)
    {
        // Get the data index of the first vertex of the last 4 vertices, as an int buffer offset
        int startIndex = ((this.vertexCount - 4) * this.vertexSize + offsetInVertexAsBytes) >> 2;
        int offset = this.vertexSize >> 2; // vertex size in int buffer entries

        this.rawIntBuffer.put(startIndex             , value);
        this.rawIntBuffer.put(startIndex + offset    , value);
        this.rawIntBuffer.put(startIndex + offset * 2, value);
        this.rawIntBuffer.put(startIndex + offset * 3, value);
    }

    protected void putNormalForQuad(int x, int y, int z)
    {
        x = (x * 127) & 0xFF;
        y = (y * 127) & 0xFF;
        z = (z * 127) & 0xFF;

        int packedValue = (z << 16) | (y << 8) | x;

        this.putIntValueForLastQuad(packedValue, this.vertexFormat.getNormalOffset());
    }

    protected void putColorForQuad(int argb)
    {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >>  8) & 0xFF;
        int b = (argb      ) & 0xFF;

        this.putColorForQuad(a, r, g, b);
    }

    protected void putColorForQuad(int a, int r, int g, int b)
    {
        int packedValue;

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
        {
            packedValue = (a << 24) | (b << 16) | (g << 8) | r;
        }
        else
        {
            packedValue = (r << 24) | (g << 16) | (b << 8) | a;
        }

        this.putIntValueForLastQuad(packedValue, this.vertexFormat.getColorOffset());
    }

    protected void addToQuadPosition(double x, double y, double z)
    {
        // Get the data index of the first vertex of the last 4 vertices, as an int buffer offset
        int startIndex = ((this.vertexCount - 4) * this.vertexSize + this.vertexFormat.getPositionOffset()) >> 2;
        int offset = this.vertexSize >> 2; // vertex size in int buffer entries

        for (int vertexIndex = 0; vertexIndex < 4; ++vertexIndex)
        {
            int baseOffset = startIndex + vertexIndex * offset;
            this.rawIntBuffer.put(baseOffset    , Float.floatToRawIntBits((float) x + Float.intBitsToFloat(this.rawIntBuffer.get(baseOffset    ))));
            this.rawIntBuffer.put(baseOffset + 1, Float.floatToRawIntBits((float) y + Float.intBitsToFloat(this.rawIntBuffer.get(baseOffset + 1))));
            this.rawIntBuffer.put(baseOffset + 2, Float.floatToRawIntBits((float) z + Float.intBitsToFloat(this.rawIntBuffer.get(baseOffset + 2))));
        }
    }

    protected void onVertexAdded()
    {
        ++this.vertexCount;
        this.growBuffer(this.vertexSize);
    }

    protected void growBuffer(int increaseAmount)
    {
        if (this.vertexCount * this.vertexSize + increaseAmount > this.byteBuffer.capacity())
        {
            int currentCapacity = this.byteBuffer.capacity();
            int newCapacity = currentCapacity + MathUtils.roundUp(increaseAmount, 2097152);
            MaLiLib.LOGGER.debug("VertexBuilder#growBuffer(): Old size {} B, new size {} B", currentCapacity, newCapacity);

            ByteBuffer newByteBuffer = allocateDirectByteBuffer(newCapacity);
            int position = this.rawIntBuffer.position();

            this.byteBuffer.position(0);
            newByteBuffer.put(this.byteBuffer);
            newByteBuffer.rewind();
            this.byteBuffer = newByteBuffer;
            this.rawFloatBuffer = this.byteBuffer.asFloatBuffer().asReadOnlyBuffer();
            this.rawIntBuffer = this.byteBuffer.asIntBuffer();
            this.rawShortBuffer = this.byteBuffer.asShortBuffer();
            this.rawIntBuffer.position(position);
            this.rawShortBuffer.position(position << 1);
        }
    }

    public static VertexBuilder coloredLines()
    {
        return create(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
    }

    public static VertexBuilder coloredLineStrip()
    {
        return create(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR);
    }

    public static VertexBuilder coloredLineLoop()
    {
        return create(GL11.GL_LINE_LOOP, VertexFormats.POSITION_COLOR);
    }

    public static VertexBuilder coloredQuads()
    {
        return create(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
    }

    public static VertexBuilder texturedQuad()
    {
        return create(GL11.GL_QUADS, VertexFormats.POSITION_TEX);
    }

    public static VertexBuilder tintedTexturedQuad()
    {
        return create(GL11.GL_QUADS, VertexFormats.POSITION_TEX_COLOR);
    }

    public static VertexBuilder coloredTriangles()
    {
        return create(GL11.GL_TRIANGLES, VertexFormats.POSITION_COLOR);
    }

    public static VertexBuilder coloredTriangleStrip()
    {
        return create(GL11.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
    }

    /**
     * Creates and returns a VertexBuilder using a static/shared ByteBuffer.
     * Note: The builder is also started using the given modes.
     */
    public static VertexBuilder create(int glMode, malilib.render.buffer.VertexFormat vertexFormat)
    {
        return create(BYTE_BUFFER, glMode, vertexFormat);
    }

    /**
     * Creates a VertexBuilder using a newly allocated ByteBuffer with the given capacity.
     * Note: The builder is also started using the given modes.
     */
    public static VertexBuilder create(int capacity, int glMode, malilib.render.buffer.VertexFormat vertexFormat)
    {
        ByteBuffer buffer = allocateDirectByteBuffer(capacity * 4);
        return create(buffer, glMode, vertexFormat);
    }

    /**
     * Creates and returns a VertexBuilder using the provided ByteBuffer.
     * Note: The builder is also started using the given modes.
     */
    public static VertexBuilder create(ByteBuffer buffer, int glMode, VertexFormat vertexFormat)
    {
        VanillaWrappingVertexBuilder builder = new VanillaWrappingVertexBuilder(buffer, glMode, vertexFormat);
        builder.start();

        return builder;
    }

    public static synchronized ByteBuffer allocateDirectByteBuffer(int capacity)
    {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }
}
