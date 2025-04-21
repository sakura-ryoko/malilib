package fi.dy.masa.malilib.mixin.render;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldRenderer.class)
public interface IMixinWorldRenderer
{
    @Accessor("backgroundRenderer")
    BackgroundRenderer malilib_getBackgroundRenderer();
}
