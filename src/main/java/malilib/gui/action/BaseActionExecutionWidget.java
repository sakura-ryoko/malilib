package malilib.gui.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import malilib.action.ActionContext;
import malilib.action.NamedAction;
import malilib.action.util.ActionUtils;
import malilib.gui.BaseScreen;
import malilib.gui.icon.Icon;
import malilib.gui.icon.IconRegistry;
import malilib.gui.util.BorderSettings;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.ContainerWidget;
import malilib.registry.Registry;
import malilib.render.text.StyledTextLine;
import malilib.util.StringUtils;
import malilib.util.data.json.JsonUtils;

public abstract class BaseActionExecutionWidget extends ContainerWidget
{
    protected static final int DEFAULT_NORMAL_BORDER_COLOR = 0xFFFFFFFF;
    protected static final int DEFAULT_HOVER_BORDER_COLOR = 0xFFE0E020;
    protected static final int DEFAULT_BACKGROUND_COLOR = 0x00000000;

    protected final List<NamedAction> actions = new ArrayList<>();
    protected final List<StyledTextLine> widgetHoverText = new ArrayList<>(1);
    protected final List<StyledTextLine> combinedHoverText = new ArrayList<>(1);
    protected final BorderSettings editedBorderSettings = new BorderSettings(0xFFFF8000, 3);
    @Nullable protected String hoverText;
    @Nullable protected ActionWidgetContainer container;
    protected String name = "";
    protected boolean dragging;
    protected boolean resizing;
    protected boolean selected;
    protected float iconScaleX = 1.0F;
    protected float iconScaleY = 1.0F;

    public BaseActionExecutionWidget()
    {
        super(40, 20);

        this.canReceiveMouseClicks = true;
        this.canReceiveMouseMoves = true;
        this.getBorderRenderer().getNormalSettings().setDefaults(true, 1, DEFAULT_NORMAL_BORDER_COLOR);
        this.getBorderRenderer().getHoverSettings().setDefaults(true, 2, DEFAULT_HOVER_BORDER_COLOR);
        this.getBackgroundRenderer().getNormalSettings().setDefaultEnabledAndColor(true, DEFAULT_BACKGROUND_COLOR);
        this.getBackgroundRenderer().getHoverSettings().setDefaultEnabledAndColor(true, DEFAULT_BACKGROUND_COLOR);

        this.getHoverInfoFactory().setTextLineProvider("widget_hover_tip", this::getActionWidgetHoverTextLines);
    }

    public void setAction(@Nullable NamedAction action)
    {
        // TODO FIXME
        this.actions.clear();

        if (action != null)
        {
            this.actions.add(action);
        }
    }

    public void setContainer(@Nullable ActionWidgetContainer container)
    {
        this.container = container;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;

        if (org.apache.commons.lang3.StringUtils.isBlank(name) == false)
        {
            StyledTextLine text = StyledTextLine.parseFirstLine(name);
            int width = text.renderWidth + 10;

            this.setText(text);

            if (width > this.getWidth())
            {
                this.setWidth(width);
            }
        }
        else
        {
            this.setText(null);
        }
    }

    public float getIconScaleX()
    {
        return this.iconScaleX;
    }

    public float getIconScaleY()
    {
        return this.iconScaleY;
    }

    public void setIconScaleX(float iconScaleX)
    {
        this.iconScaleX = iconScaleX;
    }

    public void setIconScaleY(float iconScaleY)
    {
        this.iconScaleY = iconScaleY;
    }

    protected boolean isEditMode()
    {
        return this.container != null && this.container.isEditMode();
    }

    protected int getGridSize()
    {
        return this.container != null ? this.container.getGridSize() : -1;
    }

    @Nullable
    public String getActionWidgetHoverTextString()
    {
        return this.hoverText;
    }

    protected List<StyledTextLine> getActionWidgetHoverTextLines()
    {
        if (this.isEditMode() == false)
        {
            return this.widgetHoverText;
        }
        else if (BaseScreen.isCtrlDown() == false && BaseScreen.isShiftDown() == false)
        {
            return this.combinedHoverText;
        }

        return Collections.emptyList();
    }

    public void setActionWidgetHoverText(@Nullable String hoverText)
    {
        if (org.apache.commons.lang3.StringUtils.isBlank(hoverText))
        {
            hoverText = null;
        }

        this.hoverText = hoverText;
        this.updateHoverTexts();
    }

    protected void updateHoverTexts()
    {
        this.widgetHoverText.clear();
        this.combinedHoverText.clear();

        if (this.hoverText != null)
        {
            StyledTextLine.translate(this.widgetHoverText, this.hoverText);
            this.combinedHoverText.addAll(this.widgetHoverText);
        }

        if (this.actions.isEmpty() == false)
        {
            if (this.combinedHoverText.isEmpty() == false)
            {
                this.combinedHoverText.add(StyledTextLine.EMPTY);
            }

            for (int i = 0; i < this.actions.size(); ++i)
            {
                if (i > 0)
                {
                    this.combinedHoverText.add(StyledTextLine.EMPTY);
                }

                NamedAction action = this.actions.get(i);
                this.combinedHoverText.addAll(action.getHoverInfo());
            }
        }
    }

    public boolean isSelected()
    {
        return this.selected;
    }

    public void toggleSelected()
    {
        this.setSelected(! this.selected);
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;

        if (this.selected == false)
        {
            this.dragging = false;
        }
    }

    protected void notifyChange()
    {
        if (this.container != null)
        {
            this.container.notifyWidgetEdited();
        }
    }

    public void onAdded(BaseScreen screen)
    {
        this.updateHoverTexts();
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isEditMode())
        {
            if (mouseButton == 0)
            {
                this.startDragging(mouseX, mouseY);
            }
            else if (mouseButton == 1)
            {
                if (BaseScreen.isShiftDown())
                {
                    this.startResize(mouseX, mouseY);
                }

                return true;
            }

            return false;
        }
        else if (mouseButton == 0 && this.actions.isEmpty() == false)
        {
            // Close the current screen first, in case the action opens another screen
            if (this.container != null && this.container.shouldCloseScreenOnExecute())
            {
                BaseScreen.openScreen(null);
            }

            this.executeActions();
        }

        return true;
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        this.dragging = false;
        this.resizing = false;
        super.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseMoved(int mouseX, int mouseY)
    {
        if (this.dragging)
        {
            this.moveWidget(mouseX, mouseY);
        }
        else if (this.resizing)
        {
            this.resizeWidget(mouseX, mouseY);
        }

        return false;
    }

    @Override
    public void setSize(int width, int height)
    {
        StyledTextLine text = this.getTextForRender();

        if (text != null)
        {
            width = Math.max(width, text.renderWidth + 6);
            height = Math.max(height, 10);
        }

        super.setSize(width, height);
    }

    @Override
    public void setWidth(int width)
    {
        StyledTextLine text = this.getTextForRender();

        if (text != null)
        {
            width = Math.max(width, text.renderWidth + 6);
        }

        super.setWidth(width);
    }

    @Override
    public void setHeight(int height)
    {
        height = Math.max(height, 10);
        super.setHeight(height);
    }

    protected abstract Type getType();

    public void executeActions()
    {
        ActionContext ctx = ActionContext.COMMON;

        for (NamedAction action : this.actions)
        {
            action.execute(ctx);
        }
    }

    public void startDragging(int mouseX, int mouseY)
    {
        this.dragging = true;
        this.notifyChange();
    }

    protected abstract void startResize(int mouseX, int mouseY);

    public abstract void moveWidget(int mouseX, int mouseY);

    protected abstract void resizeWidget(int mouseX, int mouseY);

    @Override
    protected BorderSettings getActiveBorderSettings(ScreenContext ctx)
    {
        if (this.dragging || this.resizing || this.selected)
        {
            return this.editedBorderSettings;
        }

        return super.getActiveBorderSettings(ctx);
    }

    @Override
    public boolean shouldRenderHoverInfo(ScreenContext ctx)
    {
        if (this.dragging || this.resizing || BaseScreen.isShiftDown() || BaseScreen.isCtrlDown())
        {
            return false;
        }

        return super.shouldRenderHoverInfo(ctx);
    }

    @Override
    protected void renderIcon(int x, int y, float z, boolean enabled, boolean hovered, ScreenContext ctx)
    {
        Icon icon = this.getIcon();

        if (icon != null)
        {
            int usableWidth = this.getWidth() - this.padding.getHorizontalTotal();
            int usableHeight = this.getHeight() - this.padding.getVerticalTotal();
            x = this.getIconPositionX(x, usableWidth, icon.getWidth());
            y = this.getIconPositionY(y, usableHeight, icon.getHeight());
            int xSize = (int) (icon.getWidth() * this.iconScaleX);
            int ySize = (int) (icon.getHeight() * this.iconScaleY);

            icon.renderScaledAt(x, y, z + 0.025f, xSize, ySize, 1, ctx);
        }
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("type", this.getType().name().toLowerCase(Locale.ROOT));

        if (org.apache.commons.lang3.StringUtils.isBlank(this.name) == false) { obj.addProperty("name", this.name); }
        if (org.apache.commons.lang3.StringUtils.isBlank(this.hoverText) == false) { obj.addProperty("hover_text", this.hoverText); }
        if (this.icon != null) { obj.addProperty("icon_name", IconRegistry.getKeyForIcon(this.icon)); }

        this.getBorderRenderer().getNormalSettings().writeToJsonIfModified(obj, "border_normal");
        this.getBorderRenderer().getHoverSettings().writeToJsonIfModified(obj, "border_hover");
        this.getBackgroundRenderer().getNormalSettings().writeToJsonIfModified(obj, "bg_color");
        this.getBackgroundRenderer().getHoverSettings().writeToJsonIfModified(obj, "bg_color_hover");
        this.getTextSettings().writeToJsonIfModified(obj, "text_settings");
        this.iconOffset.writeToJsonIfModified(obj, "icon_offset");
        this.textOffset.writeToJsonIfModified(obj, "text_offset");
        if (this.iconScaleX != 1.0F) { obj.addProperty("icon_scale_x", this.iconScaleX); }
        if (this.iconScaleY != 1.0f) { obj.addProperty("icon_scale_y", this.iconScaleY); }

        obj.add("actions", JsonUtils.toArray(this.actions, NamedAction::toJson));

        return obj;
    }

    protected void fromJson(JsonObject obj)
    {
        this.setName(JsonUtils.getStringOrDefault(obj, "name", "?"));

        if (JsonUtils.hasString(obj, "icon_name"))
        {
            this.setIcon(Registry.ICON.getIconByKeyOrNull(JsonUtils.getStringOrDefault(obj, "icon_name", "")));
        }

        this.setActionWidgetHoverText(JsonUtils.getString(obj, "hover_text"));

        JsonUtils.getObjectIfExists(obj, "text_settings", this.getTextSettings()::fromJson);
        JsonUtils.getIntegerIfExists(obj, "bg_color", this.getBackgroundRenderer().getNormalSettings()::setColor);
        JsonUtils.getIntegerIfExists(obj, "bg_color_hover", this.getBackgroundRenderer().getHoverSettings()::setColor);
        JsonUtils.getObjectIfExists(obj, "text_offset", this.textOffset::fromJson);
        JsonUtils.getObjectIfExists(obj, "icon_offset", this.iconOffset::fromJson);
        JsonUtils.getFloatIfExists(obj, "icon_scale_x", (v) -> this.iconScaleX = v);
        JsonUtils.getFloatIfExists(obj, "icon_scale_y", (v) -> this.iconScaleY = v);
        JsonUtils.getObjectIfExists(obj, "border_normal", this.getBorderRenderer().getNormalSettings()::fromJson);
        JsonUtils.getObjectIfExists(obj, "border_hover", this.getBorderRenderer().getHoverSettings()::fromJson);

        this.actions.clear();
        ActionUtils.readActionsFromList(obj, "actions", this.actions::add);
    }

    @Nullable
    public static BaseActionExecutionWidget createFromJson(JsonElement el)
    {
        if (el.isJsonObject() == false)
        {
            return null;
        }

        JsonObject obj = el.getAsJsonObject();
        Type type = JsonUtils.getStringOrDefault(obj, "type", "").equals("radial") ? Type.RADIAL : Type.RECTANGULAR;
        BaseActionExecutionWidget widget = type.create();

        widget.fromJson(obj);

        return widget;
    }

    public enum Type
    {
        RECTANGULAR ("malilib.label.action_widgets.type.rectangular", RectangularActionExecutionWidget::new),
        RADIAL      ("malilib.label.action_widgets.type.radial",      RadialActionExecutionWidget::new);

        public static final ImmutableList<Type> VALUES = ImmutableList.copyOf(values());

        private final Supplier<BaseActionExecutionWidget> factory;
        private final String translationKey;

        Type(String translationKey, Supplier<BaseActionExecutionWidget> factory)
        {
            this.translationKey = translationKey;
            this.factory = factory;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }

        public BaseActionExecutionWidget create()
        {
            return this.factory.get();
        }
    }
}
