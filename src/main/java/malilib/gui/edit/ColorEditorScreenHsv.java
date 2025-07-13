package malilib.gui.edit;

import java.util.function.IntConsumer;

import malilib.gui.BaseScreen;
import malilib.gui.widget.ColorEditorWidgetHsv;

public class ColorEditorScreenHsv extends BaseScreen
{
    protected final ColorEditorWidgetHsv colorEditorWidget;

    public ColorEditorScreenHsv(int colorIn, IntConsumer colorConsumer)
    {
        this.backgroundColor = 0xFF000000;
        this.renderBorder = true;
        this.useTitleHierarchy = false;

        this.colorEditorWidget = new ColorEditorWidgetHsv(colorIn, colorConsumer);

        this.setTitle("malilib.title.screen.color_editor");
        this.setScreenWidthAndHeight(300, 180);
        this.addPreScreenCloseListener(() -> colorConsumer.accept(this.colorEditorWidget.getColor()));

        this.centerOnScreen();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.colorEditorWidget);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        this.colorEditorWidget.setPosition(this.x + 4, this.y + 18);
    }
}
