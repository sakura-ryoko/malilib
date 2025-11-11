package fi.dy.masa.malilib.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.render.RenderUtils;

@Mixin(GameRenderer.class)
public class MixinGameRenderer
{
    @Shadow @Final private GuiRenderer guiRenderer;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void malilib_injectSpecialGuiRenderer(MinecraftClient client, HeldItemRenderer firstPersonHeldItemRenderer, BufferBuilderStorage buffers, BlockRenderManager blockRenderManager, CallbackInfo ci,
												  @Local VertexConsumerProvider.Immediate immediate)
    {
        RenderUtils.registerSpecialGuiRenderers(this.guiRenderer, immediate, client);
    }
}
