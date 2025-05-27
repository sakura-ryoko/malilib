package fi.dy.masa.malilib.mixin.render;

import net.minecraft.client.gl.GlobalSettings;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface IMixinGameRenderer
{
    @Accessor("globalSettings")
    GlobalSettings malilib_getGlobalSettings();

    @Accessor("fogRenderer")
    FogRenderer malilib_getFogRenderer();

    @Accessor("guiRenderer")
    GuiRenderer malilib_getGuiRenderer();

    @Accessor("guiState")
    GuiRenderState malilib_getGuiRenderState();
}
