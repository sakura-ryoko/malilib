package fi.dy.masa.malilib.mixin.screen;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.util.nbt.NbtInventory;

@Mixin(value = AbstractContainerScreen.class, priority = 900)
public abstract class MixinAbstractContainerScreen_containerScan<T extends AbstractContainerMenu>
{
    @Shadow public abstract T getMenu();

    @Inject(method = "onClose", at = @At("HEAD"))
    private void malilib_onCloseContainer(CallbackInfo ci)
    {
        // ((AbstractContainerScreen<?>) (Object) this).getMenu()
        Registry.ENTITY_DATA_REGISTRY.onContainerMenuClosed(this.getMenu());
    }
}
