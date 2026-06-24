package fi.dy.masa.malilib.render.special;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * DISABLED -- DOES NOT WORK, DO NOT USE
 */
@Deprecated
@ApiStatus.Experimental
public record MaLiLibBlockStateModelGuiElement(
        BlockState state,
        int x1,
        int y1,
        int size,
        float scale,
        @Nullable ScreenRect scissorArea,
        @Nullable ScreenRect bounds
) implements SpecialGuiElementRenderState
{
    public MaLiLibBlockStateModelGuiElement(BlockState state, int x0, int y0, int size, float scale, @Nullable ScreenRect scissorArea)
    {
        this(state, x0, y0, size, scale, scissorArea, SpecialGuiElementRenderState.createBounds(x0, y0, x0 + size, y0 + size, scissorArea));
    }

	@Override
    public int x2()
    {
        return this.x1() + this.size();
    }

	@Override
    public int y2()
    {
        return this.y1() + this.size();
    }
}
