package fi.dy.masa.malilib.interfaces;

import java.util.Map;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;

public interface IGuiRendererInvoker
{
    void malilib$replaceSpecialGuiRenderers(Map<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> map);
}
