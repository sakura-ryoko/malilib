package fi.dy.masa.malilib.render.element;

import java.awt.*;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

public record MaLiLibHSVColorSelectorGuiElement(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int xs,
        int ys,
        int w,
        int h,
        float hue,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState
{
    public MaLiLibHSVColorSelectorGuiElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int xs, int ys, int w, int h, float hue, @Nullable ScreenRectangle scissorArea)
    {
        this(pipeline, textureSetup, pose, xs, ys, w, h, hue, scissorArea, createBounds(xs, ys, xs + w, ys + h, pose, scissorArea));
    }

    @Override
    public void buildVertices(@NotNull VertexConsumer vertices)
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

            vertices.addVertexWith2DPose(this.pose(), this.xs(), y).setColor(r1, g1, b1, a);
            vertices.addVertexWith2DPose(this.pose(), x2, y).setColor(r2, g2, b2, a);
        }
    }

    @Nullable
    private static ScreenRectangle createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea)
    {
        ScreenRectangle screenRect = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
