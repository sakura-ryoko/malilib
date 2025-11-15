package fi.dy.masa.malilib.gui.interfaces;

import fi.dy.masa.malilib.config.IConfigBase;

public interface IConfigInfoProvider
{
    /**
     * Get the mouse-over hover info tooltip for the given config
     * @param config ()
     * @return ()
     */
    String getHoverInfo(IConfigBase config);
}
