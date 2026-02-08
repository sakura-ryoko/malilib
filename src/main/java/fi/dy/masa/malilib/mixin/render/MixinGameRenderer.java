package fi.dy.masa.malilib.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.render.RenderUtils;

@Mixin(value = GameRenderer.class, priority = 990)
public class MixinGameRenderer
{
    @Shadow @Final private GuiRenderer guiRenderer;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void malilib_injectSpecialGuiRenderer(Minecraft client, ItemInHandRenderer firstPersonHeldItemRenderer, RenderBuffers buffers, BlockRenderDispatcher blockRenderManager, CallbackInfo ci,
												  @Local MultiBufferSource.BufferSource immediate)
    {
        RenderUtils.registerSpecialGuiRenderers(this.guiRenderer, immediate, client);
    }
}
