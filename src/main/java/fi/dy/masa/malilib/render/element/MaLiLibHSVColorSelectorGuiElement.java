package fi.dy.masa.malilib.render.element;

import java.awt.*;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;

public record MaLiLibHSVColorSelectorGuiElement(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int xs,
        int ys,
        int w,
        int h,
        float hue,
        @Nullable ScreenRect scissorArea,
        @Nullable ScreenRect bounds
) implements SimpleGuiElementRenderState
{
    public MaLiLibHSVColorSelectorGuiElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int xs, int ys, int w, int h, float hue, @Nullable ScreenRect scissorArea)
    {
        this(pipeline, textureSetup, pose, xs, ys, w, h, hue, scissorArea, createBounds(xs, ys, xs + w, ys + h, pose, scissorArea));
    }

    @Override
    public void setupVertices(VertexConsumer vertices)
    {
        int x2 = this.xs() + this.w();

        for (int y = this.ys(); y <= this.ys() + this.h(); ++y)
        {
            float saturation = 1f - ((float) (y - this.ys()) / (float) this.h());
            int color1 = Color.HSBtoRGB(this.hue(), saturation, 0f);
            int color2 = Color.HSBtoRGB(this.hue(), saturation, 1f);
            int r1 = ((color1 >>> 16) & 0xFF);
            int g1 = ((color1 >>>  8) & 0xFF);
            int b1 = ( color1         & 0xFF);
            int r2 = ((color2 >>> 16) & 0xFF);
            int g2 = ((color2 >>>  8) & 0xFF);
            int b2 = ( color2         & 0xFF);
            int a = 255;

            vertices.vertex(this.pose(), this.xs(), y).color(r1, g1, b1, a);
            vertices.vertex(this.pose(), x2, y).color(r2, g2, b2, a);
        }
    }

    @Nullable
    private static ScreenRect createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRect scissorArea)
    {
        ScreenRect screenRect = new ScreenRect(x0, y0, x1 - x0, y1 - y0).transformEachVertex(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
