package fi.dy.masa.malilib.mixin.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.resources.model.ModelManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.render.RenderUtils;

@Mixin(value = GameRenderer.class, priority = 800)
public class MixinGameRenderer
{
    @Shadow @Final private GuiRenderer guiRenderer;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void malilib_injectSpecialGuiRenderer(Minecraft minecraft,
                                                  FirstPersonHandsAndItemsRenderer firstPersonHandsAndItemsRenderer,
                                                  ModelManager modelManager,
                                                  ItemModelResolver itemModelResolver, CallbackInfo ci)
    {
        RenderUtils.registerSpecialGuiRenderers(this.guiRenderer, minecraft);
    }
}
