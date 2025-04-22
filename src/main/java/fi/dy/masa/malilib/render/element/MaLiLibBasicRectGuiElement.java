package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

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
        @Nullable ScreenRect scissorArea
) implements SimpleGuiElementRenderState
{
    @Override
    public void setupVertices(VertexConsumer vertices, float depth)
    {
        float a = (float) (this.color() >> 24 & 255) / 255.0F;
        float r = (float) (this.color() >> 16 & 255) / 255.0F;
        float g = (float) (this.color() >> 8 & 255) / 255.0F;
        float b = (float) (this.color() & 255) / 255.0F;

        vertices.vertex(this.pose(), this.x() * this.scale(), this.y() * this.scale(), depth).color(r, g, b, a);
        vertices.vertex(this.pose(), this.x() * this.scale(), (this.y() + this.height()) * this.scale(), depth).color(r, g, b, a);
        vertices.vertex(this.pose(), (this.x() + this.width()) * this.scale(), (this.y() + this.height()) * this.scale(), depth).color(r, g, b, a);
        vertices.vertex(this.pose(), (this.x() + this.width()) * this.scale(), this.y() * this.scale(), depth).color(r, g, b, a);
    }
}
