package fi.dy.masa.malilib.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemModelResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface IMixinMinecraft
{
	@Accessor("itemModelResolver")
	ItemModelResolver malilib_getItemModelResolver();
}
