package fi.dy.masa.malilib.util.game;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudEntryVisibility;
import net.minecraft.util.Identifier;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.mixin.hud.IMixinDebugHudProfile;

/**
 * You need to add the AW for the "ENTRIES" in the downstream mod.
 * There really is no other more "elegant" method for this to be able
 * to reliably register / unregister them.
 */
public class DebugHudUtils
{
	public static void register(Identifier id, @Nonnull DebugHudEntry entry)
	{
		if (Objects.equals(id.getNamespace(), "minecraft")) return;
		if (!DebugHudEntries.getEntries().containsKey(id))
		{
			MinecraftClient mc = MinecraftClient.getInstance();

			DebugHudEntries.ENTRIES.put(id, entry);
			MaLiLib.debugLog("DebugHudUtils#register(): Registered [{}]", id.toString());

			if (mc.debugHudEntryList == null) return;

			if (!((IMixinDebugHudProfile) mc.debugHudEntryList).malilib$getVisibilityMap().containsKey(id))
			{
				((IMixinDebugHudProfile) mc.debugHudEntryList).malilib$getVisibilityMap().put(id, DebugHudEntryVisibility.NEVER);
				mc.debugHudEntryList.saveProfileFile();
			}
		}
	}

	public static void unregister(Identifier id)
	{
		if (Objects.equals(id.getNamespace(), "minecraft")) return;
		MinecraftClient mc = MinecraftClient.getInstance();

		DebugHudEntries.ENTRIES.remove(id);

		if (mc.debugHudEntryList != null)
		{
			((IMixinDebugHudProfile) mc.debugHudEntryList).malilib$getVisibilityMap().remove(id);
			mc.debugHudEntryList.getVisibleEntries().remove(id);
			mc.debugHudEntryList.saveProfileFile();
		}
	}

	public static @Nullable DebugHudEntryVisibility getVisibility(Identifier id)
	{
		MinecraftClient mc = MinecraftClient.getInstance();

		if (DebugHudEntries.getEntries().containsKey(id) &&
			mc.debugHudEntryList != null &&
			((IMixinDebugHudProfile) mc.debugHudEntryList).malilib$getVisibilityMap().containsKey(id))
		{
			return ((IMixinDebugHudProfile) mc.debugHudEntryList).malilib$getVisibilityMap().get(id);
		}

		return null;
	}

	public static void setVisibility(Identifier id, DebugHudEntryVisibility visibility)
	{
		MinecraftClient mc = MinecraftClient.getInstance();

		if (DebugHudEntries.getEntries().containsKey(id) &&
			mc.debugHudEntryList != null)
		{
			((IMixinDebugHudProfile) mc.debugHudEntryList).malilib$getVisibilityMap().put(id, visibility);
		}
	}
}
