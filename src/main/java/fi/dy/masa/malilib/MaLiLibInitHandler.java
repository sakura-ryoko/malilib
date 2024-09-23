package fi.dy.masa.malilib;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.test.TestRenderHandler;

public class MaLiLibInitHandler implements IInitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.getInstance().registerConfigHandler(MaLiLibReference.MOD_ID, new MaLiLibConfigs());

        InputEventHandler.getKeybindManager().registerKeybindProvider(MaLiLibInputHandler.getInstance());
        MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS.getKeybind().setCallback(new CallbackOpenConfigGui());

        IRenderer renderer = new TestRenderHandler();
        RenderEventHandler.getInstance().registerGameOverlayRenderer(renderer);
        RenderEventHandler.getInstance().registerTooltipLastRenderer(renderer);
        //RenderEventHandler.getInstance().registerWorldPreWeatherRenderer(renderer);
        //RenderEventHandler.getInstance().registerWorldLastRenderer(renderer);
    }

    private static class CallbackOpenConfigGui implements IHotkeyCallback
    {
        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            GuiBase.openGui(new MaLiLibConfigGui());
            return true;
        }
    }
}
