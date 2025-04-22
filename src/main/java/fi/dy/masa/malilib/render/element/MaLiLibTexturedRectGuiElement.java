package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

public record MaLiLibTexturedRectGuiElement(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int x,
        int y,
        int u,
        int v,
        int width,
        int height,
        float zLevel,
        int argb,
        @Nullable ScreenRect scissorArea
) implements SimpleGuiElementRenderState
{
    @Override
    public void setupVertices(VertexConsumer vertices, float depth)
    {
        float pixelWidth = 0.00390625F;

        vertices.vertex(this.pose(), this.x(), this.y() + this.height(), this.zLevel()).texture(this.u() * pixelWidth, (this.v() + this.height()) * pixelWidth).color(this.argb());
        vertices.vertex(this.pose(), this.x() + this.width(), this.y() + this.height(), this.zLevel()).texture((this.u() + this.width()) * pixelWidth, (this.v() + this.height()) * pixelWidth).color(this.argb());
        vertices.vertex(this.pose(), this.x() + this.width(), this.y(), this.zLevel()).texture((this.u() + this.width()) * pixelWidth, this.v() * pixelWidth).color(this.argb());
        vertices.vertex(this.pose(), this.x(), this.y(), this.zLevel()).texture(this.u() * pixelWidth, this.v() * pixelWidth).color(this.argb());
    }
}
