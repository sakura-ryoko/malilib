package fi.dy.masa.malilib.mixin.hud;

import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.hud.debug.DebugHudEntryVisibility;
import net.minecraft.client.gui.hud.debug.DebugHudProfile;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DebugHudProfile.class)
public interface IMixinDebugHudProfile
{
	@Accessor("visibilityMap")
	Map<Identifier, DebugHudEntryVisibility> malilib$getVisibilityMap();

	@Accessor("visibleEntries")
	List<Identifier> malilib$getVisibleEntries();
}
