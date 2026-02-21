package fi.dy.masa.malilib;

import fi.dy.masa.malilib.command.ClientCommandHandler;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.test.command.TestCommand;
import fi.dy.masa.malilib.test.input.TestInputHandler;
import fi.dy.masa.malilib.test.misc.TestSelector;
import fi.dy.masa.malilib.test.render.TestRenderHandler;

public class MaLiLibInitHandler implements IInitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.getInstance().registerConfigHandler(MaLiLibReference.MOD_ID, new MaLiLibConfigs());

        InputEventHandler.getKeybindManager().registerKeybindProvider(MaLiLibInputHandler.getInstance());
        MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS.getKeybind().setCallback(new CallbackOpenConfigGui());

        if (MaLiLibReference.DEBUG_MODE)
        {
            InputEventHandler.getKeybindManager().registerKeybindProvider(TestInputHandler.getInstance());
            IRenderer renderer = new TestRenderHandler();
            RenderEventHandler.getInstance().registerGameOverlayRenderer(renderer);
            RenderEventHandler.getInstance().registerTooltipLastRenderer(renderer);
            RenderEventHandler.getInstance().registerWorldPreWeatherRenderer(renderer);
            RenderEventHandler.getInstance().registerWorldLastRenderer(renderer);

            ClientCommandHandler.INSTANCE.registerCommand(new TestCommand());
            TickHandler.getInstance().registerClientTickHandler(TestSelector.INSTANCE);

//            if (MaLiLibReference.EXPERIMENTAL_MODE)
//            {
//                TickHandler.getInstance().registerClientTickHandler(TestThreadDaemonDefaultHandler.INSTANCE);
//                TickHandler.getInstance().registerClientTickHandler(TestThreadDaemonAsyncHandler.INSTANCE);
//            }
//            else
//            {
//                TestThreadDaemonDefaultHandler.INSTANCE.endAll();
//                TestThreadDaemonAsyncHandler.INSTANCE.endAll();
//            }
        }

        MaLiLibCallbacks.init();
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
