package fi.dy.masa.malilib.mixin.render;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.block.BlockRenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockRenderManager.class)
public interface IMixinBlockRenderManager
{
	@Accessor("blockColors")
	BlockColors malilib_getBlockColors();
}
