package fi.dy.masa.malilib.render.element;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;

public record MaLiLibHSVColorVerticalBarMarkerGuiElement(
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
    public MaLiLibHSVColorVerticalBarMarkerGuiElement(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x, int y, int bw, int bh, float val, @Nullable ScreenRectangle scissorArea)
    {
        this(pipeline, textureSetup, pose, x, y, bw, bh, val, scissorArea, createBounds(x, y, x + (bw), y + (bh), pose, scissorArea));
    }

    @Override
    public void buildVertices(VertexConsumer vertices)
    {
        int xAdj = this.x();
        int yAdj = this.y();
        int bhAdj = this.bh();
        int bwAdj = this.bw();

        yAdj += (int) (bhAdj * (1f - this.val()));
        int s = 2;
        int c = 255;

        vertices.addVertexWith2DPose(this.pose(), xAdj - s, yAdj - s).setColor(c, c, c, c);
        vertices.addVertexWith2DPose(this.pose(), xAdj - s, yAdj + s).setColor(c, c, c, c);
        vertices.addVertexWith2DPose(this.pose(), xAdj + s, yAdj).setColor(c, c, c, c);
        vertices.addVertexWith2DPose(this.pose(), xAdj + s, yAdj).setColor(c, c, c, c);

        xAdj += (bwAdj);

        vertices.addVertexWith2DPose(this.pose(), xAdj + s, yAdj - s).setColor(c, c, c, c);
        vertices.addVertexWith2DPose(this.pose(), xAdj - s, yAdj).setColor(c, c, c, c);
        vertices.addVertexWith2DPose(this.pose(), xAdj - s, yAdj).setColor(c, c, c, c);
        vertices.addVertexWith2DPose(this.pose(), xAdj + s, yAdj + s).setColor(c, c, c, c);
    }

    @Nullable
    private static ScreenRectangle createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea)
    {
        ScreenRectangle screenRect = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
