package fi.dy.masa.malilib.render.element;

import java.awt.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

public record MaLiLibHSVColorSelectorGuiElement(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int xs,
        int ys,
        int w,
        int h,
        float hue,
        @Nullable ScreenRect scissorArea
) implements SimpleGuiElementRenderState
{
    @Override
    public void setupVertices(VertexConsumer vertices, float depth)
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

            vertices.vertex(this.pose(), this.xs(), y, depth).color(r1, g1, b1, a);
            vertices.vertex(this.pose(), x2, y, depth).color(r2, g2, b2, a);
        }
    }
}
