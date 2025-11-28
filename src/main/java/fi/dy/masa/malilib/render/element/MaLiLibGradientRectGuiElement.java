package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;

public record MaLiLibGradientRectGuiElement(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        float left,
        float top,
        float right,
        float bottom,
        int startColor,
        int endColor,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState
{
    public MaLiLibGradientRectGuiElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, float left, float top, float right, float bottom, int startColor, int endColor, @Nullable ScreenRectangle scissorArea)
    {
        this(pipeline, textureSetup, pose, left, top, right, bottom, startColor, endColor, scissorArea, createBounds((int) left, (int) top, (int) right, (int) bottom, pose, scissorArea));
    }

    @Override
    public void buildVertices(VertexConsumer vertices)
    {
        int sa = (this.startColor() >> 24 & 0xFF);
        int sr = (this.startColor() >> 16 & 0xFF);
        int sg = (this.startColor() >> 8 & 0xFF);
        int sb = (this.startColor() & 0xFF);

        int ea = (this.endColor() >> 24 & 0xFF);
        int er = (this.endColor() >> 16 & 0xFF);
        int eg = (this.endColor() >> 8 & 0xFF);
        int eb = (this.endColor() & 0xFF);

        vertices.addVertexWith2DPose(this.pose(), this.right(), this.top()).setColor(sr, sg, sb, sa);
        vertices.addVertexWith2DPose(this.pose(), this.left(),  this.top()).setColor(sr, sg, sb, sa);
        vertices.addVertexWith2DPose(this.pose(), this.left(),  this.bottom()).setColor(er, eg, eb, ea);
        vertices.addVertexWith2DPose(this.pose(), this.right(), this.bottom()).setColor(er, eg, eb, ea);
    }

    @Nullable
    private static ScreenRectangle createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea)
    {
        ScreenRectangle screenRect = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
