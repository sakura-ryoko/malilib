package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

public record MaLiLibHSVColorHorizontalBarGuiElement(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int x,
        int y,
        int bw,
        int bh,
        float val,
        @Nullable ScreenRect scissorArea
) implements SimpleGuiElementRenderState
{
    @Override
    public void setupVertices(VertexConsumer vertices, float depth)
    {
        int xAdj = this.x();
        int yAdj = this.y();

        xAdj += (int) (this.bw() * this.val());
        int s = 2;
        int c = 255;

        vertices.vertex(this.pose(), xAdj - s, yAdj - s, depth).color(c, c, c, c);
        vertices.vertex(this.pose(), xAdj    , yAdj + s, depth).color(c, c, c, c);
        vertices.vertex(this.pose(), xAdj    , yAdj + s, depth).color(c, c, c, c);
        vertices.vertex(this.pose(), xAdj + s, yAdj - s, depth).color(c, c, c, c);

        yAdj += this.bh();

        vertices.vertex(this.pose(), xAdj - s, yAdj + s, depth).color(c, c, c, c);
        vertices.vertex(this.pose(), xAdj + s, yAdj + s, depth).color(c, c, c, c);
        vertices.vertex(this.pose(), xAdj    , yAdj - s, depth).color(c, c, c, c);
        vertices.vertex(this.pose(), xAdj    , yAdj - s, depth).color(c, c, c, c);
    }
}
