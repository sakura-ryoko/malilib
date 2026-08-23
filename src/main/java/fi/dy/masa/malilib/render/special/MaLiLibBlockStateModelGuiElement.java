package fi.dy.masa.malilib.render.special;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;

/**
 * DISABLED -- DOES NOT WORK, DO NOT USE
 */
@ApiStatus.Experimental
public record MaLiLibBlockStateModelGuiElement(
        BlockState state,
        Quaternionf rotation,
        int x1,
        int y1,
        int size,
        float scale,        // I don't recommend changing this from ~0.80F
        float yOffset,      // Allows a coder to set the "Y-Offset" of the translation position of the Block in the GUI, which is additive to (0.50F)
        @Nullable ScreenRect scissorArea,
        @Nullable ScreenRect bounds
)
        implements SpecialGuiElementRenderState
{
    public MaLiLibBlockStateModelGuiElement(BlockState state,
                                            Quaternionf rotation,
                                            int x0, int y0,
                                            int size,
                                            float scale,
                                            float yOffset,
                                            @Nullable ScreenRect scissorArea)
    {
        this(state,
             rotation,
             x0, y0,
             size,
             scale,
             yOffset,
             scissorArea,
             SpecialGuiElementRenderState.createBounds(x0, y0, x0 + size, y0 + size, scissorArea)
        );
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
