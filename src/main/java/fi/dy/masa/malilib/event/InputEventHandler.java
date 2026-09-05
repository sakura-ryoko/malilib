package fi.dy.masa.malilib.event;

import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.sdl.SDL_Event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.hotkeys.*;
import fi.dy.masa.malilib.util.FileNameUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.input.*;
import fi.dy.masa.malilib.util.position.Vec2d;

public class InputEventHandler implements IKeybindManager, IInputManager
{
    private static final InputEventHandler INSTANCE = new InputEventHandler();

    private final Multimap<Integer, IKeybind> hotkeyMap = ArrayListMultimap.create();
    private final List<KeybindCategory> allKeybinds = new ArrayList<>();
    private final List<IKeybindProvider> keybindProviders = new ArrayList<>();
    private final List<IKeyboardInputHandler> keyboardHandlers = new ArrayList<>();
    private final List<IMouseInputHandler> mouseHandlers = new ArrayList<>();
    private double mouseWheelDeltaSum;
    private KeyState lastKeyState;
    private int lastAction;
    private int lastScanCode;
    private Vec2d lastMousePos;

    private InputEventHandler() { }

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

    @Nullable
    public KeyState getLastKeyState()
    {
        return this.lastKeyState;
    }

    public int getLastScanCode()
    {
        return this.lastScanCode;
    }

    public int getLastActionCode()
    {
        return this.lastAction;
    }

    private void setLastMousePos(final double mouseX, final double mouseY)
    {
        this.lastMousePos = new Vec2d(mouseX, mouseY);
    }

    public Vec2d getLastMousePos()
    {
        return this.lastMousePos;
    }

    // todo - WARNING (Does not run on Minecraft Thread)
    @ApiStatus.Internal
    public boolean onHandleEvent(final SDL_Event event)
    {
        this.lastKeyState = KeyState.fromEventType(event.type());

        switch (event.type())
        {
            case EventCodes.EVENT_KEY_PRESS ->
            {
                this.lastAction = event.key().repeat() ? ActionCodes.REPEAT : ActionCodes.PRESSED;
                this.lastScanCode = event.key().scancode();
            }
            case EventCodes.EVENT_KEY_RELEASE ->
            {
                this.lastAction = ActionCodes.RELEASED;
                this.lastScanCode = event.key().scancode();
            }
            case EventCodes.EVENT_MOUSE_PRESS ->
            {
                this.lastAction = ActionCodes.PRESSED;
                this.lastScanCode = event.button().button() - ScanCodes.OFFSET_MOUSE;
            }
            case EventCodes.EVENT_MOUSE_RELEASE ->
            {
                this.lastAction = ActionCodes.RELEASED;
                this.lastScanCode = event.button().button() - ScanCodes.OFFSET_MOUSE;
            }
	        default ->
	        {
                this.lastAction = ActionCodes.NONE;
                this.lastScanCode = ScanCodes.SCAN_UNKNOWN;
	        }
        }

        System.out.printf("onHandleEvent():%s: state: %s, scanCode: %d\n", Thread.currentThread().getName(),
                          this.lastKeyState != null ? this.lastKeyState.toString() : "<>",
                          this.lastScanCode);

        // Cancel further processing -> true
        return false;
    }

    // todo - WARNING (Does not run on Minecraft Thread)
    @ApiStatus.Internal
    public void onHandleKeymapChange()
    {
        // TODO
    }

    // todo - WARNING (Does not run on Minecraft Thread)
    @ApiStatus.Internal
    public void onHandleDropStart()
    {
        // TODO
    }

    @ApiStatus.Internal
    public boolean onKeyInput(KeyEvent input, int action, @Nonnull Minecraft mc)
    {
        boolean eventKeyState = action != ActionCodes.RELEASED;

        // Update the cached pressed keys status
        KeybindMulti.onKeyInputPre(input, action);

        boolean cancel = this.checkKeyBindsForChanges(input.key());

        if (this.keyboardHandlers.isEmpty() == false)
        {
            for (IKeyboardInputHandler handler : this.keyboardHandlers)
            {
                if (handler.onKeyInput(input, eventKeyState))
                {
                    this.printInputCancellationDebugMessage(handler);
                    return true;
                }
            }
        }

        return cancel;
    }

    @ApiStatus.Internal
    public boolean onMouseClick(MouseButtonEvent click, int action)
    {
        boolean cancel = false;
        this.setLastMousePos(click.x(), click.y());

        if (click.input() != ScanCodes.SCAN_UNKNOWN)
        {
            boolean eventButtonState = action == ActionCodes.PRESSED;

            // Update the cached pressed keys status
			KeybindMulti.onKeyInputPre(new KeyEvent(click.input() - ScanCodes.OFFSET_MOUSE, KeyCodes.KEY_UNKNOWN, KeyCodes.KMOD_NONE), action);

            cancel = this.checkKeyBindsForChanges(click.input() - ScanCodes.OFFSET_MOUSE);

            if (this.mouseHandlers.isEmpty() == false)
            {
                for (IMouseInputHandler handler : this.mouseHandlers)
                {
                    if (handler.onMouseClick(click, eventButtonState))
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
            MaLiLib.LOGGER.info(msg);
        }
    }

    @ApiStatus.Internal
    public boolean onMouseScroll(final double mouseX, final double mouseY, final double xOffset, final double yOffset)
    {
        Minecraft mc = Minecraft.getInstance();
        boolean discrete = mc.options.discreteMouseScroll().get();
        double sensitivity = mc.options.mouseWheelSensitivity().get();
        double amount = (discrete ? Math.signum(yOffset) : yOffset) * sensitivity;

        this.setLastMousePos(mouseX, mouseY);

        if (MaLiLibConfigs.Debug.MOUSE_SCROLL_DEBUG.getBooleanValue())
        {
            int time = (int) (System.currentTimeMillis() & 0xFFFF);
            int tick = mc.level != null ? (int) (mc.level.getGameTime() & 0xFFFF) : 0;
            String timeStr = String.format("time: %04X, tick: %04X", time, tick);
            MaLiLib.LOGGER.info("{} - xOffset: {}, yOffset: {}, discrete: {}, sensitivity: {}, amount: {}",
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
    public void onMouseMove(final double mouseX, final double mouseY)
    {
        this.setLastMousePos(mouseX, mouseY);

        if (this.mouseHandlers.isEmpty() == false)
        {
            for (IMouseInputHandler handler : this.mouseHandlers)
            {
                handler.onMouseMove(mouseX, mouseY);
            }
        }
    }

    @ApiStatus.Internal
    public boolean onMouseDrop(final double mouseX, final double mouseY, final List<String> filePaths)
    {
        this.setLastMousePos(mouseX, mouseY);

        if (this.mouseHandlers.isEmpty() == false)
        {
            List<Path> paths = new ArrayList<>();

            for (String entry : filePaths)
            {
                try
                {
                    final String sanitizedFile = FileNameUtils.generateSafeFileName(FileNameUtils.generateSimpleUnicodeSafeFileName(entry));
                    Path path = Paths.get(sanitizedFile).normalize();

                    if (Files.exists(path) && Files.isReadable(path) &&
                        Files.isRegularFile(path))
                    {
                        paths.add(path);
                    }
                }
                catch (InvalidPathException e)
                {
                    MaLiLib.LOGGER.error("Exception; not a valid path '{}'; {}", entry, e.getMessage());
                }
            }

            for (IMouseInputHandler handler : this.mouseHandlers)
            {
                FileFilter filter = handler.dropFileFilter();
                List<Path> filteredPaths = new ArrayList<>();

                if (filter != null)
                {
                    for (Path path : paths)
                    {
                        if (filter.accept(path.toFile()))
                        {
                            filteredPaths.add(path);
                        }
                    }
                }
                else
                {
                    filteredPaths.addAll(paths);
                }

                if (handler.onMouseFilesDrop(mouseX, mouseY, filteredPaths))
                {
                    this.printInputCancellationDebugMessage(handler);
                    return true;
                }
            }
        }

        return false;
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
