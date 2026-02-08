package fi.dy.masa.malilib.mixin.screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.command.ClientCommandHandler;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Mixin(value = ChatScreen.class, priority = 999)
public abstract class MixinChatScreen extends Screen
{
    private MixinChatScreen(Component title)
    {
        super(title);
    }

    @Inject(method = "handleChatInput", at = @At("HEAD"), cancellable = true)
    private void malilib_onSendChatMessage(String msg, boolean addToHistory, CallbackInfo ci)
    {
        if (!msg.isEmpty() && ClientCommandHandler.INSTANCE.onSendClientMessage(msg, this.minecraft))
        {
            ci.cancel();
        }
    }
}
