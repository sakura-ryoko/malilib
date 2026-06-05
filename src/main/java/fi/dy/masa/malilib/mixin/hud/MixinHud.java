package fi.dy.masa.malilib.mixin.hud;

import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import fi.dy.masa.malilib.util.game.IGameHud;

@Mixin(value = Hud.class, priority = 900)
public abstract class MixinHud implements IGameHud
{
	@Shadow private int overlayMessageTime;

	@Override
	public void malilib$setOverlayRemaining(int ticks)
	{
		this.overlayMessageTime = ticks;
	}
}
