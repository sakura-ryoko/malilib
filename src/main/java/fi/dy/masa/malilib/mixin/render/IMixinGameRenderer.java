package fi.dy.masa.malilib.mixin.render;

import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GlobalSettingsUniform;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.state.GameRenderState;
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

    @Accessor("gameRenderState")
    GameRenderState malilib_getGameRenderState();

    @Accessor("lightmap")
    Lightmap malilib_getLightmap();

    @Accessor("renderBlockOutline")
    boolean malilib_isBlockOutlineEnabled();
}
