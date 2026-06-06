package fi.dy.masa.malilib.mixin.render;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface IMixinLevelRenderer
{
	@Accessor("submitNodeStorage")
	SubmitNodeStorage malilib_getSubmitNodeStorage();
}
