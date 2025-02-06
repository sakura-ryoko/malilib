package fi.dy.masa.malilib.render;

import javax.annotation.Nullable;
import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;

public class RenderPhaseWrap extends RenderPhase implements AutoCloseable
{
    @Nullable
    private VertexBuffer vertex;

    public RenderPhaseWrap(String name, Runnable start, Runnable stop)
    {
        super(name, start, stop);
    }

    public VertexBuffer bind(VertexFormat fmt)
    {
        VertexBuffer vertex = fmt.getBuffer();
        this.bind(vertex);

        return vertex;
    }

    public void bind(VertexBuffer vertex)
    {
        if (vertex != this.vertex)
        {
            vertex.bind();
            this.vertex = vertex;
        }
    }

    public VertexBuffer upload(BuiltBuffer buffer)
    {
        VertexBuffer vertex = this.bind(buffer.getDrawParameters().format());
        vertex.upload(buffer);

        return vertex;
    }

    public void draw(BuiltBuffer buffer)
    {
        this.ensureSafe();
        VertexBuffer vertex = this.upload(buffer);
        vertex.draw();
    }

    public net.minecraft.client.gl.ShaderProgram setShader(ShaderProgramKey key)
    {
        return RenderSystem.setShader(key);
    }

    public void drawWithShaders(BuiltBuffer meshData) throws RuntimeException
    {
        this.drawWithShaders(meshData, RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
    }

    public void drawWithShaders(BuiltBuffer meshData, ShaderProgramKey shaderKey) throws RuntimeException
    {
        if (RenderSystem.isOnRenderThread())
        {
            this.ensureSafe();
            this.drawWithShaders(meshData, RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.setShader(shaderKey));
        }
    }

    public void drawWithShaders(BuiltBuffer meshData, net.minecraft.client.gl.ShaderProgram shader) throws RuntimeException
    {
        if (RenderSystem.isOnRenderThread())
        {
            this.ensureSafe();
            this.drawWithShaders(meshData, RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), shader);
        }
    }

    public void drawWithShaders(BuiltBuffer meshData, Matrix4f modelView, Matrix4f posMatrix, ShaderProgramKey shaderKey) throws RuntimeException
    {
        if (RenderSystem.isOnRenderThread())
        {
            this.ensureSafe();
            this.drawWithShaders(meshData, modelView, posMatrix, RenderSystem.setShader(shaderKey));
        }
    }

    public void drawWithShaders(BuiltBuffer meshData, Matrix4f modelView, Matrix4f posMatrix, net.minecraft.client.gl.ShaderProgram shader) throws RuntimeException
    {
        this.ensureSafe();
        VertexBuffer vertex = this.upload(meshData);
        vertex.draw(modelView, posMatrix, shader);
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

        RenderSystem.assertOnRenderThread();
    }

    public void reset()
    {
        if (this.vertex != null)
        {
            this.vertex.close();
            this.vertex = null;
            VertexBuffer.unbind();
        }
    }

    @Override
    public void close() throws Exception
    {
        this.reset();
    }
}
