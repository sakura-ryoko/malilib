package fi.dy.masa.malilib.render.special;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.world.level.block.state.BlockState;

@ApiStatus.Experimental
public record MaLiLibBlockStateGuiElement(
        BlockState state,
        Quaternionf rotation,
        int x0,
        int y0,
        int size,
        float scale,        // I don't recommend changing this from ~0.80F
        float yOffset,      // Allows a coder to set the "Y-Offset" of the translation position of the Block in the GUI, which is additive to (0.50F)
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
)
        implements PictureInPictureRenderState
{
    public MaLiLibBlockStateGuiElement(BlockState state,
                                       Quaternionf rotation,
                                       int x0, int y0,
                                       int size,
                                       float scale,
                                       float yOffset,
                                       @Nullable ScreenRectangle scissorArea)
    {
        this(state,
             rotation,
             x0, y0,
             size,
             scale,
             yOffset,
             scissorArea,
             PictureInPictureRenderState.getBounds(x0, y0, x0 + size, y0 + size, scissorArea)
        );
    }

	@Override
    public int x1()
    {
        return this.x0() + this.size();
    }

	@Override
    public int y1()
    {
        return this.y0() + this.size();
    }
}
