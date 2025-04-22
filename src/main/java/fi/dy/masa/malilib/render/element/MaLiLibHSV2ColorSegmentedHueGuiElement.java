package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

public record MaLiLibHSV2ColorSegmentedHueGuiElement(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int x,
        int y,
        int w,
        int h,
        int sw,
        int sh,
        int color1,
        int color2,
        @Nullable ScreenRect scissorArea
) implements SimpleGuiElementRenderState
{
    @Override
    public void setupVertices(VertexConsumer vertices, float depth)
    {
        int r1 = ((this.color1() >>> 16) & 0xFF);
        int g1 = ((this.color1() >>>  8) & 0xFF);
        int b1 = ( this.color1()         & 0xFF);
        int r2 = ((this.color2() >>> 16) & 0xFF);
        int g2 = ((this.color2() >>>  8) & 0xFF);
        int b2 = ( this.color2()         & 0xFF);
        int a = 255;

        vertices.vertex(this.pose(), this.x(), this.y() + this.sh(), depth).color(r1, g1, b1, a);
        vertices.vertex(this.pose(), this.x() + this.w(), this.y() + this.h() + this.sh(), depth).color(r1, g1, b1, a);
        vertices.vertex(this.pose(), this.x() + this.w() + this.sw(), this.y() + this.h(), depth).color(r2, g2, b2, a);
        vertices.vertex(this.pose(), this.x() + this.sw(), this.y(), depth).color(r2, g2, b2, a);
    }
}
