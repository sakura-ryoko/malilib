package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;

public record MaLiLibHSV2ColorGradientGuiElement(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int x1,
        int x2,
        int y1,
        int y2,
        int colorStart,
        int colorEnd,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState
{
    public MaLiLibHSV2ColorGradientGuiElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x1, int x2, int y1, int y2, int colorStart, int colorEnd, @Nullable ScreenRectangle scissorArea)
    {
        this(pipeline, textureSetup, pose, x1, x2, y1, y2, colorStart, colorEnd, scissorArea, createBounds(x1, y1, x2, y2, pose, scissorArea));
    }

    @Override
    public void buildVertices(VertexConsumer vertices)
    {
        int a1 = ((this.colorStart() >>> 24) & 0xFF);
        int r1 = ((this.colorStart() >>> 16) & 0xFF);
        int g1 = ((this.colorStart() >>>  8) & 0xFF);
        int b1 = (this.colorStart()          & 0xFF);
        int a2 = ((this.colorEnd() >>> 24) & 0xFF);
        int r2 = ((this.colorEnd() >>> 16) & 0xFF);
        int g2 = ((this.colorEnd() >>>  8) & 0xFF);
        int b2 = (this.colorEnd()          & 0xFF);

        vertices.addVertexWith2DPose(this.pose(), this.x1(), this.y1()).setColor(r1, g1, b1, a1);
        vertices.addVertexWith2DPose(this.pose(), this.x1(), this.y2()).setColor(r1, g1, b1, a1);
        vertices.addVertexWith2DPose(this.pose(), this.x2(), this.y2()).setColor(r2, g2, b2, a2);
        vertices.addVertexWith2DPose(this.pose(), this.x2(), this.y1()).setColor(r2, g2, b2, a2);
    }

    @Nullable
    private static ScreenRectangle createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea)
    {
        ScreenRectangle screenRect = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
