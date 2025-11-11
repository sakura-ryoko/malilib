package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

public record MaLiLibHSV1ColorIndicatorGuiElement(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int x1,
        int x2,
        int y1,
        int y2,
        int r,
        int g,
        int b,
        int a,
        @Nullable ScreenRect scissorArea,
        @Nullable ScreenRect bounds
) implements SimpleGuiElementRenderState
{
    public MaLiLibHSV1ColorIndicatorGuiElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x1, int x2, int y1, int y2, int r, int g, int b, int a, @Nullable ScreenRect scissorArea)
    {
        this(pipeline, textureSetup, pose, x1, x2, y1, y2, r, g, b, a, scissorArea, createBounds(x1, y1, x2, y2, pose, scissorArea));
    }

    @Override
    public void setupVertices(VertexConsumer vertices)
    {
        vertices.vertex(this.pose(), this.x1(), this.y1()).color(this.r(), this.g(), this.b(), this.a());
        vertices.vertex(this.pose(), this.x1(), this.y2()).color(this.r(), this.g(), this.b(), this.a());
        vertices.vertex(this.pose(), this.x2(), this.y2()).color(this.r(), this.g(), this.b(), this.a());
        vertices.vertex(this.pose(), this.x2(), this.y1()).color(this.r(), this.g(), this.b(), this.a());
    }

    @Nullable
    private static ScreenRect createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRect scissorArea)
    {
        ScreenRect screenRect = new ScreenRect(x0, y0, x1 - x0, y1 - y0).transformEachVertex(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
