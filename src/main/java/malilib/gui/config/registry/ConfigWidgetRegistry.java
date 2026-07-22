package malilib.gui.config.registry;

import java.util.HashMap;
import javax.annotation.Nullable;

import malilib.config.group.ExpandableConfigGroup;
import malilib.config.group.PopupConfigGroup;
import malilib.config.option.BooleanAndDoubleConfig;
import malilib.config.option.BooleanAndFileConfig;
import malilib.config.option.BooleanAndIntConfig;
import malilib.config.option.BooleanConfig;
import malilib.config.option.ColorConfig;
import malilib.config.option.ConfigInfo;
import malilib.config.option.DirectoryConfig;
import malilib.config.option.DoubleConfig;
import malilib.config.option.DualColorConfig;
import malilib.config.option.FileConfig;
import malilib.config.option.GenericButtonConfig;
import malilib.config.option.HotkeyConfig;
import malilib.config.option.HotkeyedBooleanConfig;
import malilib.config.option.IntegerConfig;
import malilib.config.option.OptionConfig;
import malilib.config.option.StringConfig;
import malilib.config.option.Vec2dConfig;
import malilib.config.option.Vec2iConfig;
import malilib.config.option.list.BlackWhiteListConfig;
import malilib.config.option.list.BlockListConfig;
import malilib.config.option.list.EquipmentSlotListConfig;
import malilib.config.option.list.IdentifierListConfig;
import malilib.config.option.list.ItemListConfig;
import malilib.config.option.list.StatusEffectListConfig;
import malilib.config.option.list.StringListConfig;
import malilib.gui.config.ConfigOptionWidgetFactory;
import malilib.gui.config.ConfigSearchInfo;
import malilib.gui.config.MissingConfigTypeFactory;
import malilib.gui.widget.list.entry.config.BlackWhiteListConfigWidget;
import malilib.gui.widget.list.entry.config.BooleanAndDoubleConfigWidget;
import malilib.gui.widget.list.entry.config.BooleanAndFileConfigWidget;
import malilib.gui.widget.list.entry.config.BooleanAndIntConfigWidget;
import malilib.gui.widget.list.entry.config.BooleanConfigWidget;
import malilib.gui.widget.list.entry.config.ColorConfigWidget;
import malilib.gui.widget.list.entry.config.CustomHotkeyEntryWidget;
import malilib.gui.widget.list.entry.config.DirectoryConfigWidget;
import malilib.gui.widget.list.entry.config.DoubleConfigWidget;
import malilib.gui.widget.list.entry.config.DualColorConfigWidget;
import malilib.gui.widget.list.entry.config.ExpandableConfigGroupWidget;
import malilib.gui.widget.list.entry.config.FileConfigWidget;
import malilib.gui.widget.list.entry.config.GenericButtonConfigWidget;
import malilib.gui.widget.list.entry.config.HotkeyConfigWidget;
import malilib.gui.widget.list.entry.config.HotkeyedBooleanConfigWidget;
import malilib.gui.widget.list.entry.config.IntegerConfigWidget;
import malilib.gui.widget.list.entry.config.OptionListConfigWidget;
import malilib.gui.widget.list.entry.config.PopupConfigGroupWidget;
import malilib.gui.widget.list.entry.config.StringConfigWidget;
import malilib.gui.widget.list.entry.config.Vec2dConfigWidget;
import malilib.gui.widget.list.entry.config.Vec2iConfigWidget;
import malilib.gui.widget.list.entry.config.list.BlockListConfigWidget;
import malilib.gui.widget.list.entry.config.list.EquipmentSlotListConfigWidget;
import malilib.gui.widget.list.entry.config.list.IdentifierListConfigWidget;
import malilib.gui.widget.list.entry.config.list.ItemListConfigWidget;
import malilib.gui.widget.list.entry.config.list.StatusEffectListConfigWidget;
import malilib.gui.widget.list.entry.config.list.StringListConfigWidget;
import malilib.input.CustomHotkeyDefinition;

@SuppressWarnings("unchecked")
public class ConfigWidgetRegistry
{
    protected final HashMap<Class<? extends ConfigInfo>, ConfigOptionWidgetFactory<?>> configWidgetFactories = new HashMap<>();
    protected final HashMap<Class<? extends ConfigInfo>, ConfigSearchInfo<?>> configSearchInfoMap = new HashMap<>();
    protected final ConfigOptionWidgetFactory<?> missingTypeFactory = new MissingConfigTypeFactory();

    public ConfigWidgetRegistry()
    {
        this.registerDefaultWidgetFactories();
        this.registerDefaultSearchInfos();
    }

    /**
     * Registers a config screen widget factory for the given config type
     */
    public <C extends ConfigInfo> void registerConfigWidgetFactory(Class<C> type, ConfigOptionWidgetFactory<C> factory)
    {
        this.configWidgetFactories.put(type, factory);
    }

    /**
     * Registers a config search info provider.
     * These are only needed if your custom config type has boolean/toggle
     * option(s) or hotkey(s) and you want to have your custom config searchable/filterable
     * using the dropdown widget in the search bar, using the toggle and hotkey related options there.
     */
    public <C extends ConfigInfo> void registerConfigSearchInfo(Class<C> type, ConfigSearchInfo<C> info)
    {
        this.configSearchInfoMap.put(type, info);
    }

    @SuppressWarnings("unchecked")
    public <C extends ConfigInfo> ConfigOptionWidgetFactory<C> getWidgetFactory(C config)
    {
        return (ConfigOptionWidgetFactory<C>) this.configWidgetFactories.getOrDefault(config.getClass(), this.missingTypeFactory);
    }

    public ConfigOptionWidgetFactory<?> getMissingTypeFactory()
    {
        return this.missingTypeFactory;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <C extends ConfigInfo> ConfigSearchInfo<C> getSearchInfo(C config)
    {
        return (ConfigSearchInfo<C>) this.configSearchInfoMap.get(config.getClass());
    }

    protected void registerDefaultWidgetFactories()
    {
        this.registerConfigWidgetFactory(BlackWhiteListConfig.class,    BlackWhiteListConfigWidget::new);
        this.registerConfigWidgetFactory(OptionConfig.class, OptionListConfigWidget::new);

        this.registerConfigWidgetFactory(BlockListConfig.class,         BlockListConfigWidget::new);
        this.registerConfigWidgetFactory(BooleanConfig.class,           BooleanConfigWidget::new);
        this.registerConfigWidgetFactory(BooleanAndDoubleConfig.class,  BooleanAndDoubleConfigWidget::new);
        this.registerConfigWidgetFactory(BooleanAndFileConfig.class,    BooleanAndFileConfigWidget::new);
        this.registerConfigWidgetFactory(BooleanAndIntConfig.class,     BooleanAndIntConfigWidget::new);
        this.registerConfigWidgetFactory(ColorConfig.class,             ColorConfigWidget::new);
        this.registerConfigWidgetFactory(CustomHotkeyDefinition.class,  CustomHotkeyEntryWidget::new);
        this.registerConfigWidgetFactory(DirectoryConfig.class,         DirectoryConfigWidget::new);
        this.registerConfigWidgetFactory(DoubleConfig.class,            DoubleConfigWidget::new);
        this.registerConfigWidgetFactory(DualColorConfig.class,         DualColorConfigWidget::new);
        this.registerConfigWidgetFactory(EquipmentSlotListConfig.class, EquipmentSlotListConfigWidget::new);
        this.registerConfigWidgetFactory(ExpandableConfigGroup.class,   ExpandableConfigGroupWidget::new);
        this.registerConfigWidgetFactory(FileConfig.class,              FileConfigWidget::new);
        this.registerConfigWidgetFactory(GenericButtonConfig.class,     GenericButtonConfigWidget::new);
        this.registerConfigWidgetFactory(HotkeyConfig.class,            HotkeyConfigWidget::new);
        this.registerConfigWidgetFactory(HotkeyedBooleanConfig.class,   HotkeyedBooleanConfigWidget::new);
        this.registerConfigWidgetFactory(IdentifierListConfig.class,    IdentifierListConfigWidget::new);
        this.registerConfigWidgetFactory(IntegerConfig.class,           IntegerConfigWidget::new);
        this.registerConfigWidgetFactory(ItemListConfig.class,          ItemListConfigWidget::new);
        this.registerConfigWidgetFactory(PopupConfigGroup.class,        PopupConfigGroupWidget::new);
        this.registerConfigWidgetFactory(StatusEffectListConfig.class,  StatusEffectListConfigWidget::new);
        this.registerConfigWidgetFactory(StringConfig.class,            StringConfigWidget::new);
        this.registerConfigWidgetFactory(StringListConfig.class,        StringListConfigWidget::new);
        this.registerConfigWidgetFactory(Vec2dConfig.class,             Vec2dConfigWidget::new);
        this.registerConfigWidgetFactory(Vec2iConfig.class,             Vec2iConfigWidget::new);
    }

    protected void registerDefaultSearchInfos()
    {
        this.registerConfigSearchInfo(BooleanConfig.class,          new ConfigSearchInfo<BooleanConfig>(true, false).setBooleanStorageGetter((c) -> c));
        this.registerConfigSearchInfo(BooleanAndDoubleConfig.class, new ConfigSearchInfo<BooleanAndDoubleConfig>(true, false).setBooleanStorageGetter((c) -> c));
        this.registerConfigSearchInfo(BooleanAndFileConfig.class,   new ConfigSearchInfo<BooleanAndFileConfig>(true, false).setBooleanStorageGetter((c) -> c));
        this.registerConfigSearchInfo(BooleanAndIntConfig.class,    new ConfigSearchInfo<BooleanAndIntConfig>(true, false).setBooleanStorageGetter((c) -> c));
        this.registerConfigSearchInfo(HotkeyConfig.class,           new ConfigSearchInfo<HotkeyConfig>(false, true).setKeyBindGetter(HotkeyConfig::getKeyBind));
        this.registerConfigSearchInfo(HotkeyedBooleanConfig.class,  new ConfigSearchInfo<HotkeyedBooleanConfig>(true, true).setBooleanStorageGetter((c) -> c).setKeyBindGetter(HotkeyedBooleanConfig::getKeyBind));

        this.registerConfigSearchInfo(CustomHotkeyDefinition.class, new ConfigSearchInfo<CustomHotkeyDefinition>(false, true).setKeyBindGetter(CustomHotkeyDefinition::getKeyBind));
    }
}
