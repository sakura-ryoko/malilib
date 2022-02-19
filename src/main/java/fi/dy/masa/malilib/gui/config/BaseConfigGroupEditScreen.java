package fi.dy.masa.malilib.gui.config;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.list.ConfigOptionListWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.data.ModInfo;

public class BaseConfigGroupEditScreen extends BaseListScreen<ConfigOptionListWidget<? extends ConfigInfo>>
{
    protected final ArrayList<ConfigInfo> configs = new ArrayList<>();
    protected final ModInfo modInfo;
    @Nullable protected EventListener saveListener;
    @Nullable protected KeybindEditingScreen keyBindEditingScreen;
    protected int elementsWidth = 200;

    public BaseConfigGroupEditScreen(ModInfo modInfo, @Nullable EventListener saveListener, @Nullable GuiScreen parent)
    {
        super(8, 30, 14, 36);

        this.modInfo = modInfo;
        this.saveListener = saveListener;

        this.shouldCenter = true;
        this.renderBorder = true;
        this.useTitleHierarchy = false;
        this.backgroundColor = 0xFF000000;

        this.setParent(parent);
    }

    @Override
    protected void setScreenWidthAndHeight(int width, int height)
    {
        this.screenWidth = Math.max(520, GuiUtils.getScaledWindowWidth() - 80);
        this.screenHeight = GuiUtils.getScaledWindowHeight() - 90;
    }

    @Override
    public void onGuiClosed()
    {
        if (this.saveListener != null)
        {
            this.saveListener.onEvent();
        }

        super.onGuiClosed();
    }

    public void setSaveListener(@Nullable EventListener saveListener)
    {
        this.saveListener = saveListener;
    }

    public void setConfigs(List<? extends ConfigInfo> configs)
    {
        this.configs.clear();
        this.configs.addAll(configs);
    }

    protected List<? extends ConfigInfo> getConfigs()
    {
        return this.configs;
    }

    protected int getElementsWidth()
    {
        return this.elementsWidth;
    }

    @Nullable
    protected KeybindEditingScreen getKeybindEditingScreen()
    {
        return this.keyBindEditingScreen;
    }

    @Override
    protected ConfigOptionListWidget<? extends ConfigInfo> createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        ConfigWidgetContext ctx = new ConfigWidgetContext(this::getListWidget, this.getKeybindEditingScreen(), 0);
        return ConfigOptionListWidget.createWithExpandedGroups(listX, listY, listWidth, listHeight,
                                                               this::getElementsWidth, this.modInfo, this::getConfigs, ctx);
    }
}
