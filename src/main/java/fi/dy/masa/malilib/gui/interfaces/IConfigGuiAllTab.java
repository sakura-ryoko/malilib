package fi.dy.masa.malilib.gui.interfaces;

import java.util.List;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.util.StringUtils;

/**
 * For marking the Config GUI as utilizing an 'All' tab, and then collecting the configs for it.
 * This is so that it forces the 'Search bar with Hotkey' filter to be active.
 */
public interface IConfigGuiAllTab
{
	boolean useAllTab();

	List<GuiConfigsBase.ConfigOptionWrapper> getAllConfigs();

	static String getTranslationKey() { return "malilib.gui.title.all"; }

	static String getDisplayName() { return StringUtils.translate(getTranslationKey()); }
}
