package fi.dy.masa.malilib.mixin.screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.command.ClientCommandHandler;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen extends Screen
{
    private MixinChatScreen(Text title)
    {
        super(title);
    }

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void malilib_onSendChatMessage(String msg, boolean addToHistory, CallbackInfo ci)
    {
        if (!msg.isEmpty() && ClientCommandHandler.INSTANCE.onSendClientMessage(msg, this.client))
        {
            ci.cancel();
        }
    }
}
