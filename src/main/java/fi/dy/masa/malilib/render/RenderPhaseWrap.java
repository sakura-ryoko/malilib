package fi.dy.masa.malilib.render;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;

public class RenderPhaseWrap extends RenderPhase
{
    @Nullable
    private static VertexBuffer buffer;

    public RenderPhaseWrap(String name, Runnable start, Runnable stop)
    {
        super(name, start, stop);
    }

    public static void drawWithShaders(BuiltBuffer buffer)
    {
        RenderSystem.assertOnRenderThreadOrInit();
        VertexBuffer vertex = upload(buffer);
        vertex.draw(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
    }

    public static void reset()
    {
        if (buffer != null)
        {
            buffer.close();
            buffer = null;
            VertexBuffer.unbind();
        }
    }

    public static void draw(BuiltBuffer buffer)
    {
        RenderSystem.assertOnRenderThreadOrInit();
        VertexBuffer vertex = upload(buffer);
        vertex.draw();
    }

    private static VertexBuffer upload(BuiltBuffer buffer)
    {
        VertexBuffer vertex = bind(buffer.getDrawParameters().format());
        vertex.upload(buffer);
        return vertex;
    }

    private static VertexBuffer bind(VertexFormat fmt)
    {
        VertexBuffer vertex = fmt.getBuffer();
        bind(vertex);
        return vertex;
    }

    private static void bind(VertexBuffer vertex)
    {
        if (vertex != buffer)
        {
            vertex.bind();
            buffer = vertex;
        }
    }
}
