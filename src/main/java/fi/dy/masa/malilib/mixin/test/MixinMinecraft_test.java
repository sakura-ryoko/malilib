package fi.dy.masa.malilib.mixin.test;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.test.thread.TestThreadDaemonAsyncHandler;
import fi.dy.masa.malilib.test.thread.TestThreadDaemonDefaultHandler;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft_test
{
	@Inject(method = "stop", at = @At("HEAD"))
	private void malilib_onStop(CallbackInfo ci)
	{
		if (MaLiLibReference.DEBUG_MODE && MaLiLibReference.EXPERIMENTAL_MODE)
		{
			TestThreadDaemonDefaultHandler.INSTANCE.endAll();
            TestThreadDaemonAsyncHandler.INSTANCE.endAll();
		}
	}
}
