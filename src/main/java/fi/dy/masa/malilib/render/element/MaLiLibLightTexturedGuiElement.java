package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;

public record MaLiLibLightTexturedGuiElement(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int x1,
        int y1,
        int x2,
        int y2,
        float u1,
        float u2,
        float v1,
        float v2,
        int color,
        int light,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState
{
    public MaLiLibLightTexturedGuiElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x1, int y1, int x2, int y2, float u1, float u2, float v1, float v2, int color, int light, @Nullable ScreenRectangle scissorArea)
    {
        this(pipeline, textureSetup, pose, x1, y1, x2, y2, u1, u2, v1, v2, color, light, scissorArea, createBounds(x1, y1, x2, y2, pose, scissorArea));
    }

    @Override
    public void buildVertices(VertexConsumer vertices)
    {
        vertices.addVertexWith2DPose(this.pose(), (float)this.x1(), (float)this.y2()).setUv(this.u1(), this.v2()).setColor(this.color()).setLight(this.light());
        vertices.addVertexWith2DPose(this.pose(), (float)this.x2(), (float)this.y2()).setUv(this.u2(), this.v2()).setColor(this.color()).setLight(this.light());
        vertices.addVertexWith2DPose(this.pose(), (float)this.x2(), (float)this.y1()).setUv(this.u2(), this.v1()).setColor(this.color()).setLight(this.light());
        vertices.addVertexWith2DPose(this.pose(), (float)this.x1(), (float)this.y1()).setUv(this.u1(), this.v1()).setColor(this.color()).setLight(this.light());
    }

    @Nullable
    private static ScreenRectangle createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea)
    {
        ScreenRectangle screenRect = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
