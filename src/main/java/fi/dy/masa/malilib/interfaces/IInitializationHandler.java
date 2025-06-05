package fi.dy.masa.malilib.interfaces;

import java.nio.file.Path;

public interface IInitializationHandler
{
    /**
     * This method will be called for any registered <b>IInitializationHandler</b>
     * when the game has been initialized and the mods can register their keybinds and configs
     * to malilib without causing class loading issues.
     * <br><br>
     * So call all your (malilib-facing) mod init stuff inside this handler!
     */
    void registerModHandlers();

    /**
     * Callback as early as possible just after the game client's runDir
     * is defined after being invoked from main().
     * -
     * Note: that essentially *everything* has not yet been defined!
     * This is for registering various event callback hooks before
     * Minecraft itself initializes those systems; such as using
     * {@link fi.dy.masa.malilib.event.RenderEventHandler} to register
     * your SpecialGuiElementRenderer properly.
     *
     * @param runDir (Running Directory that Minecraft was launched in)
     */
    default void preGameInit(Path runDir) { }
}
