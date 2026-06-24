package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.gui.ButtonPressDirtyListenerSimple;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.gui.GuiConfigsBase.ConfigOptionWrapper;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.interfaces.IConfigInfoProvider;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.KeyCodes;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public abstract class GuiConfigsBase extends GuiListBase<ConfigOptionWrapper, WidgetConfigOption, WidgetListConfigOptions> implements IKeybindConfigGui
{
    protected WidgetDropDownList<ModInfo> modSwitchWidget;
    protected final List<Runnable> hotkeyChangeListeners = new ArrayList<>();
    protected final ButtonPressDirtyListenerSimple dirtyListener = new ButtonPressDirtyListenerSimple();
    protected final String modId;
    protected ConfigButtonKeybind activeKeybindButton;
    protected int configWidth = 204;
    @Nullable protected IConfigInfoProvider hoverInfoProvider;
    @Nullable protected IDialogHandler dialogHandler;

    public GuiConfigsBase(int listX, int listY, String modId, @Nullable Screen parent, String titleKey, Object... args)
    {
        super(listX, listY);

        this.modId = modId;
        this.title = StringUtils.translate(titleKey, args);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buildConfigSwitcher();
    }

    protected void buildConfigSwitcher()
    {
        ModInfo thisMod = Registry.CONFIG_SCREEN.getModInfoFromConfigScreen(this.getClass());

        if (thisMod == null)
        {
            // Attempt to Register this screen.
            try
            {
                MaLiLib.debugLog("GuiConfigsBase#initGui(): Attempting to register [{}] ...", this.getModId());
                Registry.CONFIG_SCREEN.registerConfigScreenFactory(
                        new ModInfo(this.getModId(), StringUtils.splitCamelCase(this.getModId()), () -> this)
                );
            }
            catch (Exception ignored)
            {
                MaLiLib.LOGGER.warn("GuiConfigsBase#initGui(): Failed to automatically register [{}]", this.getModId());
                return;
            }
        }

        if (thisMod != null && MaLiLibConfigs.Generic.ENABLE_CONFIG_SWITCHER.getBooleanValue())
        {
            // Was: y = 13
            this.modSwitchWidget = new WidgetDropDownList<>(GuiUtils.getScaledWindowWidth() - 155, 6, 130, 18, 200, 10, Registry.CONFIG_SCREEN.getAllModsWithConfigScreens())
            {
                {
                    selectedEntry = thisMod;
                }

                @Override
                protected void setSelectedEntry(int index)
                {
                    super.setSelectedEntry(index);

                    if (selectedEntry != null && selectedEntry.getConfigScreenSupplier() != null)
                    {
                        client.setScreen(selectedEntry.getConfigScreenSupplier().get());
                    }
                }

                @Override
                protected String getDisplayString(ModInfo entry)
                {
                    return entry.getModName();
                }
            };

            addWidget(this.modSwitchWidget);
        }
    }

    @Override
    protected int getBrowserWidth()
    {
        return this.width - 20;
    }

    @Override
    protected int getBrowserHeight()
    {
        return this.height - 80;
    }

    protected boolean useKeybindSearch()
    {
        return false;
    }

    protected int getConfigWidth()
    {
        return this.configWidth;
    }

    public GuiConfigsBase setConfigWidth(int configWidth)
    {
        this.configWidth = configWidth;
        return this;
    }

    public GuiConfigsBase setHoverInfoProvider(IConfigInfoProvider provider)
    {
        this.hoverInfoProvider = provider;
        return this;
    }

    @Nullable
    @Override
    public IDialogHandler getDialogHandler()
    {
        return this.dialogHandler;
    }

    public void setDialogHandler(@Nullable IDialogHandler handler)
    {
        this.dialogHandler = handler;
    }

    @Override
    public String getModId()
    {
        return this.modId;
    }

    @Override
    @Nullable
    public IConfigInfoProvider getHoverInfoProvider()
    {
        return this.hoverInfoProvider;
    }

    @Override
    protected WidgetListConfigOptions createListWidget(int listX, int listY)
    {
        return new WidgetListConfigOptions(listX, listY,
                this.getBrowserWidth(), this.getBrowserHeight(), this.getConfigWidth(), 0.f, this.useKeybindSearch(), this);
    }

    @Override
    public void removed()
    {
        if (this.getListWidget().wereConfigsModified())
        {
            this.getListWidget().applyPendingModifications();
            this.onSettingsChanged();
            this.getListWidget().clearConfigsModifiedFlag();
        }
    }

    protected void onSettingsChanged()
    {
        ConfigManager.getInstance().onConfigsChanged(this.modId);

        if (this.hotkeyChangeListeners.size() > 0)
        {
            InputEventHandler.getKeybindManager().updateUsedKeys();
        }
    }

    @Override
    public boolean onKeyTyped(KeyInput input)
    {
        if (this.activeKeybindButton != null)
        {
            this.activeKeybindButton.onKeyPressed(input.key());
            return true;
        }
        else
        {
            if (this.getListWidget().onKeyTyped(input))
            {
                return true;
            }

            if (input.key() == KeyCodes.KEY_ESCAPE && this.getParent() != GuiUtils.getCurrentScreen())
            {
                this.closeGui(true);
                return true;
            }

            return false;
        }
    }

    @Override
    public boolean onCharTyped(CharInput input)
    {
        if (this.activeKeybindButton != null)
        {
            // Prevents the chars leaking into the search box, if we didn't pretend to handle them here
            return true;
        }

        if (this.getListWidget().onCharTyped(input))
        {
            return true;
        }

        return super.onCharTyped(input);
    }

    @Override
    public boolean onMouseClicked(Click click, boolean doubleClick)
    {
        if (super.onMouseClicked(click, doubleClick))
        {
            return true;
        }

        // When clicking on not-a-button, clear the selection
        if (this.activeKeybindButton != null)
        {
            this.activeKeybindButton.onClearSelection();
            this.setActiveKeybindButton(null);
            return true;
        }

        return false;
    }

    @Override
    public void clearOptions()
    {
        this.setActiveKeybindButton(null);
        this.hotkeyChangeListeners.clear();
    }

    @Override
    public void addKeybindChangeListener(Runnable listener)
    {
        this.hotkeyChangeListeners.add(listener);
    }

    @Override
    public ButtonPressDirtyListenerSimple getButtonPressListener()
    {
        return this.dirtyListener;
    }

    @Override
    public void setActiveKeybindButton(@Nullable ConfigButtonKeybind button)
    {
        if (this.activeKeybindButton != null)
        {
            this.activeKeybindButton.onClearSelection();
            this.updateKeybindButtons();
        }

        this.activeKeybindButton = button;

        if (this.activeKeybindButton != null)
        {
            this.activeKeybindButton.onSelected();
        }
    }

    protected void updateKeybindButtons()
    {
        for (Runnable listener : this.hotkeyChangeListeners)
        {
            listener.run();
        }
    }

    public static class ConfigOptionWrapper
    {
        private final Type type;
        @Nullable private final IConfigBase config;
        @Nullable private final String label;

        public ConfigOptionWrapper(IConfigBase config)
        {
            this.type = Type.CONFIG;
            this.config = config;
            this.label = null;
        }

        public ConfigOptionWrapper(String label)
        {
            this.type = Type.LABEL;
            this.config = null;
            this.label = label;
        }

        public Type getType()
        {
            return this.type;
        }

        @Nullable
        public IConfigBase getConfig()
        {
            return this.config;
        }

        @Nullable
        public String getLabel()
        {
            return this.label;
        }

        public static List<ConfigOptionWrapper> createFor(Collection<? extends IConfigBase> configs)
        {
            ImmutableList.Builder<ConfigOptionWrapper> builder = ImmutableList.builder();

            for (IConfigBase config : configs)
            {
                builder.add(new ConfigOptionWrapper(config));
            }

            return builder.build();
        }

        public enum Type
        {
            CONFIG,
            LABEL;
        }
    }
}
