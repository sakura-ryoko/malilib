package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

public record MaLiLibTexturedRectGuiElement(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int x,
        int y,
        int u,
        int v,
        int width,
        int height,
        int argb,
        @Nullable ScreenRect scissorArea,
        @Nullable ScreenRect bounds
) implements SimpleGuiElementRenderState
{
    public MaLiLibTexturedRectGuiElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x, int y, int u, int v, int width, int height, int argb, @Nullable ScreenRect scissorArea)
    {
        this(pipeline, textureSetup, pose, x, y, u, v, width, height, argb, scissorArea, createBounds(x, y, x + width, y + height, pose, scissorArea));
    }

    @Override
    public void setupVertices(VertexConsumer vertices)
    {
        float pixelWidth = 0.00390625F;

        vertices.vertex(this.pose(), this.x(), this.y() + this.height()).texture(this.u() * pixelWidth, (this.v() + this.height()) * pixelWidth).color(this.argb());
        vertices.vertex(this.pose(), this.x() + this.width(), this.y() + this.height()).texture((this.u() + this.width()) * pixelWidth, (this.v() + this.height()) * pixelWidth).color(this.argb());
        vertices.vertex(this.pose(), this.x() + this.width(), this.y()).texture((this.u() + this.width()) * pixelWidth, this.v() * pixelWidth).color(this.argb());
        vertices.vertex(this.pose(), this.x(), this.y()).texture(this.u() * pixelWidth, this.v() * pixelWidth).color(this.argb());
    }

    @Nullable
    private static ScreenRect createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRect scissorArea)
    {
        ScreenRect screenRect = new ScreenRect(x0, y0, x1 - x0, y1 - y0).transformEachVertex(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
