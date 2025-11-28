package fi.dy.masa.malilib.mixin.hud;

import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DebugScreenEntryList.class)
public interface IMixinDebugHudProfile
{
	@Accessor("allStatuses")
	Map<Identifier, DebugScreenEntryStatus> malilib$getVisibilityMap();

	@Accessor("currentlyEnabled")
	List<Identifier> malilib$getVisibleEntries();
}
