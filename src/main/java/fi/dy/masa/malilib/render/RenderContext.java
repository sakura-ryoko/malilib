package fi.dy.masa.malilib.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_10881;
import net.minecraft.class_10883;
import net.minecraft.client.gl.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.DrawableTexture;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.math.ColorHelper;

import javax.annotation.Nullable;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;

import fi.dy.masa.malilib.mixin.render.IMixinBufferBuilder;

public class RenderContext implements AutoCloseable
{
    private GlUsage usage;
    private GpuBuffer buffer;
    private RenderSystem.ShapeIndexBuffer shapeIndex;
    private BufferAllocator alloc;
    private BufferBuilder builder;
    private ShaderPipeline shader;
    private boolean started;
    private int bufferIndex;

    public RenderContext(ShaderPipeline shader)
    {
        this(shader, GlUsage.DYNAMIC_WRITE);
    }

    public RenderContext(ShaderPipeline shader, GlUsage usage)
    {
        this.alloc = new BufferAllocator(shader.getFormat().getVertexSizeByte());
        this.builder = new BufferBuilder(this.alloc, shader.getDrawMode(), shader.getFormat());
        this.shapeIndex = RenderSystem.getSequentialBuffer(shader.getDrawMode());
        this.shader = shader;
        this.usage = usage;
        this.buffer = null;
        this.bufferIndex = -1;
        this.started = true;
    }

    public BufferBuilder start(ShaderPipeline shader)
    {
        return this.start(shader, GlUsage.STATIC_WRITE);
    }

    public BufferBuilder start(ShaderPipeline shader, GlUsage usage)
    {
        this.reset();
        this.alloc = new BufferAllocator(shader.getFormat().getVertexSizeByte());
        this.builder = new BufferBuilder(this.alloc, shader.getDrawMode(), shader.getFormat());
        this.shapeIndex = RenderSystem.getSequentialBuffer(shader.getDrawMode());
        this.shader = shader;
        this.usage = usage;
        this.buffer = null;
        this.bufferIndex = -1;

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
        return this.shader.getFormat();
    }

    public VertexFormat.DrawMode getDrawMode()
    {
        return this.shader.getDrawMode();
    }

    /**
     * BUILDER PHASE --
     * -
     * This is to simply ensure that the builder is stored again
     * @param builder ()
     * @return ()
     */
    public RenderContext setBuilder(BufferBuilder builder)
    {
        this.builder = builder;
        return this;
    }

    /**
     * DRAW PHASE --
     * -
     * Performs the Renderer draw to the specified Frame Buffer
     */
    public void draw() throws RuntimeException
    {
        this.ensureSafe();

        if (RenderSystem.isOnRenderThread())
        {
            this.draw(this.builder.endNullable());
        }
    }

    public void draw(BuiltBuffer meshData) throws RuntimeException
    {
        this.ensureSafe();

        if (RenderSystem.isOnRenderThread())
        {
            this.draw(meshData, new float[]{0f, 0f, 0f});
        }
    }

    public void draw(Supplier<String> name, BuiltBuffer meshData) throws RuntimeException
    {
        this.draw(name, null, GlBufferTarget.VERTICES, -1, meshData, new float[]{0f, 0f, 0f});
    }

    public void draw(Supplier<String> name, BuiltBuffer meshData, int color) throws RuntimeException
    {
        this.draw(name, null, GlBufferTarget.VERTICES, color, meshData, new float[]{0f, 0f, 0f});
    }

    public void draw(Supplier<String> name, BuiltBuffer meshData, GlBufferTarget target, int color) throws RuntimeException
    {
        this.draw(name, null, target, color, meshData, new float[]{0f, 0f, 0f});
    }

    public void draw(BuiltBuffer meshData, float[] offset) throws RuntimeException
    {
        this.draw(() -> "Render Context", null, GlBufferTarget.VERTICES, -1, meshData, offset);
    }

    public void draw(BuiltBuffer meshData, int color, float[] offset) throws RuntimeException
    {
        this.draw(() -> "Render Context", null, GlBufferTarget.VERTICES, color, meshData, offset);
    }

    public void draw(BuiltBuffer meshData, GlBufferTarget target, int color, float[] offset) throws RuntimeException
    {
        this.draw(() -> "Render Context", null, target, color, meshData, offset);
    }

    public void draw(Supplier<String> name, BuiltBuffer meshData, GlBufferTarget target, int color, float[] offset) throws RuntimeException
    {
        this.draw(name, null, target, color, meshData, offset);
    }

    public void draw(@Nullable Framebuffer otherFb, BuiltBuffer meshData) throws RuntimeException
    {
        this.draw(() -> "Render Context", otherFb, GlBufferTarget.VERTICES, -1, meshData, new float[]{0f, 0f, 0f});
    }

    public void draw(@Nullable Framebuffer otherFb, BuiltBuffer meshData, float[] offset) throws RuntimeException
    {
        this.draw(() -> "Render Context", otherFb, GlBufferTarget.VERTICES, -1, meshData, offset);
    }

    public void draw(@Nullable Framebuffer otherFb, BuiltBuffer meshData, int color, float[] offset) throws RuntimeException
    {
        this.draw(() -> "Render Context", otherFb, GlBufferTarget.VERTICES, color, meshData, offset);
    }

    public void draw(Supplier<String> name, @Nullable Framebuffer otherFb, GlBufferTarget target, int color, BuiltBuffer meshData, float[] offset) throws RuntimeException
    {
        this.ensureSafe();

        if (RenderSystem.isOnRenderThread())
        {
            if (meshData == null)
            {
                this.bufferIndex = 0;
            }
            else
            {
                if (this.buffer != null && this.buffer.size >= meshData.getBuffer().remaining())
                {
                    class_10881 device = RenderSystem.getDevice().method_68389();
                    device.method_68350(this.buffer, meshData.getBuffer(), 0);
                }
                else
                {
                    if (this.buffer != null)
                    {
                        this.buffer.close();
                    }

                    this.buffer = RenderSystem.getDevice().method_68387(name, target, this.usage, meshData.getBuffer());
                }

                this.bufferIndex = meshData.getDrawParameters().indexCount();
            }

            if (this.bufferIndex > 0)
            {
                float[] rgba = {ColorHelper.getRedFloat(color), ColorHelper.getGreenFloat(color), ColorHelper.getBlueFloat(color), ColorHelper.getAlphaFloat(color)};

                RenderSystem.setShaderColor(rgba[0], rgba[1], rgba[2], rgba[3]);
                this.drawInternal(otherFb, offset);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            this.started = false;
        }
    }

    private void drawInternal(@Nullable Framebuffer otherFb, float[] offset)
    {
        RenderSystem.setModelOffset(-offset[0], offset[1], -offset[2]);
        Framebuffer mainFb = RenderUtils.fb();
        DrawableTexture texture1;
        DrawableTexture texture2;

        if (otherFb != null)
        {
            texture1 = otherFb.getColorAttachment();
            texture2 = otherFb.getDepthAttachment();
        }
        else
        {
            texture1 = mainFb.getColorAttachment();
            texture2 = mainFb.getDepthAttachment();
        }

        try (class_10883 device = RenderSystem.getDevice()
                                              .method_68389()
                                              .method_68368(texture1, OptionalInt.empty(), texture2, OptionalDouble.empty()))
        {
            device.method_68412(this.shader);
            device.method_68411(this.shapeIndex.method_68274(this.bufferIndex), this.shapeIndex.getIndexType());
            device.method_68410(0, this.buffer);
            device.method_68408(0, this.bufferIndex);
        }

        RenderSystem.resetModelOffset();
        this.started = false;
    }

    public void drawLayer() throws RuntimeException
    {
        this.ensureSafe();
        this.drawLayer(RenderUtils.fb(), this.builder.endNullable());
    }

    public void drawLayer(Framebuffer fb) throws RuntimeException
    {
        this.ensureSafe();
        this.drawLayer(fb, this.builder.endNullable());
    }

    public void drawLayer(Framebuffer fb, BuiltBuffer meshData) throws RuntimeException
    {
        this.ensureSafe();

        if (RenderSystem.isOnRenderThread())
        {
            try
            {
                GpuBuffer gpuBuffer = this.shader.getFormat().method_68460(meshData.getBuffer());
                GpuBuffer sortedBuffer = meshData.getSortedBuffer() != null ? this.shader.getFormat().method_68461(meshData.getSortedBuffer()) : null;

                try (class_10883 dev = RenderSystem.getDevice().method_68389()
                        .method_68368(fb.getColorAttachment(), OptionalInt.empty(), fb.useDepthAttachment ? fb.getDepthAttachment() : null, OptionalDouble.empty()))
                {
                    // SetShader ?
                    dev.method_68412(this.shader);
                    dev.method_68410(0, gpuBuffer);

                    // Scissor
                    if (RenderSystem.SCISSOR_STATE.method_68455())
                    {
                        dev.method_68413(RenderSystem.SCISSOR_STATE);
                    }

                    // Draw Textures
                    for (int i = 0; i < 12; i++)
                    {
                        DrawableTexture texture = RenderSystem.getShaderTexture(i);

                        if (texture != null)
                        {
                            dev.method_68414("RenderContext"+ i, texture);
                        }
                    }

                    // Sorting
                    if (sortedBuffer != null)
                    {
                        dev.method_68411(sortedBuffer, meshData.getDrawParameters().indexType());
                    }
                    else
                    {
                        RenderSystem.ShapeIndexBuffer shapeIndex = RenderSystem.getSequentialBuffer(meshData.getDrawParameters().mode());
                        dev.method_68411(shapeIndex.method_68274(meshData.getDrawParameters().indexCount()), shapeIndex.getIndexType());
                    }

                    // Unbind ?
                    dev.method_68408(0, meshData.getDrawParameters().indexCount());
                }

            }
            catch (Exception err)
            {
                if (meshData != null)
                {
                    try
                    {
                        meshData.close();
                    }
                    catch (Exception ignored) {}
                }

                this.started = false;
                throw new RuntimeException("Exception drawing; "+ err.getMessage());
            }

            meshData.close();
            this.started = false;
        }
    }

    public void drawTranslucentLayer() throws RuntimeException
    {
        this.ensureSafe();
        this.drawTranslucentLayer(RenderUtils.fb(), this.builder);
    }

    public void drawTranslucentLayer(Framebuffer fb) throws RuntimeException
    {
        this.ensureSafe();
        this.drawTranslucentLayer(fb, this.builder);
    }

    public void drawTranslucentLayer(Framebuffer fb, BufferBuilder builder) throws RuntimeException
    {
        this.ensureSafe();

        if (RenderSystem.isOnRenderThread())
        {
            try
            {
                BuiltBuffer meshData = builder.endNullable();

                if (meshData != null)
                {
                    meshData.sortQuads(this.alloc, RenderSystem.getProjectionType().getVertexSorter());
                }

                this.drawLayer(fb, meshData);
            }
            catch (Exception err)
            {
                throw new RuntimeException("Translucent Draw Failure!");
            }
        }
    }

    private void ensureSafe() throws RuntimeException
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

        if (this.shader == null)
        {
            throw new RuntimeException("Shader Pipeline not valid!");
        }

        /*
        if (!((IMixinBufferBuilder) this.builder).malilib_isBuilding())
        {
            throw new RuntimeException("Builder not building!");
        }

        if (((IMixinBufferBuilder) this.builder).malilib_getVertexCount() == 0)
        {
            throw new RuntimeException("Buffer Builder vertices are zero!");
        }

        if (((IMixinBufferBuilder) this.builder).malilib_getVertexPointer() == -1L)
        {
            throw new RuntimeException("Buffer Builder has no vertices!");
        }
         */
    }

    public void reset()
    {
        if (this.buffer != null)
        {
            this.buffer.close();
            this.buffer = null;
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
