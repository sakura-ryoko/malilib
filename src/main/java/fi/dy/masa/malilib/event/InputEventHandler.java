package fi.dy.masa.malilib.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IInputManager;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import fi.dy.masa.malilib.hotkeys.IKeyboardInputHandler;
import fi.dy.masa.malilib.hotkeys.IMouseInputHandler;
import fi.dy.masa.malilib.hotkeys.KeybindCategory;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.util.InfoUtils;

public class InputEventHandler implements IKeybindManager, IInputManager
{
    private static final InputEventHandler INSTANCE = new InputEventHandler();

    private final MinecraftClient mc;
    private final Multimap<Integer, IKeybind> hotkeyMap = ArrayListMultimap.create();
    private final List<KeybindCategory> allKeybinds = new ArrayList<>();
    private final List<IKeybindProvider> keybindProviders = new ArrayList<>();
    private final List<IKeyboardInputHandler> keyboardHandlers = new ArrayList<>();
    private final List<IMouseInputHandler> mouseHandlers = new ArrayList<>();
    private double mouseWheelDeltaSum;

    private InputEventHandler()
    {
        this.mc = MinecraftClient.getInstance();
    }

    public static IKeybindManager getKeybindManager()
    {
        return INSTANCE;
    }

    public static IInputManager getInputManager()
    {
        return INSTANCE;
    }

    @Override
    public void registerKeybindProvider(IKeybindProvider provider)
    {
        if (this.keybindProviders.contains(provider) == false)
        {
            this.keybindProviders.add(provider);
        }

        provider.addHotkeys(this);
    }

    @Override
    public void unregisterKeybindProvider(IKeybindProvider provider)
    {
        this.keybindProviders.remove(provider);
    }

    @Override
    public List<KeybindCategory> getKeybindCategories()
    {
        return this.allKeybinds;
    }

    @Override
    public void updateUsedKeys()
    {
        this.hotkeyMap.clear();

        for (IKeybindProvider handler : this.keybindProviders)
        {
            handler.addKeysToMap(this);
        }
    }

    @Override
    public void addKeybindToMap(IKeybind keybind)
    {
        Collection<Integer> keys = keybind.getKeys();

        for (int key : keys)
        {
            this.hotkeyMap.put(key, keybind);
        }
    }

    @Override
    public void addHotkeysForCategory(String modName, String keyCategory, List<? extends IHotkey> hotkeys)
    {
        KeybindCategory cat = new KeybindCategory(modName, keyCategory, hotkeys);

        // Remove a previous entry, if any (matched based on the modName and keyCategory only!)
        this.allKeybinds.remove(cat);
        this.allKeybinds.add(cat);
    }

    @Override
    public void registerKeyboardInputHandler(IKeyboardInputHandler handler)
    {
        if (this.keyboardHandlers.contains(handler) == false)
        {
            this.keyboardHandlers.add(handler);
        }
    }

    @Override
    public void unregisterKeyboardInputHandler(IKeyboardInputHandler handler)
    {
        this.keyboardHandlers.remove(handler);
    }

    @Override
    public void registerMouseInputHandler(IMouseInputHandler handler)
    {
        if (this.mouseHandlers.contains(handler) == false)
        {
            this.mouseHandlers.add(handler);
        }
    }

    @Override
    public void unregisterMouseInputHandler(IMouseInputHandler handler)
    {
        this.mouseHandlers.remove(handler);
    }

    @ApiStatus.Internal
    public boolean onKeyInput(int keyCode, int scanCode, int modifiers, int action)
    {
        boolean eventKeyState = action != GLFW.GLFW_RELEASE;

        // Update the cached pressed keys status
        KeybindMulti.onKeyInputPre(keyCode, scanCode, modifiers, action);

        boolean cancel = this.checkKeyBindsForChanges(keyCode);

        if (this.keyboardHandlers.isEmpty() == false)
        {
            for (IKeyboardInputHandler handler : this.keyboardHandlers)
            {
                if (handler.onKeyInput(keyCode, scanCode, modifiers, eventKeyState))
                {
                    this.printInputCancellationDebugMessage(handler);
                    return true;
                }
            }
        }

        return cancel;
    }

    @ApiStatus.Internal
    public boolean onMouseClick(int mouseX, int mouseY, int eventButton, int action)
    {
        boolean cancel = false;

        if (eventButton != -1)
        {
            boolean eventButtonState = action == GLFW.GLFW_PRESS;

            // Update the cached pressed keys status
            KeybindMulti.onKeyInputPre(eventButton - 100, 0, 0, action);

            cancel = this.checkKeyBindsForChanges(eventButton - 100);

            if (this.mouseHandlers.isEmpty() == false)
            {
                for (IMouseInputHandler handler : this.mouseHandlers)
                {
                    if (handler.onMouseClick(mouseX, mouseY, eventButton, eventButtonState))
                    {
                        this.printInputCancellationDebugMessage(handler);
                        return true;
                    }
                }
            }
        }

        return cancel;
    }

    private void printInputCancellationDebugMessage(Object handler)
    {
        if (MaLiLibConfigs.Debug.INPUT_CANCELLATION_DEBUG.getBooleanValue())
        {
            String msg = String.format("Cancel requested by input handler '%s'", handler.getClass().getName());
            InfoUtils.showInGameMessage(Message.MessageType.INFO, msg);
            MaLiLib.logger.info(msg);
        }
    }

    @ApiStatus.Internal
    public boolean onMouseScroll(final int mouseX, final int mouseY, final double xOffset, final double yOffset)
    {
        boolean discrete = this.mc.options.getDiscreteMouseScroll().getValue();
        double sensitivity = this.mc.options.getMouseWheelSensitivity().getValue();
        double amount = (discrete ? Math.signum(yOffset) : yOffset) * sensitivity;

        if (MaLiLibConfigs.Debug.MOUSE_SCROLL_DEBUG.getBooleanValue())
        {
            int time = (int) (System.currentTimeMillis() & 0xFFFF);
            int tick = this.mc.world != null ? (int) (this.mc.world.getTime() & 0xFFFF) : 0;
            String timeStr = String.format("time: %04X, tick: %04X", time, tick);
            MaLiLib.logger.info("{} - xOffset: {}, yOffset: {}, discrete: {}, sensitivity: {}, amount: {}",
                                timeStr, xOffset, yOffset, discrete, sensitivity, amount);
        }

        if (amount != 0 && this.mouseHandlers.isEmpty() == false)
        {
            if (this.mouseWheelDeltaSum != 0.0 && Math.signum(amount) != Math.signum(this.mouseWheelDeltaSum))
            {
                this.mouseWheelDeltaSum = 0.0;
            }

            this.mouseWheelDeltaSum += amount;
            amount = (int) this.mouseWheelDeltaSum;

            if (amount != 0.0)
            {
                this.mouseWheelDeltaSum -= amount;

                for (IMouseInputHandler handler : this.mouseHandlers)
                {
                    if (handler.onMouseScroll(mouseX, mouseY, amount))
                    {
                        this.printInputCancellationDebugMessage(handler);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @ApiStatus.Internal
    public void onMouseMove(final int mouseX, final int mouseY)
    {
        if (this.mouseHandlers.isEmpty() == false)
        {
            for (IMouseInputHandler handler : this.mouseHandlers)
            {
                handler.onMouseMove(mouseX, mouseY);
            }
        }
    }

    private boolean checkKeyBindsForChanges(int eventKey)
    {
        boolean cancel = false;
        Collection<IKeybind> keybinds = this.hotkeyMap.get(eventKey);

        if (keybinds.isEmpty() == false)
        {
            for (IKeybind keybind : keybinds)
            {
                // Note: isPressed() has to get called for key releases too, to reset the state
                cancel |= keybind.updateIsPressed();
            }
        }

        return cancel;
    }
}
