package fi.dy.masa.malilib.mixin.screen;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface IMixinHandledScreen
{
    @Accessor("hoveredSlot")
    Slot malilib_getFocusedSlot();
}
