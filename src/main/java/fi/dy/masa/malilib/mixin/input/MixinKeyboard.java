package fi.dy.masa.malilib.mixin.input;

import org.objectweb.asm.Opcodes;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.util.IF3KeyStateSetter;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;

@Mixin(value = KeyboardHandler.class, priority = 990)
public abstract class MixinKeyboard implements IF3KeyStateSetter
{
    @Shadow private boolean usedDebugKeyAsModifier;
    @Shadow @Final private Minecraft minecraft;

    @Override
    public void malilib$setF3KeyState(boolean value)
    {
        this.usedDebugKeyAsModifier = value;
    }

    @Inject(method = "keyPress", cancellable = true,
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/KeyboardHandler;debugCrashKeyTime:J",
                     ordinal = 0,
                     opcode = Opcodes.GETFIELD))
    private void malilib_onKeyboardInput(long window, int key, KeyEvent input, CallbackInfo ci)
    {
        if (((InputEventHandler) InputEventHandler.getInputManager()).onKeyInput(input, key, this.minecraft))
        {
            ci.cancel();
        }
    }
}
