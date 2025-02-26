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

public class RenderContext implements AutoCloseable
{
    private GlUsage usage;
    private BufferAllocator alloc;
    private BufferBuilder builder;
    private VertexFormat format;
    private VertexFormat.DrawMode drawMode;
    private ShaderPipeline shader;

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
    }

    public BufferBuilder start(VertexFormat.DrawMode drawMode, VertexFormat format, ShaderPipeline shader)
    {
        return this.start(drawMode, format, shader, GlUsage.STATIC_WRITE);
    }

    public BufferBuilder start(VertexFormat.DrawMode drawMode, VertexFormat format, ShaderPipeline shader, GlUsage usage)
    {
        this.alloc = new BufferAllocator(format.getVertexSizeByte());
        this.builder = new BufferBuilder(this.alloc, drawMode, format);
        this.usage = usage;
        this.format = format;
        this.drawMode = drawMode;
        this.shader = shader;

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

    public void draw(Framebuffer fb, BuiltBuffer meshData) throws RuntimeException
    {
        this.ensureSafe();

        if (RenderSystem.isOnRenderThread())
        {
            try (GpuBuffer gpuBuffer = this.shader.getFormat().method_68460(meshData.getBuffer()))
            {
                try (GpuBuffer sortedBuffer = meshData.getSortedBuffer() != null ? this.shader.getFormat().method_68461(meshData.getSortedBuffer()) : null)
                {
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

            }
            catch (Exception e)
            {
                if (meshData != null)
                {
                    meshData.close();
                }

                throw new RuntimeException(e);
            }

            meshData.close();
        }
    }

    public void drawWithShaders(Framebuffer fb, BuiltBuffer meshData, ShaderPipeline shaderKey) throws RuntimeException
    {
//
//        if (RenderSystem.isOnRenderThread())
//        {
//            this.drawWithShaders(meshData, RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), shaderKey);
//        }

        this.ensureSafe();
        this.shader = shaderKey;
        this.draw(fb, meshData);
    }

    private void ensureSafe() throws RuntimeException
    {
        if (this.alloc == null)
        {
            throw new RuntimeException("Allocator not valid!");
        }

        if (this.builder == null)
        {
            throw new RuntimeException("Buffer Builder not valid!");
        }
    }

    public void reset()
    {
        if (this.alloc != null)
        {
            this.alloc.close();
            this.alloc = null;
        }

        if (this.builder != null)
        {
            this.builder = null;
        }
    }

    @Override
    public void close() throws Exception
    {
        this.reset();
    }
}
