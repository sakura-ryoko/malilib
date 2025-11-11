package fi.dy.masa.malilib.mixin.input;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.util.IF3KeyStateSetter;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;

@Mixin(Keyboard.class)
public abstract class MixinKeyboard implements IF3KeyStateSetter
{
    @Shadow private boolean switchF3State;
    @Shadow @Final private MinecraftClient client;

    @Override
    public void malilib$setF3KeyState(boolean value)
    {
        this.switchF3State = value;
    }

    @Inject(method = "onKey", cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Keyboard;debugCrashStartTime:J", ordinal = 0))
    private void malilib_onKeyboardInput(long window, int key, KeyInput input, CallbackInfo ci)
    {
        if (((InputEventHandler) InputEventHandler.getInputManager()).onKeyInput(input, key, this.client))
        {
            ci.cancel();
        }
    }
}
