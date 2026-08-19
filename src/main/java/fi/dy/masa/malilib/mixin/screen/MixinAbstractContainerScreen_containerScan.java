package fi.dy.masa.malilib.mixin.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.registry.Registry;

@Mixin(value = AbstractContainerScreen.class, priority = 900)
public abstract class MixinAbstractContainerScreen_containerScan<T extends AbstractContainerMenu>
{
    @Shadow public abstract T getMenu();

    @Inject(method = "onClose", at = @At("HEAD"))
    private void malilib_onCloseContainer(CallbackInfo ci)
    {
        Minecraft mc = Minecraft.getInstance();

        if (mc.hasSingleplayerServer() && mc.getSingleplayerServer() != null)
        {
            mc.getSingleplayerServer().execute(() -> Registry.ENTITY_DATA_REGISTRY.chestTracker().onContainerMenuClosed(this.getMenu()));
        }
        else
        {
            mc.execute(() -> Registry.ENTITY_DATA_REGISTRY.chestTracker().onContainerMenuClosed(this.getMenu()));
        }
    }
}
