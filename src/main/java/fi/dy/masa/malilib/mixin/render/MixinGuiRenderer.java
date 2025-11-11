package fi.dy.masa.malilib.mixin.render;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import fi.dy.masa.malilib.interfaces.IGuiRendererInvoker;

@Mixin(GuiRenderer.class)
public abstract class MixinGuiRenderer implements IGuiRendererInvoker
{
    @Mutable @Shadow @Final private Map<Class<? extends SpecialGuiElementRenderState>, SpecialGuiElementRenderer<?>> specialElementRenderers;

    @Override
    public void malilib$replaceSpecialGuiRenderers(Map<Class<? extends SpecialGuiElementRenderState>, SpecialGuiElementRenderer<?>> map)
    {
        this.specialElementRenderers = new HashMap<>(map);
    }
}
