package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;

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
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState
{
    public MaLiLibHSV2ColorSegmentedHueGuiElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x, int y, int w, int h, int sw, int sh, int color1, int color2, @Nullable ScreenRectangle scissorArea)
    {
        this(pipeline, textureSetup, pose, x, y, w, h, sw, sh, color1, color2, scissorArea, createBounds(x, y, (x + w + sw), (y + h + sh), pose, scissorArea));
    }

    @Override
    public void buildVertices(VertexConsumer vertices)
    {
        int r1 = ((this.color1() >>> 16) & 0xFF);
        int g1 = ((this.color1() >>>  8) & 0xFF);
        int b1 = ( this.color1()         & 0xFF);
        int r2 = ((this.color2() >>> 16) & 0xFF);
        int g2 = ((this.color2() >>>  8) & 0xFF);
        int b2 = ( this.color2()         & 0xFF);
        int a = 255;

        vertices.addVertexWith2DPose(this.pose(), this.x(), this.y() + this.sh()).setColor(r1, g1, b1, a);
        vertices.addVertexWith2DPose(this.pose(), this.x() + this.w(), this.y() + this.h() + this.sh()).setColor(r1, g1, b1, a);
        vertices.addVertexWith2DPose(this.pose(), this.x() + this.w() + this.sw(), this.y() + this.h()).setColor(r2, g2, b2, a);
        vertices.addVertexWith2DPose(this.pose(), this.x() + this.sw(), this.y()).setColor(r2, g2, b2, a);
    }

    @Nullable
    private static ScreenRectangle createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea)
    {
        ScreenRectangle screenRect = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
