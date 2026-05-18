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
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.test.command.TestCommand;
import fi.dy.masa.malilib.test.input.TestInputHandler;
import fi.dy.masa.malilib.test.misc.TestSelector;
import fi.dy.masa.malilib.test.render.TestRenderHandler;
import fi.dy.masa.malilib.util.i18n.i18nMode;

public class MaLiLibInitHandler implements IInitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.getInstance().registerConfigHandler(MaLiLibReference.MOD_ID, new MaLiLibConfigs());
	    MaLiLibConfigs.LANG.ifPresent(
                i18nManager ->
                        Registry.TRANSLATION_OVERRIDE_MANAGER.registerTranslationManager(MaLiLibReference.MOD_ID, i18nManager,
                                                                                         (i18nMode) MaLiLibConfigs.Generic.TRANSLATION_MODE.getOptionListValue())
        );
        InputEventHandler.getKeybindManager().registerKeybindProvider(MaLiLibInputHandler.getInstance());
        MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS.getKeybind().setCallback(new CallbackOpenConfigGui());
        MaLiLibConfigs.Generic.TRANSLATION_MODE.setValueChangeCallback(
                cfg ->
                        Registry.TRANSLATION_OVERRIDE_MANAGER.registerLanguageMode(MaLiLibReference.MOD_ID, (i18nMode) cfg.getOptionListValue())
        );

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
//                TickHandler.getInstance().registerClientTickHandler(TestThreadDaemonHandler.INSTANCE);
//            }
//            else
//            {
//                TestThreadDaemonHandler.INSTANCE.endAll();
//            }

            /*
            if (MaLiLibReference.EXPERIMENTAL_MODE)
            {
                OnDemandRenderer.getInstance().registerOnDemandRenderer(
                        MaLiLibReference.MOD_ID+"_block_targeting_overlay",
                        new BlockTargetingOverlayRenderer(MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN, true, false)
                );
            }
            */
        }

//        RenderEventHandler.getInstance().registerWorldLastRenderer(OnDemandRenderer.getInstance());
//        TickHandler.getInstance().registerClientTickHandler(OnDemandRenderer.getInstance());
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
