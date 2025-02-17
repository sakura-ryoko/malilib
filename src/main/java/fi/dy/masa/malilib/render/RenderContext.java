package fi.dy.masa.malilib.render;

import javax.annotation.Nullable;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.GlUsage;
import net.minecraft.client.gl.ShaderProgramLayer;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.BufferAllocator;

public class RenderContext implements AutoCloseable
{

    @Nullable
    private VertexBuffer vertex;
    private BufferAllocator alloc;
    private BufferBuilder builder;

    public RenderContext(VertexFormat.DrawMode drawMode, VertexFormat format)
    {
        this(drawMode, format, GlUsage.STATIC_WRITE);
    }

    public RenderContext(VertexFormat.DrawMode drawMode, VertexFormat format, GlUsage usage)
    {
        this.alloc = new BufferAllocator(format.getVertexSizeByte());
        this.builder = new BufferBuilder(this.alloc, drawMode, format);
        this.vertex = new VertexBuffer(usage);
    }

    public BufferBuilder start(VertexFormat.DrawMode drawMode, VertexFormat format)
    {
        return this.start(drawMode, format, GlUsage.STATIC_WRITE);
    }

    public BufferBuilder start(VertexFormat.DrawMode drawMode, VertexFormat format, GlUsage usage)
    {
        this.alloc = new BufferAllocator(format.getVertexSizeByte());
        this.builder = new BufferBuilder(this.alloc, drawMode, format);
        this.vertex = new VertexBuffer(usage);

        return this.builder;
    }

    public @Nullable BufferBuilder getBuilder()
    {
        return this.builder;
    }

    public void draw() throws RuntimeException
    {
        if (this.vertex == null)
        {
            throw new RuntimeException("Vertex Buffer is null!");
        }

        if (RenderSystem.isOnRenderThread())
        {
            this.ensureSafe();
            this.vertex.bind();
            this.vertex.upload(this.builder.end());
            this.vertex.draw();
            VertexBuffer.unbind();
        }
    }

    public void draw(BuiltBuffer meshData) throws RuntimeException
    {
        if (this.vertex == null)
        {
            throw new RuntimeException("Vertex Buffer is null!");
        }

        if (RenderSystem.isOnRenderThread())
        {
            this.ensureSafe();
            this.vertex.bind();
            this.vertex.upload(meshData);
            this.vertex.draw();
            VertexBuffer.unbind();
        }
    }

    public void drawWithShaders(BuiltBuffer meshData, ShaderProgramLayer shaderKey) throws RuntimeException
    {
        if (RenderSystem.isOnRenderThread())
        {
            this.ensureSafe();
            this.drawWithShaders(meshData, RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), shaderKey);
        }
    }

    public void drawWithShaders(BuiltBuffer meshData, Matrix4f modelView, Matrix4f posMatrix, ShaderProgramLayer shaderKey) throws RuntimeException
    {
        if (this.vertex == null)
        {
            throw new RuntimeException("Vertex Buffer is null!");
        }

        if (RenderSystem.isOnRenderThread())
        {
            this.ensureSafe();
            this.vertex.bind();
            this.vertex.upload(meshData);
            this.vertex.draw(modelView, posMatrix, shaderKey.getProgram());
            VertexBuffer.unbind();
            meshData.close();
        }
    }

    private void ensureSafe() throws RuntimeException
    {
        if (this.vertex == null)
        {
            throw new RuntimeException("Vertex Buffer is null!");
        }

        if (this.vertex.isClosed())
        {
            throw new RuntimeException("Vertex Buffer is closed!");
        }

        if (this.alloc == null)
        {
            throw new RuntimeException("Allocator not valid!");
        }

        if (this.builder == null)
        {
            throw new RuntimeException("Buffer Builder not valid!");
        }

        RenderSystem.assertOnRenderThread();
    }

    public void reset()
    {
        if (this.vertex != null && !this.vertex.isClosed())
        {
            this.vertex.close();
            this.vertex = null;
            VertexBuffer.unbind();
        }

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
