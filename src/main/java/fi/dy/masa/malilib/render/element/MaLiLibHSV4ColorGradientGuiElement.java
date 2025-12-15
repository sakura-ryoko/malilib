package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;

public record MaLiLibHSV4ColorGradientGuiElement(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int x1,
        int x2,
        int y1,
        int y2,
        int[] colorPair,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState
{
    public MaLiLibHSV4ColorGradientGuiElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x1, int x2, int y1, int y2, int[] colorPair, @Nullable ScreenRectangle scissorArea)
    {
        this(pipeline, textureSetup, pose, x1, x2, y1, y2, colorPair, scissorArea, createBounds(x1, y1, x2, y2, pose, scissorArea));
    }

    @Override
    public void buildVertices(VertexConsumer vertices)
    {
        vertices.addVertexWith2DPose(this.pose(), this.x1(), this.y1()).setColor(this.colorPair()[0]);      // TOP LEFT
        vertices.addVertexWith2DPose(this.pose(), this.x1(), this.y2()).setColor(this.colorPair()[1]);      // TOP RIGHT
        vertices.addVertexWith2DPose(this.pose(), this.x2(), this.y2()).setColor(this.colorPair()[2]);      // BOTTOM RIGHT
        vertices.addVertexWith2DPose(this.pose(), this.x2(), this.y1()).setColor(this.colorPair()[3]);      // BOTTOM LEFT
    }

    @Nullable
    private static ScreenRectangle createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea)
    {
        ScreenRectangle screenRect = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
