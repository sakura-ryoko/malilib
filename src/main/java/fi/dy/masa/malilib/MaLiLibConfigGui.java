package fi.dy.masa.malilib;

import java.util.Collections;
import java.util.List;
import com.google.common.collect.ImmutableList;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.BooleanHotkeyGuiWrapper;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.test.ConfigTestEnum;
import fi.dy.masa.malilib.util.StringUtils;

public class MaLiLibConfigGui extends GuiConfigsBase
{
    private static ConfigGuiTab tab = ConfigGuiTab.GENERIC;
    public static ImmutableList<ConfigTestEnum> TEST_ENUM_LIST = ConfigTestEnum.VALUES;

    public MaLiLibConfigGui()
    {
        super(10, 50, MaLiLibReference.MOD_ID, null, "malilib.gui.title.configs", String.format("%s", MaLiLibReference.MOD_VERSION));
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.clearOptions();

        int x = 10;
        int y = 26;

        for (ConfigGuiTab tab : ConfigGuiTab.values())
        {
            if (!MaLiLibReference.DEBUG_MODE)
            {
                if (tab == ConfigGuiTab.TEST || tab == ConfigGuiTab.TEST_ENUM)
                {
                    continue;
                }
            }

            if (!MaLiLibReference.EXPERIMENTAL_MODE)
            {
                if (tab == ConfigGuiTab.EXPERIMENTAL)
                {
                    continue;
                }
            }

            x += this.createButton(x, y, -1, tab) + 2;
        }
    }

    private int createButton(int x, int y, int width, ConfigGuiTab tab)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(MaLiLibConfigGui.tab != tab);
        this.addButton(button, new ButtonListener(tab, this));

        return button.getWidth();
    }

    @Override
    protected int getConfigWidth()
    {
        ConfigGuiTab tab = MaLiLibConfigGui.tab;

        if (tab == ConfigGuiTab.GENERIC)
        {
            return 200;
        }

        return super.getConfigWidth();
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        List<? extends IConfigBase> configs;
        ConfigGuiTab tab = MaLiLibConfigGui.tab;

        if (tab == ConfigGuiTab.GENERIC)
        {
            configs = MaLiLibConfigs.Generic.OPTIONS;
        }
        else if (tab == ConfigGuiTab.DEBUG)
        {
            configs = MaLiLibConfigs.Debug.OPTIONS;
        }
        else if (tab == ConfigGuiTab.TEST && MaLiLibReference.DEBUG_MODE)
        {
            configs = MaLiLibConfigs.Test.OPTIONS;
        }
        else if (tab == ConfigGuiTab.TEST_ENUM && MaLiLibReference.DEBUG_MODE)
        {
            return ConfigOptionWrapper.createFor(TEST_ENUM_LIST.stream().map(this::wrapConfig).toList());
        }
        else if (tab == ConfigGuiTab.EXPERIMENTAL && MaLiLibReference.EXPERIMENTAL_MODE)
        {
            configs = MaLiLibConfigs.Experimental.OPTIONS;
        }
        else
        {
            return Collections.emptyList();
        }

        return ConfigOptionWrapper.createFor(configs);
    }

    protected BooleanHotkeyGuiWrapper wrapConfig(ConfigTestEnum config)
    {
        return new BooleanHotkeyGuiWrapper(config.getName(), config, config.getKeybind());
    }

    private static class ButtonListener implements IButtonActionListener
    {
        private final MaLiLibConfigGui parent;
        private final ConfigGuiTab tab;

        public ButtonListener(ConfigGuiTab tab, MaLiLibConfigGui parent)
        {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            MaLiLibConfigGui.tab = this.tab;

            this.parent.reCreateListWidget(); // apply the new config width
            this.parent.getListWidget().resetScrollbarPosition();
            this.parent.initGui();
        }
    }

    public enum ConfigGuiTab
    {
        GENERIC      ("malilib.gui.title.generic"),
        DEBUG        ("malilib.gui.title.debug"),
        TEST         ("malilib.gui.title.test"),
        TEST_ENUM    ("malilib.gui.title.test_enum"),
        EXPERIMENTAL ("malilib.gui.title.experimental");

        private final String translationKey;

        ConfigGuiTab(String translationKey)
        {
            this.translationKey = translationKey;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }
    }
}
