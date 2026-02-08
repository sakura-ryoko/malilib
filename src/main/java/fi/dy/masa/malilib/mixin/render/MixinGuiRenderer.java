package fi.dy.masa.malilib.mixin.render;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import fi.dy.masa.malilib.interfaces.IGuiRendererInvoker;

@Mixin(value = GuiRenderer.class, priority = 990)
public abstract class MixinGuiRenderer implements IGuiRendererInvoker
{
    @Mutable @Shadow @Final private Map<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> pictureInPictureRenderers;

    @Override
    public void malilib$replaceSpecialGuiRenderers(Map<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> map)
    {
        this.pictureInPictureRenderers = new HashMap<>(map);
    }
}
