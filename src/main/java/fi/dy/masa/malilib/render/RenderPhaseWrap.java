package fi.dy.masa.malilib.render;

import javax.annotation.Nullable;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderPipeline;
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

    public void draw(BuiltBuffer meshData)
    {
        this.ensureSafe();
        VertexBuffer vertex = this.bind(meshData.getDrawParameters().format());
        vertex.upload(meshData);
        vertex.draw();
    }

    public void drawWithShaders(BuiltBuffer meshData, ShaderPipeline shaderKey) throws RuntimeException
    {
        this.ensureSafe();
        this.drawWithShaders(meshData, RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), shaderKey);
    }

    public void drawWithShaders(BuiltBuffer meshData, Matrix4f modelView, Matrix4f posMatrix, ShaderPipeline shaderKey) throws RuntimeException
    {
        this.ensureSafe();
        VertexBuffer vertex = this.bind(meshData.getDrawParameters().format());
        vertex.upload(meshData);
        vertex.draw(modelView, posMatrix, shaderKey.getProgram());
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
