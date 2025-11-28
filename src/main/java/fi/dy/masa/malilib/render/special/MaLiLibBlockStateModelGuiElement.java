package fi.dy.masa.malilib.render.special;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * DISABLED -- DOES NOT WORK, DO NOT USE
 */
@Deprecated
@ApiStatus.Experimental
public record MaLiLibBlockStateModelGuiElement(
        BlockState state,
        int x0,
        int y0,
        int size,
        float scale,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState
{
    public MaLiLibBlockStateModelGuiElement(BlockState state, int x0, int y0, int size, float scale, @Nullable ScreenRectangle scissorArea)
    {
        this(state, x0, y0, size, scale, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x0 + size, y0 + size, scissorArea));
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
