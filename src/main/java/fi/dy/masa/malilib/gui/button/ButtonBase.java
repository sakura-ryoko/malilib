package fi.dy.masa.malilib.gui.button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class ButtonBase extends WidgetBase
{
    protected static final Identifier BUTTON_TEXTURE = Identifier.withDefaultNamespace("widget/button");
    protected static final Identifier BUTTON_DISABLE_TEXTURE = Identifier.withDefaultNamespace("widget/button_disabled");
    protected static final Identifier BUTTON_HOVER_TEXTURE = Identifier.withDefaultNamespace("widget/button_highlighted");

    protected final List<String> hoverStrings = new ArrayList<>();
    protected final ImmutableList<@NotNull String> hoverHelp;
    protected String displayString;
    protected boolean enabled = true;
    protected boolean visible = true;
    protected boolean hovered;
    protected boolean hoverInfoRequiresShift;
    @Nullable protected IButtonActionListener actionListener;

    public ButtonBase(int x, int y, int width, int height)
    {
        this(x, y, width, height, "");
    }

    public ButtonBase(int x, int y, int width, int height, String text)
    {
        this(x, y, width, height, text, null);
    }

    public ButtonBase(int x, int y, int width, int height, String text, @Nullable IButtonActionListener actionListener)
    {
        super(x, y, width, height);

        if (width < 0)
        {
            this.width = this.getStringWidth(text) + 10;
        }

        this.displayString = text;
        this.hoverHelp = ImmutableList.of(StringUtils.translate("malilib.gui.button.hover.hold_shift_for_info"));
    }

    public ButtonBase setActionListener(@Nullable IButtonActionListener actionListener)
    {
        this.actionListener = actionListener;
        return this;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void setDisplayString(String text)
    {
        this.displayString = text;
    }

    public boolean isMouseOver()
    {
        return this.hovered;
    }

    @Override
    protected boolean onMouseClickedImpl(MouseButtonEvent click, boolean doubleClick)
    {
        this.mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

        if (this.actionListener != null)
        {
            this.actionListener.actionPerformedWithButton(this, click.input());
        }

        return true;
    }

    @Override
    public boolean onMouseScrolledImpl(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
    {
        int mouseButton = verticalAmount < 0 ? 1 : 0;
        return this.onMouseClickedImpl(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(mouseButton, -1)), false);
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return this.enabled && this.visible && super.isMouseOver(mouseX, mouseY);
    }

    public void updateDisplayString()
    {
    }

    public boolean hasHoverText()
    {
        return this.hoverStrings.isEmpty() == false;
    }

    public void setHoverInfoRequiresShift(boolean requireShift)
    {
        this.hoverInfoRequiresShift = requireShift;
    }

    public void setHoverStrings(String... hoverStrings)
    {
        this.setHoverStrings(Arrays.asList(hoverStrings));
    }

    public void setHoverStrings(List<String> hoverStrings)
    {
        this.hoverStrings.clear();

        for (String str : hoverStrings)
        {
            str = StringUtils.translate(str);

            String[] parts = str.split("\\\\n");

            for (String part : parts)
            {
                this.hoverStrings.add(StringUtils.translate(part));
            }
        }
    }

    public List<String> getHoverStrings()
    {
        if (this.hoverInfoRequiresShift && GuiBase.isShiftDown() == false)
        {
            return this.hoverHelp;
        }

        return this.hoverStrings;
    }

    public void clearHoverStrings()
    {
        this.hoverStrings.clear();
    }

    protected int getTextureOffset(boolean isMouseOver)
    {
        return (this.enabled == false) ? 0 : (isMouseOver ? 2 : 1);
    }

    protected Identifier getTexture(boolean isMouseOver)
    {
        return (this.enabled == false) ? BUTTON_DISABLE_TEXTURE : (isMouseOver ? BUTTON_HOVER_TEXTURE : BUTTON_TEXTURE);
    }

    @Override
    public void postRenderHovered(GuiContext ctx, int mouseX, int mouseY, boolean selected)
    {
        super.postRenderHovered(ctx, mouseX, mouseY, selected);

        if (this.hasHoverText() && this.isMouseOver())
        {
            RenderUtils.drawHoverText(ctx, mouseX, mouseY, this.getHoverStrings());
        }
    }
}
