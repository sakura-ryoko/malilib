package malilib.gui.edit;

import java.util.function.Consumer;
import javax.annotation.Nullable;

import malilib.gui.BaseScreen;
import malilib.gui.icon.NamedBaseIcon;
import malilib.gui.icon.NamedIcon;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.IntegerEditWidget;
import malilib.gui.widget.LabelWidget;
import malilib.render.ShapeRenderUtils;
import malilib.util.StringUtils;
import malilib.util.data.Identifier;
import malilib.util.game.wrap.RenderWrap;

public class CustomIconEditScreen extends BaseScreen
{
    @Nullable protected final NamedIcon originalIcon;
    protected final Consumer<NamedIcon> iconConsumer;
    protected final LabelWidget iconNameLabel;
    protected final LabelWidget textureNameLabel;
    protected final LabelWidget uLabel;
    protected final LabelWidget vLabel;
    protected final LabelWidget iconWidthLabel;
    protected final LabelWidget iconHeightLabel;
    protected final LabelWidget textureSheetWidthLabel;
    protected final LabelWidget textureSheetHeightLabel;
    protected final LabelWidget variantOffsetLabel;
    protected final LabelWidget previewLabel;
    protected final IntegerEditWidget uEditWidget;
    protected final IntegerEditWidget vEditWidget;
    protected final IntegerEditWidget iconWidthEditWidget;
    protected final IntegerEditWidget iconHeightEditWidget;
    protected final IntegerEditWidget textureSheetWidthEditWidget;
    protected final IntegerEditWidget textureSheetHeightEditWidget;
    protected final IntegerEditWidget variantOffsetUEditWidget;
    protected final IntegerEditWidget variantOffsetVEditWidget;
    protected final BaseTextFieldWidget iconNameTextField;
    protected final BaseTextFieldWidget textureNameTextField;
    @Nullable protected Identifier texture;
    protected int u;
    protected int v;
    protected int iconWidth = 10;
    protected int iconHeight = 10;
    protected int textureSheetWidth = 256;
    protected int textureSheetHeight = 256;
    protected int variantOffsetU;
    protected int variantOffsetV;
    protected String iconName = "name";

    public CustomIconEditScreen(Consumer<NamedIcon> iconConsumer)
    {
        this(null, iconConsumer);
    }

    public CustomIconEditScreen(@Nullable NamedIcon originalIcon, Consumer<NamedIcon> iconConsumer)
    {
        this(originalIcon, iconConsumer,
             originalIcon != null ? originalIcon.getVariantU(1) - originalIcon.getU() : 0,
             originalIcon != null ? originalIcon.getVariantV(1) - originalIcon.getV() : 0);
    }

    public CustomIconEditScreen(@Nullable NamedIcon originalIcon, Consumer<NamedIcon> iconConsumer,
                                int variantOffsetU, int variantOffsetV)
    {
        this.setTitle("malilib.title.screen.custom_icons_edit_screen");

        this.originalIcon = originalIcon;

        if (originalIcon != null)
        {
            this.texture = originalIcon.getTexture();
            this.u = originalIcon.getU();
            this.v = originalIcon.getV();
            this.iconWidth = originalIcon.getWidth();
            this.iconHeight = originalIcon.getHeight();
            this.textureSheetWidth = originalIcon.getTextureSheetWidth();
            this.textureSheetHeight = originalIcon.getTextureSheetHeight();
            this.iconName = originalIcon.getName();
        }

        this.variantOffsetU = variantOffsetU;
        this.variantOffsetV = variantOffsetV;
        this.iconConsumer = iconConsumer;

        this.iconNameLabel              = new LabelWidget("malilib.label.custom_icon_edit.icon_name");
        this.textureNameLabel           = new LabelWidget("malilib.label.custom_icon_edit.texture_name");
        this.uLabel                     = new LabelWidget("malilib.label.custom_icon_edit.coordinate.u");
        this.vLabel                     = new LabelWidget("malilib.label.custom_icon_edit.coordinate.v");
        this.iconWidthLabel             = new LabelWidget("malilib.label.custom_icon_edit.icon_width");
        this.iconHeightLabel            = new LabelWidget("malilib.label.custom_icon_edit.icon_height");
        this.textureSheetWidthLabel     = new LabelWidget("malilib.label.custom_icon_edit.texture_sheet_width");
        this.textureSheetHeightLabel    = new LabelWidget("malilib.label.custom_icon_edit.texture_sheet_height");
        this.variantOffsetLabel         = new LabelWidget("malilib.label.custom_icon_edit.variant_offset_uv");
        this.previewLabel               = new LabelWidget("malilib.label.custom_icon_edit.icon_preview");

        this.iconNameTextField = new BaseTextFieldWidget(280, 16, this.iconName);
        this.iconNameTextField.setListener(this::setIconName);

        this.textureNameTextField = new BaseTextFieldWidget(280, 16);
        this.textureNameTextField.setListener(this::setTextureName);

        if (this.texture != null)
        {
            this.textureNameTextField.setTextNoNotify(this.texture.toString());
        }

        int fieldWidth = 80;
        this.uEditWidget                    = new IntegerEditWidget(fieldWidth, 16, this.u, 0, 32767, (val) -> this.u = val);
        this.vEditWidget                    = new IntegerEditWidget(fieldWidth, 16, this.v, 0, 32767, (val) -> this.v = val);
        this.iconWidthEditWidget            = new IntegerEditWidget(fieldWidth, 16, this.iconWidth, 1, 8192, (val) -> this.iconWidth = val);
        this.iconHeightEditWidget           = new IntegerEditWidget(fieldWidth, 16, this.iconHeight, 1, 8192, (val) -> this.iconHeight = val);
        this.textureSheetWidthEditWidget    = new IntegerEditWidget(fieldWidth, 16, this.textureSheetWidth, 1, 32767, (val) -> this.textureSheetWidth = val);
        this.textureSheetHeightEditWidget   = new IntegerEditWidget(fieldWidth, 16, this.textureSheetHeight, 1, 32767, (val) -> this.textureSheetHeight = val);
        this.variantOffsetUEditWidget       = new IntegerEditWidget(fieldWidth, 16, this.variantOffsetU, -32768, 32767, (val) -> this.variantOffsetU = val);
        this.variantOffsetVEditWidget       = new IntegerEditWidget(fieldWidth, 16, this.variantOffsetV, -32768, 32767, (val) -> this.variantOffsetV = val);

        this.backgroundColor = 0xF0000000;
        this.renderBorder = true;

        this.addPreScreenCloseListener(this::createAndApplyIcon);
        this.setScreenWidthAndHeight(300, 272);
        this.centerOnScreen();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.iconNameLabel);
        this.addWidget(this.iconNameTextField);

        this.addWidget(this.textureNameLabel);
        this.addWidget(this.textureNameTextField);

        this.addWidget(this.uLabel);
        this.addWidget(this.uEditWidget);

        this.addWidget(this.vLabel);
        this.addWidget(this.vEditWidget);

        this.addWidget(this.iconWidthLabel);
        this.addWidget(this.iconWidthEditWidget);

        this.addWidget(this.iconHeightLabel);
        this.addWidget(this.iconHeightEditWidget);

        this.addWidget(this.textureSheetWidthLabel);
        this.addWidget(this.textureSheetWidthEditWidget);

        this.addWidget(this.textureSheetHeightLabel);
        this.addWidget(this.textureSheetHeightEditWidget);

        this.addWidget(this.variantOffsetLabel);
        this.addWidget(this.variantOffsetUEditWidget);
        this.addWidget(this.variantOffsetVEditWidget);

        this.addWidget(this.previewLabel);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 24;
        int rightX = x + Math.max(this.iconWidthLabel.getWidth(), this.textureSheetWidthLabel.getWidth()) + 10;
        rightX = Math.max(rightX, x + 100);

        this.iconNameLabel.setPosition(x, y);
        y += 10;
        this.iconNameTextField.setPosition(x, y);

        y += 22;
        this.textureNameLabel.setPosition(x, y);
        y += 10;
        this.textureNameTextField.setPosition(x, y);

        y += 22;
        this.uLabel.setPosition(x, y);
        this.uEditWidget.setPosition(x, y + 10);

        this.vLabel.setPosition(rightX, y);
        this.vEditWidget.setPosition(rightX, y + 10);

        y += 32;
        this.iconWidthLabel.setPosition(x, y);
        this.iconWidthEditWidget.setPosition(x, y + 10);

        this.iconHeightLabel.setPosition(rightX, y);
        this.iconHeightEditWidget.setPosition(rightX, y + 10);

        y += 32;
        this.textureSheetWidthLabel.setPosition(x, y);
        this.textureSheetWidthEditWidget.setPosition(x, y + 10);

        this.textureSheetHeightLabel.setPosition(rightX, y);
        this.textureSheetHeightEditWidget.setPosition(rightX, y + 10);

        y += 32;
        this.variantOffsetLabel.setPosition(x, y);
        this.variantOffsetUEditWidget.setPosition(x, y + 10);
        this.variantOffsetVEditWidget.setPosition(rightX, y + 10);

        y += 32;
        this.previewLabel.setPosition(x, y);
    }

    protected void createAndApplyIcon()
    {
        NamedIcon icon = this.createIcon();

        if (icon != null && icon.equals(this.originalIcon) == false)
        {
            this.iconConsumer.accept(icon);
        }
    }

    @Nullable
    protected NamedIcon createIcon()
    {
        if (this.hasValidData())
        {
            return new NamedBaseIcon(this.u, this.v, this.iconWidth, this.iconHeight,
                                     this.variantOffsetU, this.variantOffsetV,
                                     this.textureSheetWidth, this.textureSheetHeight,
                                     this.texture, this.iconName);
        }

        return null;
    }

    protected boolean hasValidData()
    {
        return this.texture != null && this.iconName != null &&
               this.iconWidth > 0 && this.iconHeight > 0 &&
               this.textureSheetWidth > 0 && this.textureSheetHeight > 0;
    }

    protected void setIconName(String str)
    {
        this.iconName = str;
    }

    protected void setTextureName(String str)
    {
        this.texture = StringUtils.identifier(str);
    }

    @Override
    protected void renderCustomContents(ScreenContext ctx)
    {
        if (this.hasValidData())
        {
            int x = this.x + 10;
            int y = this.previewLabel.getBottom() + 2;
            float z = this.z + 0.5f;

            float pw = 1.0F / (float) this.textureSheetWidth;
            float ph = 1.0F / (float) this.textureSheetHeight;
            int width = this.iconWidth;
            int height = this.iconHeight;

            if (width > 64 || height > 64)
            {
                double scale = 64.0 / (double) Math.max(width, height);
                width = (int) Math.floor(scale * width);
                height = (int) Math.floor(scale * height);
            }

            RenderWrap.setupBlendSeparate();

            //ShapeRenderUtils.renderOutline(x, y, z, width + 4, height + 4, 1, 0xFFFFFFFF);

            x += 2;
            y += 2;

            RenderWrap.bindTexture(this.texture);

            ShapeRenderUtils.renderScaledTexturedRectangle(x, y, z,
                                                           this.u, this.v,
                                                           width, height,
                                                           this.iconWidth, this.iconHeight,
                                                           pw, ph, ctx);
        }
    }
}
