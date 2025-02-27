package fi.dy.masa.malilib.mixin.render;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface IMixinHandledScreen
{
    @Accessor("focusedSlot")
    Slot malilib_getFocusedSlot();
}
