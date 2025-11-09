package fi.dy.masa.malilib.util.game;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.resources.ResourceLocation;

import fi.dy.masa.malilib.MaLiLib;

public class DebugHudUtils
{
	public static void register(ResourceLocation id, @Nonnull DebugScreenEntry entry)
	{
		if (Objects.equals(id.getNamespace(), "minecraft")) return;
		if (!DebugScreenEntries.ENTRIES_BY_LOCATION.containsKey(id))
		{
			Minecraft mc = Minecraft.getInstance();

			DebugScreenEntries.ENTRIES_BY_LOCATION.put(id, entry);
			MaLiLib.debugLog("DebugHudUtils#register(): Registered [{}]", id.toString());

			if (mc.debugEntries == null) return;
			if (!mc.debugEntries.allStatuses.containsKey(id))
			{
				mc.debugEntries.allStatuses.put(id, DebugScreenEntryStatus.NEVER);
				mc.debugEntries.save();
			}
		}
	}

	public static void unregister(ResourceLocation id)
	{
		if (Objects.equals(id.getNamespace(), "minecraft")) return;
		Minecraft mc = Minecraft.getInstance();

		DebugScreenEntries.ENTRIES_BY_LOCATION.remove(id);

		if (mc.debugEntries != null)
		{
			mc.debugEntries.allStatuses.remove(id);
			mc.debugEntries.currentlyEnabled.remove(id);
			mc.debugEntries.save();
		}
	}

	public static @Nullable DebugScreenEntryStatus getVisibility(ResourceLocation id)
	{
		Minecraft mc = Minecraft.getInstance();

		if (DebugScreenEntries.ENTRIES_BY_LOCATION.containsKey(id) &&
			mc.debugEntries != null &&
			mc.debugEntries.allStatuses.containsKey(id))
		{
			return mc.debugEntries.allStatuses.get(id);
		}

		return null;
	}

	public static void setVisibility(ResourceLocation id, DebugScreenEntryStatus visibility)
	{
		Minecraft mc = Minecraft.getInstance();

		if (DebugScreenEntries.ENTRIES_BY_LOCATION.containsKey(id) &&
			mc.debugEntries != null)
		{
			mc.debugEntries.allStatuses.put(id, visibility);
		}
	}
}
