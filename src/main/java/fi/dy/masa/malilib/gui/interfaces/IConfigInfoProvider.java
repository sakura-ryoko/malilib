package fi.dy.masa.malilib.gui.interfaces;

import javax.annotation.Nullable;

import net.minecraft.network.chat.MutableComponent;

import fi.dy.masa.malilib.config.IConfigBase;

public interface IConfigInfoProvider
{
    /**
     * Get the mouse-over hover info tooltip for the given config
     * @param config ()
     * @return ()
     */
    String getHoverInfo(IConfigBase config);

    @Nullable
    default MutableComponent getHoverComponent(IConfigBase config) { return null; }
}
