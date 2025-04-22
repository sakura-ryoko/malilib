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
        @Nullable ScreenRect scissorArea
) implements SimpleGuiElementRenderState
{
    @Override
    public void setupVertices(VertexConsumer vertices, float depth)
    {
        vertices.vertex(this.pose(), (float)this.x1(), (float)this.y2(), depth).texture(this.u1(), this.v2()).color(this.color()).light(this.light());
        vertices.vertex(this.pose(), (float)this.x2(), (float)this.y2(), depth).texture(this.u2(), this.v2()).color(this.color()).light(this.light());
        vertices.vertex(this.pose(), (float)this.x2(), (float)this.y1(), depth).texture(this.u2(), this.v1()).color(this.color()).light(this.light());
        vertices.vertex(this.pose(), (float)this.x1(), (float)this.y1(), depth).texture(this.u1(), this.v1()).color(this.color()).light(this.light());
    }
}
