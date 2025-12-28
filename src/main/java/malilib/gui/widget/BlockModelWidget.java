package malilib.gui.widget;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;

import malilib.gui.util.ScreenContext;
import malilib.render.ModelRenderUtils;

public class BlockModelWidget extends BaseModelWidget
{
    @Nullable protected IBlockState state;
    @Nullable protected IBakedModel model;

    public BlockModelWidget(@Nullable IBlockState state)
    {
        this(16, state);
    }

    public BlockModelWidget(@Nullable Block block)
    {
        this(16, block);
    }

    public BlockModelWidget(int dimensions, @Nullable Block block)
    {
        this(dimensions, block != null ? block.getDefaultState() : null);
    }

    public BlockModelWidget(int dimensions, @Nullable IBlockState state)
    {
        super(dimensions);

        this.setState(state);
    }

    public BlockModelWidget setState(@Nullable IBlockState state)
    {
        this.state = state;

        if (state != null)
        {
            this.model = this.mc.getBlockRendererDispatcher().getModelForState(state);
        }
        else
        {
            this.model = null;
        }

        this.updateSize();

        return this;
    }

    @Override
    protected void renderModel(int x, int y, float z, float scale, ScreenContext ctx)
    {
        if (this.model != null)
        {
            ModelRenderUtils.renderModelInGui(x, y, z + 10, this.model, this.state, ctx);
        }
    }
}
