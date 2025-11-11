package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

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
        @Nullable ScreenRect scissorArea,
        @Nullable ScreenRect bounds
) implements SimpleGuiElementRenderState
{
    public MaLiLibLightTexturedGuiElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x1, int y1, int x2, int y2, float u1, float u2, float v1, float v2, int color, int light, @Nullable ScreenRect scissorArea)
    {
        this(pipeline, textureSetup, pose, x1, y1, x2, y2, u1, u2, v1, v2, color, light, scissorArea, createBounds(x1, y1, x2, y2, pose, scissorArea));
    }

    @Override
    public void setupVertices(VertexConsumer vertices)
    {
        vertices.vertex(this.pose(), (float)this.x1(), (float)this.y2()).texture(this.u1(), this.v2()).color(this.color()).light(this.light());
        vertices.vertex(this.pose(), (float)this.x2(), (float)this.y2()).texture(this.u2(), this.v2()).color(this.color()).light(this.light());
        vertices.vertex(this.pose(), (float)this.x2(), (float)this.y1()).texture(this.u2(), this.v1()).color(this.color()).light(this.light());
        vertices.vertex(this.pose(), (float)this.x1(), (float)this.y1()).texture(this.u1(), this.v1()).color(this.color()).light(this.light());
    }

    @Nullable
    private static ScreenRect createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRect scissorArea)
    {
        ScreenRect screenRect = new ScreenRect(x0, y0, x1 - x0, y1 - y0).transformEachVertex(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
