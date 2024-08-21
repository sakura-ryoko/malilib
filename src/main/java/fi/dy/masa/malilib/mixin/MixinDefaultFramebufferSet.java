package fi.dy.masa.malilib.mixin;

import java.util.Set;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.malilib.interfaces.IFramebufferSetRemap;
import fi.dy.masa.malilib.event.FramebufferHandler;

@Mixin(value = DefaultFramebufferSet.class)
public class MixinDefaultFramebufferSet implements IFramebufferSetRemap
{
    @Mutable
    @Shadow @Final public static Set<Identifier> STAGES;

    @Inject(method = "set", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/Identifier;equals(Ljava/lang/Object;)Z",
            shift = At.Shift.BEFORE,
            ordinal = 6))
    private void malilib_handleFramebufferSet(Identifier id, Handle<Framebuffer> framebuffer, CallbackInfo ci)
    {
        ((FramebufferHandler) FramebufferHandler.getInstance()).onFramebufferSet(id, framebuffer);
    }

    @Inject(method = "get", at = @At(value = "RETURN"), cancellable = true)
    private void malilib_handleFramebufferGet(Identifier id, CallbackInfoReturnable<Handle<Framebuffer>> cir)
    {
        Handle<Framebuffer> result = ((FramebufferHandler) FramebufferHandler.getInstance()).onFramebufferGet(id);

        if (result != null && cir.getReturnValue() == null)
        {
            cir.setReturnValue(result);
        }
    }

    @Inject(method = "clear", at = @At("TAIL"))
    private void malilib_onFramebufferClear(CallbackInfo ci)
    {
        ((FramebufferHandler) FramebufferHandler.getInstance()).onFramebufferClear();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void malilib_onFramebufferInit(CallbackInfo ci)
    {
        this.malilib_remapStages();
    }

    @Unique
    public void malilib_remapStages()
    {
        STAGES = ((FramebufferHandler) FramebufferHandler.getInstance()).onStagesRemap(STAGES);
    }
}
