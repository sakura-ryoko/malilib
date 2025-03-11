package fi.dy.masa.malilib.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.math.ColorHelper;

import javax.annotation.Nullable;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.mixin.render.IMixinBufferBuilder;

public class RenderContext implements AutoCloseable
{
    private Supplier<String> name;
    private GlUsage usage;
    private GpuBuffer gpuBuffer;
    private RenderSystem.ShapeIndexBuffer shapeIndex;
    private BufferAllocator alloc;
    private BufferBuilder builder;
    private ShaderPipeline shader;
    private VertexFormat format;
    private VertexFormat.DrawMode drawMode;
    private ResourceTexture texture;
    private boolean started;
    private int bufferIndex;

    public RenderContext(ShaderPipeline shader)
    {
        this(shader, GlUsage.DYNAMIC_WRITE);
    }

    public RenderContext(ShaderPipeline shader, GlUsage usage)
    {
        this(() -> "RenderContext", shader, usage);
    }

    public RenderContext(Supplier<String> name, ShaderPipeline shader, GlUsage usage)
    {
        this.name = name;
        this.alloc = new BufferAllocator(shader.getFormat().getVertexSizeByte() * 4);
        this.builder = new BufferBuilder(this.alloc, shader.getDrawMode(), shader.getFormat());
        this.shapeIndex = RenderSystem.getSequentialBuffer(shader.getDrawMode());
        this.format = shader.getFormat();
        this.drawMode = shader.getDrawMode();
        this.shader = shader;
        this.usage = usage;
        this.gpuBuffer = null;
        this.bufferIndex = -1;
        this.texture = null;
        this.started = true;
    }

    public RenderContext(VertexFormat format, VertexFormat.DrawMode drawMode)
    {
        this(() -> "RenderContext", format, drawMode, GlUsage.STATIC_WRITE);
    }

    public RenderContext(VertexFormat format, VertexFormat.DrawMode drawMode, GlUsage usage)
    {
        this(() -> "RenderContext", format, drawMode, usage);
    }

    public RenderContext(Supplier<String> name, VertexFormat format, VertexFormat.DrawMode drawMode, GlUsage usage)
    {
        this.name = name;
        this.alloc = new BufferAllocator(format.getVertexSizeByte() * 4);
        this.builder = new BufferBuilder(this.alloc, drawMode, format);
        this.shapeIndex = RenderSystem.getSequentialBuffer(drawMode);
        this.format = format;
        this.drawMode = drawMode;
        this.shader = null;
        this.usage = usage;
        this.gpuBuffer = null;
        this.bufferIndex = -1;
        this.texture = null;
        this.started = true;
    }

    public BufferBuilder startShader(ShaderPipeline shader)
    {
        return this.startShader(() -> "RenderContext", shader, GlUsage.STATIC_WRITE);
    }

    public BufferBuilder startShader(Supplier<String> name, ShaderPipeline shader, GlUsage usage)
    {
        this.reset();
        this.name = name;
        this.alloc = new BufferAllocator(shader.getFormat().getVertexSizeByte() * 4);
        this.builder = new BufferBuilder(this.alloc, shader.getDrawMode(), shader.getFormat());
        this.shapeIndex = RenderSystem.getSequentialBuffer(shader.getDrawMode());
        this.format = shader.getFormat();
        this.drawMode = shader.getDrawMode();
        this.shader = shader;
        this.usage = usage;
        this.gpuBuffer = null;
        this.bufferIndex = -1;
        this.texture = null;
        this.started = true;
        return this.builder;
    }

    // Textured Quads Draw
    public BufferBuilder startNoShader(VertexFormat format, VertexFormat.DrawMode drawMode)
    {
        return this.startNoShader(() -> "RenderContext", format, drawMode, GlUsage.STATIC_WRITE);
    }

    public BufferBuilder startNoShader(Supplier<String> name, VertexFormat format, VertexFormat.DrawMode drawMode, GlUsage usage)
    {
        this.reset();
        this.name = name;
        this.alloc = new BufferAllocator(format.getVertexSizeByte() * 4);
        this.builder = new BufferBuilder(this.alloc, drawMode, format);
        this.shapeIndex = RenderSystem.getSequentialBuffer(drawMode);
        this.format = format;
        this.drawMode = drawMode;
        this.shader = null;
        this.usage = usage;
        this.gpuBuffer = null;
        this.bufferIndex = -1;
        this.texture = null;
        this.started = true;
        return this.builder;
    }

    public BufferBuilder getBuilder()
    {
        return this.builder;
    }

    public GlUsage getUsage()
    {
        return this.usage;
    }

    public VertexFormat getFormat()
    {
        return this.format;
    }

    public VertexFormat.DrawMode getDrawMode()
    {
        return this.drawMode;
    }

    public VertexFormat getShaderFormat()
    {
        if (this.shader != null)
        {
            return this.shader.getFormat();
        }

        return this.format;
    }

    public VertexFormat.DrawMode getShaderDrawMode()
    {
        if (this.shader != null)
        {
            return this.shader.getDrawMode();
        }

        return this.drawMode;
    }

    /**
     * BUILDER PHASE --
     * -
     * This is to simply ensure that the builder is stored again
     * @param builder ()
     * @return ()
     */
    public RenderContext setBuilder(BufferBuilder builder) throws RuntimeException
    {
        this.ensureBuilding(builder);
        this.builder = builder;
        return this;
    }

    public RenderContext setShader(ShaderPipeline shader) throws RuntimeException
    {
        if (this.format != shader.getFormat() || this.drawMode != shader.getDrawMode())
        {
            throw new RuntimeException("Shader does not match Format/Draw mode!");
        }

        this.shader = shader;
        return this;
    }

    /**
     * UPLOAD PHASE --
     * -
     * This uploads the BufferBuilder to the GpuBuffer for Drawing
     */
    public void upload() throws RuntimeException
    {
        this.ensureSafeNoShader();
        this.upload(this.name, this.builder.endNullable(), GlBufferTarget.VERTICES);
    }

    public void upload(BufferBuilder builder) throws RuntimeException
    {
        this.ensureSafeNoShader();
        this.ensureBuilding(builder);
        this.builder = builder;
        this.upload(this.name, this.builder.endNullable(), GlBufferTarget.VERTICES);
    }

    public void upload(BuiltBuffer meshData) throws RuntimeException
    {
        this.ensureSafeNoShader();
        this.upload(this.name, meshData, GlBufferTarget.VERTICES);
    }

    public void upload(BuiltBuffer meshData, GlBufferTarget target) throws RuntimeException
    {
        this.ensureSafeNoShader();
        this.upload(this.name, meshData, target);
    }

    public void upload(Supplier<String> name, BuiltBuffer meshData, GlBufferTarget target) throws RuntimeException
    {
        this.name = name;
        this.ensureSafeNoShader();

        if (RenderSystem.isOnRenderThread() && meshData != null)
        {
            int expectedSize = meshData.getBuffer().remaining();

            if (this.gpuBuffer != null)
            {
                this.gpuBuffer.close();
            }

            this.gpuBuffer = RenderSystem.getDevice().createBuffer(this.name, target, this.usage, expectedSize);

            RenderSystem.getDevice()
                        .getResourceManager()
                        .copyDataInto(this.gpuBuffer, meshData.getBuffer(), 0);

            this.bufferIndex = meshData.getDrawParameters().indexCount();
        }
    }

    /**
     * BIND TEXTURE PHASE --
     * -
     * Performs the Texture Binding/Unbind for the "Shader Texture" layer
     */
    public void bindTexture(Identifier id)
    {
        this.ensureSafeNoBuffer();
//        RenderUtils.tex().registerTexture(id);
//        this.texture = RenderUtils.tex().getTexture(id);
//        this.texture.setFilter(TriState.FALSE, false);
//        RenderSystem.setShaderTexture(0, this.texture.getGlTexture());

        this.texture = new ResourceTexture(id);
        RenderUtils.tex().registerTexture(id, this.texture);

        try (TextureContents contents = this.texture.loadContents(RenderUtils.mc().getResourceManager()))
        {
            NativeImage image = contents.image();
            MaLiLib.LOGGER.warn("NativeImage Id [{}] //  Width [{}], Height [{}] // Format: [{}]", image.imageId(), image.getWidth(), image.getHeight(), image.getFormat().name());
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("bindTexture exception; {}", err.getMessage());
            throw new RuntimeException(err);
        }

        this.texture.setFilter(TriState.FALSE, false);
        RenderSystem.setShaderTexture(0, this.texture.getGlTexture());
    }

    public void unbindTexture(@Nullable Identifier id)
    {
        if (id != null)
        {
            RenderUtils.tex().destroyTexture(id);
        }

        RenderSystem.setShaderTexture(0, null);
        this.texture = null;
    }

    /**
     * DRAW PHASE --
     * -
     * Performs the Renderer draw to the specified Frame Buffer
     */
    public void draw() throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.ensureBuilding(this.builder);
        this.draw(this.builder.endNullable());
    }

    public void draw(BuiltBuffer meshData) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(meshData, new float[]{0f, 0f, 0f}, false);
    }

    public void draw(BuiltBuffer meshData, int color) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(this.name, null, GlBufferTarget.VERTICES, color, meshData, new float[]{0f, 0f, 0f}, false, 0.0f, false);
    }

    public void draw(Supplier<String> name, BuiltBuffer meshData) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(name, null, GlBufferTarget.VERTICES, -1, meshData, new float[]{0f, 0f, 0f}, false, 0.0f, false);
    }

    public void draw(Supplier<String> name, BuiltBuffer meshData, int color) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(name, null, GlBufferTarget.VERTICES, color, meshData, new float[]{0f, 0f, 0f}, false, 0.0f, false);
    }

    public void draw(Supplier<String> name, BuiltBuffer meshData, GlBufferTarget target, int color) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(name, null, target, color, meshData, new float[]{0f, 0f, 0f}, false, 0.0f, false);
    }

    public void draw(BuiltBuffer meshData, float[] offset, boolean useOffset) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(this.name, null, GlBufferTarget.VERTICES, -1, meshData, offset, useOffset, 0.0f, false);
    }

    public void draw(BuiltBuffer meshData, int color, float[] offset, boolean useOffset) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(this.name, null, GlBufferTarget.VERTICES, color, meshData, offset, useOffset, 0.0f, false);
    }

    public void draw(BuiltBuffer meshData, GlBufferTarget target, int color, float[] offset, boolean useOffset) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(this.name, null, target, color, meshData, offset, useOffset, 0.0f, false);
    }

    public void draw(Supplier<String> name, BuiltBuffer meshData, GlBufferTarget target, int color, float[] offset, boolean useOffset) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(name, null, target, color, meshData, offset, useOffset, 0.0f, false);
    }

    public void draw(@Nullable Framebuffer otherFb, BuiltBuffer meshData) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(this.name, otherFb, GlBufferTarget.VERTICES, -1, meshData, new float[]{0f, 0f, 0f}, false, 0.0f, false);
    }

    public void draw(@Nullable Framebuffer otherFb, BuiltBuffer meshData, float[] offset, boolean useOffset) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(this.name, otherFb, GlBufferTarget.VERTICES, -1, meshData, offset, useOffset, 0.0f, false);
    }

    public void draw(@Nullable Framebuffer otherFb, BuiltBuffer meshData, int color, float[] offset, boolean useOffset) throws RuntimeException
    {
        this.ensureSafeNoBuffer();
        this.draw(this.name, otherFb, GlBufferTarget.VERTICES, color, meshData, offset, useOffset, 0.0f, false);
    }

    public void draw(Supplier<String> name, @Nullable Framebuffer otherFb, GlBufferTarget target,
                     int color, BuiltBuffer meshData,
                     float[] offset, boolean useOffset,
                     float lineWidth, boolean setLineWidth) throws RuntimeException
    {
        this.ensureSafeNoBuffer();

        if (RenderSystem.isOnRenderThread())
        {
            if (meshData == null)
            {
                this.bufferIndex = 0;
            }
            else
            {
                this.name = name;

                // Create & upload buffer
                if (this.bufferIndex < 1)
                {
                    this.upload(name, meshData, target);
                }
            }

            // Draw
            if (this.bufferIndex > 0)
            {
                float[] rgba = {ColorHelper.getRedFloat(color), ColorHelper.getGreenFloat(color), ColorHelper.getBlueFloat(color), ColorHelper.getAlphaFloat(color)};

                RenderSystem.setShaderColor(rgba[0], rgba[1], rgba[2], rgba[3]);
                this.drawInternal(otherFb, offset, useOffset, lineWidth, setLineWidth);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    public void drawPost() throws RuntimeException
    {
        this.drawPost(null, -1, new float[]{0f, 0f, 0f}, false, 0.0f, false);
    }

    public void drawPost(int color) throws RuntimeException
    {
        this.drawPost(null, color, new float[]{0f, 0f, 0f}, false, 0.0f, false);
    }

    public void drawPost(@Nullable Framebuffer otherFb, int color) throws RuntimeException
    {
        this.drawPost(otherFb, color, new float[]{0f, 0f, 0f}, false, 0.0f, false);
    }

    public void drawPost(@Nullable Framebuffer otherFb, int color, float[] offset, boolean useOffset) throws RuntimeException
    {
        this.drawPost(otherFb, color, offset, useOffset, 0.0f, false);
    }

    public void drawPost(@Nullable Framebuffer otherFb, int color, float[] offset, boolean useOffset, float lineWidth, boolean setLineWidth) throws RuntimeException
    {
        if (this.bufferIndex > 0)
        {
            float[] rgba = new float[]{ColorHelper.getRedFloat(color), ColorHelper.getGreenFloat(color), ColorHelper.getBlueFloat(color), ColorHelper.getAlphaFloat(color)};

            RenderSystem.setShaderColor(rgba[0], rgba[1], rgba[2], rgba[3]);
            this.drawInternal(otherFb, offset, useOffset, lineWidth, setLineWidth);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private void drawInternal(@Nullable Framebuffer otherFb,
                              float[] offset, boolean useOffset,
                              float lineWidth, boolean setLineWidth) throws RuntimeException
    {
        this.ensureSafeNoTexture();

        if (RenderSystem.isOnRenderThread())
        {
            if (useOffset)
            {
                RenderSystem.setModelOffset(-offset[0], offset[1], -offset[2]);
                //RenderSystem.setModelOffset(offset[0], offset[1], offset[2]);
            }
            Framebuffer mainFb = RenderUtils.fb();
            DrawableTexture texture1;
            DrawableTexture texture2;

            if (otherFb != null)
            {
                texture1 = otherFb.getColorAttachment();
                texture2 = otherFb.useDepthAttachment ? otherFb.getDepthAttachment() : null;
            }
            else
            {
                texture1 = mainFb.getColorAttachment();
                texture2 = mainFb.useDepthAttachment ? mainFb.getDepthAttachment() : null;
            }

            // Attach Frame buffers
            try (RenderPass pass = RenderSystem.getDevice()
                                               .getResourceManager()
                                               .createRenderPass(texture1, OptionalInt.empty(),
                                                                 texture2, OptionalDouble.empty()))
            {
                pass.bindShader(this.shader);

                for (int i = 0; i < 12; i++)
                {
                    DrawableTexture drawableTexture = RenderSystem.getShaderTexture(i);

                    if (drawableTexture != null)
                    {
                        pass.setUniform("Sampler"+i, drawableTexture);
                    }
                }

                if (setLineWidth)
                {
                    float width = lineWidth > 0.0f ? lineWidth : RenderSystem.getShaderLineWidth();
                    pass.setUniform("LineWidth", width);
                }

                pass.setIndexBuffer(this.shapeIndex.getIndexBuffer(this.bufferIndex), this.shapeIndex.getIndexType());
                pass.setVertexBuffer(0, this.gpuBuffer);
                pass.drawObjects(0, this.bufferIndex);
            }

            if (useOffset)
            {
                RenderSystem.resetModelOffset();
            }
        }
    }

    private void ensureBuilding(BufferBuilder builder) throws RuntimeException
    {
        // Check BufferBuilder status
        if (!((IMixinBufferBuilder) builder).malilib_isBuilding())
        {
            throw new RuntimeException("Buffer Builder is not building!");
        }

        if (((IMixinBufferBuilder) builder).malilib_getVertexCount() == 0)
        {
            throw new RuntimeException("Buffer Builder vertices are zero!");
        }

        if (((IMixinBufferBuilder) builder).malilib_getVertexPointer() == -1L)
        {
            throw new RuntimeException("Buffer Builder has no vertices!");
        }
    }

    private void ensureSafeNoShader() throws RuntimeException
    {
        if (!this.started)
        {
            throw new RuntimeException("Context not started!");
        }

        if (this.alloc == null)
        {
            throw new RuntimeException("Allocator not valid!");
        }

        if (this.builder == null)
        {
            throw new RuntimeException("Buffer Builder not valid!");
        }

        if (this.name.get().isEmpty())
        {
            this.name = () -> "RenderContext";
        }
    }

    private void ensureSafeNoBuffer() throws RuntimeException
    {
        this.ensureSafeNoShader();

        if (this.shader == null)
        {
            throw new RuntimeException("Shader Pipeline not valid!");
        }
    }

    private void ensureSafeNoTexture() throws RuntimeException
    {
        this.ensureSafeNoBuffer();

        if (this.gpuBuffer == null)
        {
            throw new RuntimeException("GpuBuffer not uploaded!");
        }
    }

    private void ensureSafe()
    {
        this.ensureSafeNoTexture();

        if (this.texture == null)
        {
            throw new RuntimeException("A Texture Object is expected to be bound");
        }
    }

    public void reset()
    {
        if (this.gpuBuffer != null)
        {
            this.gpuBuffer.close();
            this.gpuBuffer = null;
        }

        if (this.builder != null)
        {
            if (((IMixinBufferBuilder) this.builder).malilib_isBuilding() &&
                ((IMixinBufferBuilder) this.builder).malilib_getVertexCount() != 0)
            {
                try
                {
                    BuiltBuffer meshData = this.builder.endNullable();

                    if (meshData != null)
                    {
                        meshData.close();
                    }
                }
                catch (Exception ignored) { }
            }

            this.builder = null;
        }

        if (this.alloc != null)
        {
            this.alloc.close();
            this.alloc = null;
        }

        this.bufferIndex = -1;
        this.started = false;
    }

    @Override
    public void close() throws Exception
    {
        this.reset();
    }
}
