package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;

public record MaLiLibHSVColorHorizontalBarMarkerGuiElement(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        int x,
        int y,
        int bw,
        int bh,
        float val,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState
{
    public MaLiLibHSVColorHorizontalBarMarkerGuiElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x, int y, int bw, int bh, float val, @Nullable ScreenRectangle scissorArea)
    {
        this(pipeline, textureSetup, pose, x, y, bh, bw, val, scissorArea, createBounds((x), (y - 2), x + (int) (bw * 7.5), y + (bh / 6), pose, scissorArea));
    }

    @Override
    public void buildVertices(VertexConsumer vertices)
    {
        float xAdj = this.x();
        float yAdj = (float) (this.y() - 1.5);
        float bwAdj = (float) (this.bw() * 7.5);
        float bhAdj = (float) this.bh() / 6;

        xAdj += (bwAdj * this.val());
        final int s = 2;
        final int c = 255;

        vertices.addVertexWith2DPose(this.pose(), xAdj - s, yAdj - s).setColor(c, c, c, c);
        vertices.addVertexWith2DPose(this.pose(), xAdj    , yAdj + s).setColor(c, c, c, c);
        vertices.addVertexWith2DPose(this.pose(), xAdj    , yAdj + s).setColor(c, c, c, c);
        vertices.addVertexWith2DPose(this.pose(), xAdj + s, yAdj - s).setColor(c, c, c, c);

        yAdj += (bhAdj);

        vertices.addVertexWith2DPose(this.pose(), xAdj - s, yAdj + s).setColor(c, c, c, c);
        vertices.addVertexWith2DPose(this.pose(), xAdj + s, yAdj + s).setColor(c, c, c, c);
        vertices.addVertexWith2DPose(this.pose(), xAdj    , yAdj - s).setColor(c, c, c, c);
        vertices.addVertexWith2DPose(this.pose(), xAdj    , yAdj - s).setColor(c, c, c, c);
    }

    @Nullable
    private static ScreenRectangle createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea)
    {
        ScreenRectangle screenRect = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
