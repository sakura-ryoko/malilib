package malilib.render.overlay;

import javax.annotation.Nullable;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;

public abstract class BaseColoredQuadOverlayRenderer extends BaseOverlayRenderer
{
    protected static final VertexBuilder COLORED_QUADS_BUILDER = VanillaWrappingVertexBuilder.create(2097152, GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
    protected static final VertexBuilder COLORED_LINES_BUILDER = VanillaWrappingVertexBuilder.create(2097152, GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
    protected static final VertexBuilder COLORED_TEXTURED_QUADS_BUILDER = VanillaWrappingVertexBuilder.create(2097152, GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

    protected final VertexBuilder quadBuilder;
    protected final VertexBuilder lineBuilder;
    @Nullable protected BaseRenderObject quadRenderer;
    @Nullable protected BaseRenderObject outlineRenderer;

    public BaseColoredQuadOverlayRenderer()
    {
        this(COLORED_QUADS_BUILDER, COLORED_LINES_BUILDER);
    }

    public BaseColoredQuadOverlayRenderer(VertexBuilder quadBuilder, VertexBuilder lineBuilder)
    {
        this.quadBuilder = quadBuilder;
        this.lineBuilder = lineBuilder;
    }

    @Override
    protected void startBuffers()
    {
        this.quadBuilder.start();
        this.lineBuilder.start();
    }

    @Override
    protected void uploadBuffers()
    {
        this.quadRenderer.uploadData(this.quadBuilder);
        this.outlineRenderer.uploadData(this.lineBuilder);
        this.needsUpdate = false;
    }

    @Override
    public void draw()
    {
        this.preRender();

        this.quadRenderer.draw();
        this.outlineRenderer.draw();

        this.postRender();
    }

    @Override
    public void allocateGlResources()
    {
        this.quadRenderer = this.allocateBuffer(GL11.GL_QUADS);
        this.outlineRenderer = this.allocateBuffer(GL11.GL_LINES);
    }

    @Override
    public void deleteGlResources()
    {
        if (this.quadRenderer != null)
        {
            this.quadRenderer.deleteGlResources();
            this.quadRenderer = null;
        }

        if (this.outlineRenderer != null)
        {
            this.outlineRenderer.deleteGlResources();
            this.outlineRenderer = null;
        }
    }
}
