package fi.dy.masa.malilib.mixin.render;

import java.util.Map;

import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiRenderer.class)
public interface IMixinGuiRenderer
{
    @Accessor("specialElementRenderers")
    Map<Class<? extends SpecialGuiElementRenderState>, SpecialGuiElementRenderer<?>> malilib_getSpecialGuiRenderers();
}
