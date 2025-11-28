package fi.dy.masa.malilib.util.game;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.resources.Identifier;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.mixin.hud.IMixinDebugHudProfile;

/**
 * You need to add the AW for the "ENTRIES" in the downstream mod.
 * There really is no other more "elegant" method for this to be able
 * to reliably register / unregister them.
 */
public class DebugHudUtils
{
	public static void register(Identifier id, @Nonnull DebugScreenEntry entry)
	{
		if (Objects.equals(id.getNamespace(), "minecraft")) return;
		if (!DebugScreenEntries.allEntries().containsKey(id))
		{
			Minecraft mc = Minecraft.getInstance();

			DebugScreenEntries.ENTRIES_BY_ID.put(id, entry);
			MaLiLib.debugLog("DebugHudUtils#register(): Registered [{}]", id.toString());

			if (mc.debugEntries == null) return;

			if (!((IMixinDebugHudProfile) mc.debugEntries).malilib$getVisibilityMap().containsKey(id))
			{
				((IMixinDebugHudProfile) mc.debugEntries).malilib$getVisibilityMap().put(id, DebugScreenEntryStatus.NEVER);
				mc.debugEntries.save();
			}
		}
	}

	public static void unregister(Identifier id)
	{
		if (Objects.equals(id.getNamespace(), "minecraft")) return;
		Minecraft mc = Minecraft.getInstance();

		DebugScreenEntries.ENTRIES_BY_ID.remove(id);

		if (mc.debugEntries != null)
		{
			((IMixinDebugHudProfile) mc.debugEntries).malilib$getVisibilityMap().remove(id);
			mc.debugEntries.getCurrentlyEnabled().remove(id);
			mc.debugEntries.save();
		}
	}

	public static @Nullable DebugScreenEntryStatus getVisibility(Identifier id)
	{
		Minecraft mc = Minecraft.getInstance();

		if (DebugScreenEntries.allEntries().containsKey(id) &&
			mc.debugEntries != null &&
			((IMixinDebugHudProfile) mc.debugEntries).malilib$getVisibilityMap().containsKey(id))
		{
			return ((IMixinDebugHudProfile) mc.debugEntries).malilib$getVisibilityMap().get(id);
		}

		return null;
	}

	public static void setVisibility(Identifier id, DebugScreenEntryStatus visibility)
	{
		Minecraft mc = Minecraft.getInstance();

		if (DebugScreenEntries.allEntries().containsKey(id) &&
			mc.debugEntries != null)
		{
			((IMixinDebugHudProfile) mc.debugEntries).malilib$getVisibilityMap().put(id, visibility);
		}
	}
}
