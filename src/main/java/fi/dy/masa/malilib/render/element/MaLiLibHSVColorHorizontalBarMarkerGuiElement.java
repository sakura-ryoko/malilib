package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

public record MaLiLibHSVColorHorizontalBarMarkerGuiElement(
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
    public MaLiLibHSVColorHorizontalBarMarkerGuiElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x, int y, int bw, int bh, float val, @Nullable ScreenRect scissorArea)
    {
        this(pipeline, textureSetup, pose, x, y, bh, bw, val, scissorArea, createBounds((x), (y - 2), x + (int) (bw * 7.5), y + (bh / 6), pose, scissorArea));
    }

    @Override
    public void setupVertices(VertexConsumer vertices)
    {
        float xAdj = this.x();
        float yAdj = (float) (this.y() - 1.5);
        float bwAdj = (float) (this.bw() * 7.5);
        float bhAdj = (float) this.bh() / 6;

        xAdj += (bwAdj * this.val());
        final int s = 2;
        final int c = 255;

        vertices.vertex(this.pose(), xAdj - s, yAdj - s).color(c, c, c, c);
        vertices.vertex(this.pose(), xAdj    , yAdj + s).color(c, c, c, c);
        vertices.vertex(this.pose(), xAdj    , yAdj + s).color(c, c, c, c);
        vertices.vertex(this.pose(), xAdj + s, yAdj - s).color(c, c, c, c);

        yAdj += (bhAdj);

        vertices.vertex(this.pose(), xAdj - s, yAdj + s).color(c, c, c, c);
        vertices.vertex(this.pose(), xAdj + s, yAdj + s).color(c, c, c, c);
        vertices.vertex(this.pose(), xAdj    , yAdj - s).color(c, c, c, c);
        vertices.vertex(this.pose(), xAdj    , yAdj - s).color(c, c, c, c);
    }

    @Nullable
    private static ScreenRect createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRect scissorArea)
    {
        ScreenRect screenRect = new ScreenRect(x0, y0, x1 - x0, y1 - y0).transformEachVertex(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
