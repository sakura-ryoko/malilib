package fi.dy.masa.malilib.render.special;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;

/**
 * DISABLED -- DOES NOT WORK, DO NOT USE
 */
@Deprecated
public record MaLiLibBlockStateModelGuiElement(
        BlockState state,
        int x1,
        int y1,
        int size,
        float zLevel,
        float scale,
        @Nullable ScreenRect scissorArea,
        @Nullable ScreenRect bounds
) implements SpecialGuiElementRenderState
{
    public MaLiLibBlockStateModelGuiElement(BlockState state, int x1, int y1, int size, float zLevel, float scale, @Nullable ScreenRect scissorArea)
    {
        this(state, x1, y1, size, zLevel, scale, scissorArea, SpecialGuiElementRenderState.createBounds(x1, y1, x1 + size, y1 + size, scissorArea));
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
