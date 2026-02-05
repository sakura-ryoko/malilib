package fi.dy.masa.malilib.gui.widgets;

import java.util.*;

import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase.ConfigOptionWrapper;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.util.AlphaNumComparator;

public class WidgetListConfigOptions extends WidgetListConfigOptionsBase<ConfigOptionWrapper, WidgetConfigOption>
{
    protected final GuiConfigsBase parent;
    protected final WidgetSearchBarConfigs widgetSearchConfigs;

    public WidgetListConfigOptions(int x, int y, int width, int height, int configWidth, float zLevel, boolean useKeybindSearch, GuiConfigsBase parent)
    {
        super(x, y, width, height, configWidth);

        this.parent = parent;

        if (useKeybindSearch)
        {
            this.widgetSearchConfigs = new WidgetSearchBarConfigs(x + 2, y + 4, width - 14, 20, 0, MaLiLibIcons.SEARCH, LeftRight.LEFT);
            this.widgetSearchBar = this.widgetSearchConfigs;
            this.browserEntriesOffsetY = 23;
        }
        else
        {
            this.widgetSearchConfigs = null;
            this.widgetSearchBar = new WidgetSearchBar(x + 2, y + 4, width - 14, 14, 0, MaLiLibIcons.SEARCH, LeftRight.LEFT);
            this.browserEntriesOffsetY = 17;
        }
    }

    @Override
    protected Collection<ConfigOptionWrapper> getAllEntries()
    {
        return this.parent.getConfigs();
    }

    @Override
    protected void reCreateListEntryWidgets()
    {
        this.maxLabelWidth = this.getMaxNameLengthWrapped(this.listContents);
        super.reCreateListEntryWidgets();
    }

    @Override
    protected List<String> getEntryStringsForFilter(ConfigOptionWrapper entry)
    {
        IConfigBase config = entry.getConfig();

        if (config != null)
        {
            ArrayList<String> list = new ArrayList<>();
            String name = config.getName();
            String translated = config.getConfigGuiDisplayName();

            list.add(name.toLowerCase());

            if (name.equals(translated) == false)
            {
                list.add(translated.toLowerCase());
            }

            if (config instanceof IConfigResettable && ((IConfigResettable) config).isModified())
            {
                list.add("modified");
            }

            return list;
        }

        return Collections.emptyList();
    }

    @Override
    protected void addFilteredContents(Collection<ConfigOptionWrapper> entries)
    {
        if (this.widgetSearchConfigs != null)
        {
            IKeybind filterKeys = this.widgetSearchConfigs.getKeybind();
            String filterText = this.widgetSearchConfigs.getFilter();

            // Updated Searchbar with Hotkey filter to be less jank
            for (ConfigOptionWrapper entry : entries)
            {
                if (entry.getConfig() == null)
                {
                    continue;
                }

                boolean isHotkey = entry.getConfig().getType() == ConfigType.HOTKEY ||
                                   entry.getConfig() instanceof IHotkeyTogglable;
                IKeybind keybind = isHotkey ? ((IHotkey) entry.getConfig()).getKeybind() : null;
                boolean hasFilterKeys = filterKeys != null && !filterKeys.getKeys().isEmpty();
                boolean overlaps = keybind != null && hasFilterKeys && keybind.overlaps(filterKeys);
//                boolean filterEmpty = filterText != null && filterText.isEmpty();         // entryMatchesFilter() also checks for empty
                boolean entryMatches = this.entryMatchesFilter(entry, filterText);

//                MaLiLib.LOGGER.error("addFilteredContents() - [{}/{}]: isHotkey: [{}], overlaps: [{}], keybind: [{}] // '{}' = filterEmpty: [{}], entryMatches: [{}]",
//                                     entry.getConfig().getType(), entry.getConfig().getName(),
//                                     isHotkey, overlaps, keybind != null ? keybind.getStringValue() : "<>",
//                                     filterText, filterEmpty, entryMatches
//                );

                if ((isHotkey && (overlaps || !hasFilterKeys) && entryMatches) ||
                    (!hasFilterKeys && entryMatches))
                {
                    this.listContents.add(entry);
                }
            }
        }
        else
        {
            super.addFilteredContents(entries);
        }
    }

    @Override
    protected Comparator<ConfigOptionWrapper> getComparator()
    {
        return new ConfigComparator();
    }

    @Override
    protected WidgetConfigOption createListEntryWidget(int x, int y, int listIndex, boolean isOdd, ConfigOptionWrapper wrapper)
    {
        return new WidgetConfigOption(x, y, this.browserEntryWidth, this.browserEntryHeight,
                this.maxLabelWidth, this.configWidth, wrapper, listIndex, this.parent, this);
    }

    public int getMaxNameLengthWrapped(List<ConfigOptionWrapper> wrappers)
    {
        int width = 0;

        for (ConfigOptionWrapper wrapper : wrappers)
        {
            if (wrapper.getType() == ConfigOptionWrapper.Type.CONFIG && wrapper.getConfig() != null)
            {
                width = Math.max(width, this.getStringWidth(wrapper.getConfig().getConfigGuiDisplayName()));
            }
        }

        return width;
    }

    protected static class ConfigComparator extends AlphaNumComparator implements Comparator<ConfigOptionWrapper>
    {
        @Override
        public int compare(ConfigOptionWrapper config1, ConfigOptionWrapper config2)
        {
			if (config1.getConfig() == null || config2.getConfig() == null) return 0;
            return this.compare(config1.getConfig().getName(), config2.getConfig().getName());
        }
    }
}
