package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

public record MaLiLibHSV4ColorGradientGuiElement(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int x1,
        int x2,
        int y1,
        int y2,
        int[] colorPair,
        @Nullable ScreenRect scissorArea
) implements SimpleGuiElementRenderState
{
    @Override
    public void setupVertices(VertexConsumer vertices, float depth)
    {
        vertices.vertex(this.pose(), this.x1(), this.y1(), depth).color(this.colorPair()[0]);
        vertices.vertex(this.pose(), this.x1(), this.y2(), depth).color(this.colorPair()[1]);
        vertices.vertex(this.pose(), this.x2(), this.y2(), depth).color(this.colorPair()[2]);
        vertices.vertex(this.pose(), this.x2(), this.y1(), depth).color(this.colorPair()[3]);
    }
}
