package fi.dy.masa.malilib.render.states;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

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
        @Nullable ScreenRect scissorArea
) implements SimpleGuiElementRenderState
{
    @Override
    public void setupVertices(VertexConsumer vertices, float depth)
    {
        int sa = (this.startColor() >> 24 & 0xFF);
        int sr = (this.startColor() >> 16 & 0xFF);
        int sg = (this.startColor() >> 8 & 0xFF);
        int sb = (this.startColor() & 0xFF);

        int ea = (this.endColor() >> 24 & 0xFF);
        int er = (this.endColor() >> 16 & 0xFF);
        int eg = (this.endColor() >> 8 & 0xFF);
        int eb = (this.endColor() & 0xFF);

        vertices.vertex(this.pose(), this.right(), this.top(),    depth).color(sr, sg, sb, sa);
        vertices.vertex(this.pose(), this.left(),  this.top(),    depth).color(sr, sg, sb, sa);
        vertices.vertex(this.pose(), this.left(),  this.bottom(), depth).color(er, eg, eb, ea);
        vertices.vertex(this.pose(), this.right(), this.bottom(), depth).color(er, eg, eb, ea);
    }
}
