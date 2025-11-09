package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;

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
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState
{
    public MaLiLibBasicRectGuiElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x, int y, int width, int height, float scale, int color, @Nullable ScreenRectangle scissorArea)
    {
        this(pipeline, textureSetup, pose, x, y, width, height, scale, color, scissorArea, createBounds(x, y, (x + width), (y + height), scale, pose, scissorArea));
    }

    @Override
    public void buildVertices(VertexConsumer vertices)
    {
        float a = (float) (this.color() >> 24 & 255) / 255.0F;
        float r = (float) (this.color() >> 16 & 255) / 255.0F;
        float g = (float) (this.color() >> 8 & 255) / 255.0F;
        float b = (float) (this.color() & 255) / 255.0F;

        vertices.addVertexWith2DPose(this.pose(), this.x() * this.scale(), this.y() * this.scale()).setColor(r, g, b, a);
        vertices.addVertexWith2DPose(this.pose(), this.x() * this.scale(), (this.y() + this.height()) * this.scale()).setColor(r, g, b, a);
        vertices.addVertexWith2DPose(this.pose(), (this.x() + this.width()) * this.scale(), (this.y() + this.height()) * this.scale()).setColor(r, g, b, a);
        vertices.addVertexWith2DPose(this.pose(), (this.x() + this.width()) * this.scale(), this.y() * this.scale()).setColor(r, g, b, a);
    }

    @Nullable
    private static ScreenRectangle createBounds(int x0, int y0, int x1, int y1, float scale, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea)
    {
        ScreenRectangle screenRect = new ScreenRectangle(x0, y0, (int) (x1 * scale) - x0, (int) (y1 * scale) - y0).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
