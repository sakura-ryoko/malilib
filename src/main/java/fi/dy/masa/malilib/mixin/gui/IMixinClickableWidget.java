package fi.dy.masa.malilib.mixin.gui;

import net.minecraft.client.gui.tooltip.TooltipState;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClickableWidget.class)
public interface IMixinClickableWidget
{
	@Accessor("tooltip")
	TooltipState malilib_getTooltipHolder();
}
