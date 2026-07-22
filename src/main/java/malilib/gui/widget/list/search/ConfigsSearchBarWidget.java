package malilib.gui.widget.list.search;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import malilib.MaLiLibConfigs;
import malilib.config.option.ConfigInfo;
import malilib.config.value.BaseOptionConfigValue;
import malilib.config.value.OptionConfigValue;
import malilib.gui.BaseScreen;
import malilib.gui.ConfirmActionScreen;
import malilib.gui.config.ConfigSearchInfo;
import malilib.gui.icon.Icon;
import malilib.gui.widget.DropDownListWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.button.KeyBindConfigButton;
import malilib.input.Context;
import malilib.input.KeyBind;
import malilib.input.KeyBindImpl;
import malilib.input.KeyBindSettings;
import malilib.listener.EventListener;
import malilib.registry.Registry;
import malilib.util.StringUtils;

public class ConfigsSearchBarWidget extends SearchBarWidget
{
    protected static final KeyBindSettings SETTINGS = KeyBindSettings.builder().context(Context.ANY).both().extra().noCancel().build();

    protected final KeyBindImpl searchKey;
    protected final KeyBindConfigButton hotkeySearchButton;
    protected final GenericButton resetConfigsButton;
    protected final DropDownListWidget<Scope> sourceSelectionDropdown;
    protected final DropDownListWidget<TypeFilter> typeFilterDropdown;
    protected final EventListener configResetter;
    protected int openedHeight;

    public ConfigsSearchBarWidget(int width,
                                  int openedHeight,
                                  EventListener searchInputChangeListener,
                                  EventListener openCloseListener,
                                  EventListener configScopeChangeListener,
                                  Icon iconSearch,
                                  EventListener configResetter)
    {
        super(width - 160, 14, searchInputChangeListener, openCloseListener, iconSearch);

        this.canReceiveMouseClicks = true;
        this.openedHeight = openedHeight;
        this.configResetter = configResetter;

        this.searchKey = KeyBindImpl.fromStorageString("", SETTINGS);

        this.hotkeySearchButton = new KeyBindConfigButton(160, 16, this.searchKey);
        this.hotkeySearchButton.setUpdateKeyBindImmediately();
        this.hotkeySearchButton.translateAndAddHoverString("malilib.hover.button.config.hotkey_search_button");
        this.hotkeySearchButton.setHoverInfoRequiresShift(false);
        this.hotkeySearchButton.setValueChangeListener(searchInputChangeListener);

        this.resetConfigsButton = GenericButton.create(160, 16, "malilib.button.config.reset_all_filtered");
        this.resetConfigsButton.translateAndAddHoverString("malilib.hover.button.config.reset_all_filtered");
        this.resetConfigsButton.setActionListener(this::openResetConfigsConfirmationScreen);

        this.sourceSelectionDropdown = new DropDownListWidget<>(14, 4, Scope.VALUES, Scope::getDisplayName);
        this.sourceSelectionDropdown.setSelectedEntry(MaLiLibConfigs.Generic.CONFIG_SEARCH_DEFAULT_SCOPE.getValue());
        this.sourceSelectionDropdown.setSelectionListener((s) -> configScopeChangeListener.onEvent());
        this.sourceSelectionDropdown.translateAndAddHoverString("malilib.hover.config.search_default_scope");

        this.typeFilterDropdown = new DropDownListWidget<>(14, 12, TypeFilter.VALUES, TypeFilter::getDisplayName);
        this.typeFilterDropdown.setSelectedEntry(TypeFilter.ALL);
        this.typeFilterDropdown.setSelectionListener((s) -> configScopeChangeListener.onEvent());
    }

    public KeyBind getKeybind()
    {
        return this.searchKey;
    }

    public Scope getCurrentScope()
    {
        return this.isSearchOpen() ? this.sourceSelectionDropdown.getSelectedEntry() : Scope.CURRENT_CATEGORY;
    }

    @Override
    public void reAddSubWidgets()
    {
        // Don't call super, so that we can add the dropdown widget before the text field,
        // so that the dropdown can handle the mouse click first even when it overlaps the text field.
        // Although this should now be fixed by the new input dispatch to the top hovered widget first.

        this.clearWidgets();

        this.addWidget(this.searchToggleButton);

        if (this.isSearchOpen())
        {
            this.addWidget(this.sourceSelectionDropdown);
            this.addWidget(this.typeFilterDropdown);
            this.addWidget(this.resetConfigsButton);
            this.addWidget(this.hotkeySearchButton);
            this.addWidget(this.textField);
        }
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX();
        int y = this.getY();
        int width = this.getWidth();

        this.sourceSelectionDropdown.setPosition(x + 16, y);
        this.typeFilterDropdown.setPosition(this.sourceSelectionDropdown.getRight() + 4, y);

        int btnX = x + width - this.hotkeySearchButton.getWidth() - 1;
        this.resetConfigsButton.setPosition(btnX, y);

        if (this.searchToggleButton != null)
        {
            this.searchToggleButton.setY(y + 1);
        }

        y = this.sourceSelectionDropdown.getBottom() + 2;

        this.hotkeySearchButton.setPosition(btnX, y);
        this.textField.setPosition(this.sourceSelectionDropdown.getX(), y + 1);
        this.textField.setWidth(width - this.hotkeySearchButton.getWidth() - 20);
    }

    @Override
    public int getHeight()
    {
        return this.isSearchOpen() ? this.openedHeight : super.getHeight();
    }

    @Override
    public boolean hasFilter()
    {
        return super.hasFilter() || (this.isSearchOpen() &&
                                     (this.searchKey.hasKeys() ||
                                      this.typeFilterDropdown.getSelectedEntry() != TypeFilter.ALL));
    }

    public boolean passesFilter(ConfigInfo config)
    {
        if (this.isSearchOpen())
        {
            @Nullable ConfigSearchInfo<ConfigInfo> info = Registry.CONFIG_WIDGET.getSearchInfo(config);

            if (this.searchKey.hasKeys())
            {
                if (info == null || info.hasHotkey == false)
                {
                    return false;
                }

                KeyBind kb = info.getKeyBind(config);

                if (kb == null || kb.overlaps(this.searchKey) == false)
                {
                    return false;
                }
            }

            TypeFilter type = this.typeFilterDropdown.getSelectedEntry();
            return type == null || type.matches(info, config);
        }

        return true;
    }

    /*
    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isSearchOpen())
        {
            if (this.hotkeySearchButton.isMouseOver(mouseX, mouseY))
            {
                boolean selectedPre = this.hotkeySearchButton.isFocused();
                this.hotkeySearchButton.tryMouseClick(mouseX, mouseY, mouseButton);

                if (selectedPre == false)
                {
                    this.hotkeySearchButton.onSelected();
                }

                return true;
            }
            else if (this.hotkeySearchButton.isFocused())
            {
                this.hotkeySearchButton.onClearSelection();
                return true;
            }
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (this.isSearchOpen() && this.hotkeySearchButton.isFocused())
        {
            this.hotkeySearchButton.onKeyTyped(keyCode, scanCode, modifiers);

            if (keyCode == Keys.KEY_ESCAPE)
            {
                this.hotkeySearchButton.onClearSelection();
            }

            return true;
        }

        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }
    */

    protected void openResetConfigsConfirmationScreen()
    {
        String title = "malilib.title.screen.confirm_config_reset";
        String message = "malilib.label.confirm.selected_configs_reset";
        ConfirmActionScreen confirmScreen = new ConfirmActionScreen(240, title, this.configResetter, message);
        BaseScreen.openPopupScreenWithCurrentScreenAsParent(confirmScreen);
    }

    public static class Scope extends BaseOptionConfigValue
    {
        public static final Scope CURRENT_CATEGORY = new Scope("malilib.label.config.search.scope.current_category");
        public static final Scope ALL_CATEGORIES   = new Scope("malilib.label.config.search.scope.all_categories");
        public static final Scope ALL_MODS         = new Scope("malilib.label.config.search.scope.all_mods");

        public static final ImmutableList<Scope> VALUES = ImmutableList.of(CURRENT_CATEGORY, ALL_CATEGORIES, ALL_MODS);

        private Scope(String translationKey)
        {
            super(translationKey, translationKey);
        }
    }

    public static class TypeFilter implements OptionConfigValue
    {
        public static final List<TypeFilter> VALUES = new ArrayList<>();

        public static final TypeFilter ALL             = register("malilib.label.config.search.type_filter.all",             (i, c) -> true);
        public static final TypeFilter MODIFIED        = register("malilib.label.config.search.type_filter.modified",        (i, c) -> c.isModified());
        public static final TypeFilter MODIFIED_TOGGLE = register("malilib.label.config.search.type_filter.modified_toggle", (i, c) -> i != null && i.hasModifiedToggle(c));
        public static final TypeFilter ENABLED_TOGGLE  = register("malilib.label.config.search.type_filter.enabled_toggle",  (i, c) -> i != null && i.hasEnabledToggle(c));
        public static final TypeFilter DISABLED_TOGGLE = register("malilib.label.config.search.type_filter.disabled_toggle", (i, c) -> i != null && i.hasDisabledToggle(c));
        public static final TypeFilter ANY_HOTKEY      = register("malilib.label.config.search.type_filter.any_hotkey",      (i, c) -> i != null && i.hasHotkey);
        public static final TypeFilter MODIFIED_HOTKEY = register("malilib.label.config.search.type_filter.modified_hotkey", (i, c) -> i != null && i.hasModifiedHotkey(c));
        public static final TypeFilter BOUND_HOTKEY    = register("malilib.label.config.search.type_filter.bound_hotkey",    (i, c) -> i != null && i.hasBoundHotkey(c));
        public static final TypeFilter UNBOUND_HOTKEY  = register("malilib.label.config.search.type_filter.unbound_hotkey",  (i, c) -> i != null && i.hasUnboundHotkey(c));

        private final String translationKey;
        private final TypeFilterTest tester;

        public TypeFilter(String translationKey, TypeFilterTest tester)
        {
            this.translationKey = translationKey;
            this.tester = tester;
        }

        @Override
        public String getName()
        {
            return this.translationKey;
        }

        @Override
        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }

        public boolean matches(@Nullable ConfigSearchInfo<ConfigInfo> info, ConfigInfo config)
        {
            return this.tester.test(info, config);
        }

        public static TypeFilter register(String translationKey, TypeFilterTest tester)
        {
            TypeFilter filter = new TypeFilter(translationKey, tester);
            VALUES.add(filter);
            return filter;
        }
    }

    public interface TypeFilterTest
    {
        boolean test(ConfigSearchInfo<ConfigInfo> info, ConfigInfo config);
    }
}
