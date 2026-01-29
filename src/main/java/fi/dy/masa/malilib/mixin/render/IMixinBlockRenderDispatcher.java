package fi.dy.masa.malilib.mixin.render;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockRenderDispatcher.class)
public interface IMixinBlockRenderDispatcher
{
	@Accessor("blockColors")
	BlockColors malilib_getBlockColors();
}
