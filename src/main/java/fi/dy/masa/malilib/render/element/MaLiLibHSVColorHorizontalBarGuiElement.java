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
        @Nullable ScreenRect scissorArea,
        @Nullable ScreenRect bounds
) implements SimpleGuiElementRenderState
{
    public MaLiLibHSVColorHorizontalBarGuiElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x, int y, int bw, int bh, float val, @Nullable ScreenRect scissorArea)
    {
        this(pipeline, textureSetup, pose, x, y, bh, bw, val, scissorArea, createBounds(x, y, x + bw, y + bh, pose, scissorArea));
    }

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

    @Nullable
    private static ScreenRect createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRect scissorArea)
    {
        ScreenRect screenRect = new ScreenRect(x0, y0, x1 - x0, y1 - y0).transformEachVertex(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
