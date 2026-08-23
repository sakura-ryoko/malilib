package fi.dy.masa.malilib.mixin.test;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient_test
{
	@Inject(method = "stop", at = @At("HEAD"))
	private void malilib_onStop(CallbackInfo ci)
	{
//		if (MaLiLibReference.DEBUG_MODE && MaLiLibReference.EXPERIMENTAL_MODE)
//		{
//			TestThreadDaemonHandler.INSTANCE.endAll();
//		}
	}
}
