package fi.dy.masa.malilib.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_10883;
import net.minecraft.client.gl.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.DrawableTexture;
import net.minecraft.client.util.BufferAllocator;

import javax.annotation.Nullable;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import fi.dy.masa.malilib.mixin.render.IMixinBufferBuilder;

public class RenderContext implements AutoCloseable
{
    private GlUsage usage;
    private BufferAllocator alloc;
    private BufferBuilder builder;
    private VertexFormat format;
    private VertexFormat.DrawMode drawMode;
    private ShaderPipeline shader;
    private boolean started;

    public RenderContext(VertexFormat.DrawMode drawMode, VertexFormat format)
    {
        this(drawMode, format, ShaderPipelines.DEBUG_QUADS, GlUsage.STATIC_WRITE);
    }

    public RenderContext(VertexFormat.DrawMode drawMode, VertexFormat format, ShaderPipeline shader)
    {
        this(drawMode, format, shader, GlUsage.STATIC_WRITE);
    }

    public RenderContext(VertexFormat.DrawMode drawMode, VertexFormat format, ShaderPipeline shader, GlUsage usage)
    {
        this.alloc = new BufferAllocator(format.getVertexSizeByte());
        this.builder = new BufferBuilder(this.alloc, drawMode, format);
        this.usage = usage;
        this.format = format;
        this.drawMode = drawMode;
        this.shader = shader;
        this.started = true;
    }

    public BufferBuilder start(VertexFormat.DrawMode drawMode, VertexFormat format, ShaderPipeline shader)
    {
        return this.start(drawMode, format, shader, GlUsage.STATIC_WRITE);
    }

    public BufferBuilder start(VertexFormat.DrawMode drawMode, VertexFormat format, ShaderPipeline shader, GlUsage usage)
    {
        this.reset();

        this.alloc = new BufferAllocator(format.getVertexSizeByte());
        this.builder = new BufferBuilder(this.alloc, drawMode, format);
        this.usage = usage;
        this.format = format;
        this.drawMode = drawMode;
        this.shader = shader;
        this.started = true;

        return this.builder;
    }

    public @Nullable BufferBuilder getBuilder()
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

    public RenderContext setShader(ShaderPipeline shader)
    {
        this.shader = shader;
        return this;
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
     * @param fb ()
     */
    public void draw(Framebuffer fb) throws RuntimeException
    {
        this.ensureSafe();

        if (RenderSystem.isOnRenderThread())
        {
            this.draw(fb, this.builder.endNullable());
        }
    }

    public void draw(Framebuffer fb, BuiltBuffer meshData) throws RuntimeException
    {
        this.ensureSafe();

        if (RenderSystem.isOnRenderThread())
        {
            BuiltBuffer built = meshData;
            try
            {
                GpuBuffer gpuBuffer = this.shader.getFormat().method_68460(built.getBuffer());
                GpuBuffer sortedBuffer = built.getSortedBuffer() != null ? this.shader.getFormat().method_68461(built.getSortedBuffer()) : null;

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
                        dev.method_68411(sortedBuffer, built.getDrawParameters().indexType());
                    }
                    else
                    {
                        RenderSystem.ShapeIndexBuffer shapeIndex = RenderSystem.getSequentialBuffer(built.getDrawParameters().mode());
                        dev.method_68411(shapeIndex.method_68274(built.getDrawParameters().indexCount()), shapeIndex.getIndexType());
                    }

                    // Unbind ?
                    dev.method_68408(0, built.getDrawParameters().indexCount());
                }

            }
            catch (Exception err)
            {
                if (meshData != null)
                {
                    try
                    {
                        built.close();
                    }
                    catch (Exception ignored) {}
                }

                this.started = false;
                throw new RuntimeException("Exception drawing; "+ err.getMessage());
            }

            if (meshData != null)
            {
                meshData.close();
            }

            this.started = false;
        }
    }

    public void drawShader(Framebuffer fb, BuiltBuffer meshData, ShaderPipeline shaderKey) throws RuntimeException
    {
        this.ensureSafe();

        if (RenderSystem.isOnRenderThread())
        {
            this.shader = shaderKey;
            this.draw(fb, meshData);
        }
    }

    public void drawTranslucent(Framebuffer fb, BufferBuilder builder) throws RuntimeException
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

                this.draw(fb, meshData);
            }
            catch (Exception err)
            {
                throw new RuntimeException("Translucent Draw Failure!");
            }
        }
    }

    public void drawTranslucent(Framebuffer fb, BufferBuilder builder, ShaderPipeline shader) throws RuntimeException
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

                this.shader = shader;
                this.draw(fb, meshData);
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

        this.started = false;
    }

    @Override
    public void close() throws Exception
    {
        this.reset();
    }
}
