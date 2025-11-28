package fi.dy.masa.malilib.mixin.render;

import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GlobalSettingsUniform;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface IMixinGameRenderer
{
    @Accessor("globalSettingsUniform")
    GlobalSettingsUniform malilib_getGlobalSettings();

    @Accessor("fogRenderer")
    FogRenderer malilib_getFogRenderer();

    @Accessor("guiRenderer")
    GuiRenderer malilib_getGuiRenderer();

    @Accessor("guiRenderState")
    GuiRenderState malilib_getGuiRenderState();
}
