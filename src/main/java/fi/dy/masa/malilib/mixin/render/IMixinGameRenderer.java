package fi.dy.masa.malilib.mixin.render;

import net.minecraft.client.gl.GlobalSettings;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface IMixinGameRenderer
{
    @Accessor("globalSettings")
    GlobalSettings malilib_getGlobalSettings();
}
