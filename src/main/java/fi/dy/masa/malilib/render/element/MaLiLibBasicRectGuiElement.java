package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

public record MaLiLibBasicRectGuiElement(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int x,
        int y,
        int width,
        int height,
        float scale,
        int color,
        @Nullable ScreenRect scissorArea,
        @Nullable ScreenRect bounds
) implements SimpleGuiElementRenderState
{
    public MaLiLibBasicRectGuiElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x, int y, int width, int height, float scale, int color, @Nullable ScreenRect scissorArea)
    {
        this(pipeline, textureSetup, pose, x, y, width, height, scale, color, scissorArea, createBounds(x, y, (x + width), (y + height), scale, pose, scissorArea));
    }

    @Override
    public void setupVertices(VertexConsumer vertices)
    {
        float a = (float) (this.color() >> 24 & 255) / 255.0F;
        float r = (float) (this.color() >> 16 & 255) / 255.0F;
        float g = (float) (this.color() >> 8 & 255) / 255.0F;
        float b = (float) (this.color() & 255) / 255.0F;

        vertices.vertex(this.pose(), this.x() * this.scale(), this.y() * this.scale()).color(r, g, b, a);
        vertices.vertex(this.pose(), this.x() * this.scale(), (this.y() + this.height()) * this.scale()).color(r, g, b, a);
        vertices.vertex(this.pose(), (this.x() + this.width()) * this.scale(), (this.y() + this.height()) * this.scale()).color(r, g, b, a);
        vertices.vertex(this.pose(), (this.x() + this.width()) * this.scale(), this.y() * this.scale()).color(r, g, b, a);
    }

    @Nullable
    private static ScreenRect createBounds(int x0, int y0, int x1, int y1, float scale, Matrix3x2f pose, @Nullable ScreenRect scissorArea)
    {
        ScreenRect screenRect = new ScreenRect(x0, y0, (int) (x1 * scale) - x0, (int) (y1 * scale) - y0).transformEachVertex(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
