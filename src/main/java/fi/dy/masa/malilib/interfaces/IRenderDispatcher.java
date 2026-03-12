package fi.dy.masa.malilib.interfaces;

public interface IRenderDispatcher
{
    /**
     * Registers a renderer which will have its {@link IRenderer.onExtractInGameGuiPost}
     * method called after the vanilla In Game Gui extraction is done
     * @param renderer ()
     */
    void registerInGameGuiRenderer(IRenderer renderer);

    /**
     * Registers a renderer which will have its {@link IRenderer.onRenderTooltipLast}
     * method called after the vanilla tooltip text has been rendered.
     * @param renderer ()
     */
    void registerTooltipLastRenderer(IRenderer renderer);

    /**
     * Registers a renderer which will have its {@link IRenderer.onExtractWorldPreWeather}
     * method called before the vanilla Weather rendering is done
     * @param renderer ()
     */
    void registerWorldPreWeatherRenderer(IRenderer renderer);

    /**
     * Registers a renderer which will have its {@link IRenderer.onExtractWorldLast}
     * method called after the vanilla rendering is done except for debug
     * @param renderer ()
     */
    void registerWorldLastRenderer(IRenderer renderer);

    /**
     * Register this renderer with a Special Gui Render State / Renderer callback from Vanilla.
     * @param renderer ()
     */
    void registerSpecialGuiRenderer(IRenderer renderer);
}
