package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

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
        @Nullable ScreenRect scissorArea
) implements SimpleGuiElementRenderState
{
    @Override
    public void setupVertices(VertexConsumer vertices, float depth)
    {
        int a1 = ((this.colorStart() >>> 24) & 0xFF);
        int r1 = ((this.colorStart() >>> 16) & 0xFF);
        int g1 = ((this.colorStart() >>>  8) & 0xFF);
        int b1 = (this.colorStart()          & 0xFF);
        int a2 = ((this.colorEnd() >>> 24) & 0xFF);
        int r2 = ((this.colorEnd() >>> 16) & 0xFF);
        int g2 = ((this.colorEnd() >>>  8) & 0xFF);
        int b2 = (this.colorEnd()          & 0xFF);

        vertices.vertex(this.pose(), this.x1(), this.y1(), depth).color(r1, g1, b1, a1);
        vertices.vertex(this.pose(), this.x1(), this.y2(), depth).color(r1, g1, b1, a1);
        vertices.vertex(this.pose(), this.x2(), this.y2(), depth).color(r2, g2, b2, a2);
        vertices.vertex(this.pose(), this.x2(), this.y1(), depth).color(r2, g2, b2, a2);
    }
}
