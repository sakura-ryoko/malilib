package fi.dy.masa.malilib.mixin.gui;

import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Hud.class)
public interface IMixinHud
{
    @Accessor("overlayMessageTime")
    void malilib_setOverlayMessageTime(int ticks);
}
