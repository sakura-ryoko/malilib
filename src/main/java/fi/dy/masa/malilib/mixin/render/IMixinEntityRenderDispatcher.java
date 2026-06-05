package fi.dy.masa.malilib.mixin.render;

import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderDispatcher.class)
public interface IMixinEntityRenderDispatcher
{
	@Accessor("blockModelResolver")
	BlockModelResolver malilib_getBlockModelResolver();
}
