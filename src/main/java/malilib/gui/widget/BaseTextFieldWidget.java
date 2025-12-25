package malilib.gui.widget;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import net.minecraft.util.ChatAllowedCharacters;

import malilib.MaLiLibConfigs;
import malilib.config.value.HorizontalAlignment;
import malilib.gui.BaseScreen;
import malilib.gui.util.BorderSettings;
import malilib.gui.util.GuiUtils;
import malilib.gui.util.ScreenContext;
import malilib.gui.util.TextRegion;
import malilib.gui.widget.util.TextFieldValidator;
import malilib.input.Keys;
import malilib.overlay.message.Message;
import malilib.overlay.widget.MessageRendererWidget;
import malilib.render.ShapeRenderUtils;
import malilib.render.text.StyledText;
import malilib.render.text.StyledTextLine;
import malilib.render.text.TextRenderer;
import malilib.util.MathUtils;
import malilib.util.StringUtils;
import malilib.util.data.LeftRight;

public class BaseTextFieldWidget extends ContainerWidget
{
    public static final Pattern PATTERN_HEX_COLOR_8_6_4_3 = Pattern.compile("^(#|0x)([0-9a-fA-F]{8}|[0-9a-fA-F]{6}|[0-9a-fA-F]{4}|[0-9a-fA-F]{3})$");
    public static final Pattern PATTERN_HEX_COLOR_6_8 = Pattern.compile("^(#|0x)([0-9a-fA-F]{6}|[0-9a-fA-F]{8})$");
    public static final Pattern PATTERN_HEX_COLOR_8 = Pattern.compile("^(#|0x)[0-9a-fA-F]{8}$");

    public static final TextFieldValidator VALIDATOR_HEX_COLOR_8_6_4_3 = (str) -> PATTERN_HEX_COLOR_8_6_4_3.matcher(str).matches();
    public static final TextFieldValidator VALIDATOR_HEX_COLOR_6_8     = (str) -> PATTERN_HEX_COLOR_6_8.matcher(str).matches();
    public static final TextFieldValidator VALIDATOR_HEX_COLOR_8       = (str) -> PATTERN_HEX_COLOR_8.matcher(str).matches();
    public static final TextFieldValidator VALIDATOR_DOUBLE            = new DoubleTextFieldWidget.DoubleValidator(Double.MIN_VALUE, Double.MAX_VALUE);
    public static final TextFieldValidator VALIDATOR_DOUBLE_POSITIVE   = new DoubleTextFieldWidget.DoubleValidator(+0.0, Double.MAX_VALUE);
    public static final TextFieldValidator VALIDATOR_INTEGER           = new IntegerTextFieldWidget.IntRangeValidator(Integer.MIN_VALUE, Integer.MAX_VALUE);
    public static final TextFieldValidator VALIDATOR_INTEGER_POSITIVE  = new IntegerTextFieldWidget.IntRangeValidator(1, Integer.MAX_VALUE);

    protected final TextRegion visibleText = new TextRegion();
    protected final MessageRendererWidget messageRenderer = new MessageRendererWidget();
    private String text = "";
    protected String lastNotifiedText;
    protected String emptyValueDisplayString = "";
    @Nullable protected IInputCharacterValidator inputValidator;
    @Nullable protected TextFieldValidator textValidator;
    @Nullable protected Consumer<String> listener;
    protected int colorError = 0xFFE04040;
    protected int colorErrorDisabled = 0xFFB01010;
    protected int colorDisabled = 0xFF707070;
    protected int colorFocused = 0xFFD0D0D0;
    protected int colorUnfocused = 0xFFA0A0A0;
    protected int colorWarning;
    protected int cursorPosition;
    protected int selectionStartPosition = -1;
    protected boolean canUnFocusWithEsc;
    protected boolean isValidInput = true;
    protected boolean rightClickContextMenu;
    protected boolean showCursorPosition;
    protected boolean updateListenerAlways;
    protected boolean updateListenerFromTextSet;
    protected boolean visibleTextNeedsUpdate;

    public BaseTextFieldWidget(int width, int height)
    {
        this(width, height, "");
    }

    public BaseTextFieldWidget(int width, int height, String text)
    {
        super(width, height);

        this.lastNotifiedText = text;

        this.canBeFocused = true;
        this.canReceiveMouseClicks = true;
        this.canReceiveMouseScrolls = true;
        this.blockHoverContentFromBelow = true;
        this.rightClickContextMenu = MaLiLibConfigs.Generic.TEXT_FIELD_RIGHT_CLICK_CONTEXT_MENU.getBooleanValue();

        this.setShouldReceiveOutsideClicks(true);
        this.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, 0xFF000000);
        this.getBorderRenderer().getNormalSettings().setBorderWidthAndColor(1, this.colorUnfocused);
        this.padding.setLeft(3);
        this.padding.setRight(3);
        this.updateTextFieldSize();
        this.setText(text);

        this.messageRenderer.setMaxMessages(1);
        this.messageRenderer.getPadding().setAll(4, 6, 4, 6);
        this.messageRenderer.setAutomaticWidth(true);
    }

    @Override
    protected void onSizeChanged()
    {
        super.onSizeChanged();

        this.updateTextFieldSize();
    }

    @Override
    protected void onPositionOrSizeChanged(int oldX, int oldY)
    {
        super.onPositionOrSizeChanged(oldX, oldY);

        this.updateTextFieldSize();
    }

    protected void updateTextFieldSize()
    {
        this.visibleText.setMaxWidth(this.getMaxTextWidth());
        this.messageRenderer.setWidth(Math.max(this.getWidth(), 220));
    }

    @Override
    public void setZ(float z)
    {
        this.messageRenderer.setZ(z + 100f);
        super.setZ(z);
    }

    public void setRightClickContextMenu(boolean rightClickContextMenu)
    {
        this.rightClickContextMenu = rightClickContextMenu;
    }

    public String getText()
    {
        return this.text;
    }

    public int getTextLength()
    {
        return this.getText().length();
    }

    public void setCanUnFocusWithEsc(boolean canUnFocusWithEsc)
    {
        this.canUnFocusWithEsc = canUnFocusWithEsc;
    }

    @Override
    public List<BaseTextFieldWidget> getAllTextFields()
    {
        return ImmutableList.of(this);
    }

    public BaseTextFieldWidget setColorError(int color)
    {
        this.colorError = color;
        return this;
    }

    public BaseTextFieldWidget setColorErrorDisabled(int color)
    {
        this.colorErrorDisabled = color;
        return this;
    }

    public BaseTextFieldWidget setColorDisabled(int color)
    {
        this.colorDisabled = color;
        return this;
    }

    public BaseTextFieldWidget setColorFocused(int color)
    {
        this.colorFocused = color;
        return this;
    }

    public BaseTextFieldWidget setColorUnfocused(int color)
    {
        this.colorUnfocused = color;
        return this;
    }

    public BaseTextFieldWidget setColorWarning(int color)
    {
        this.colorWarning = color;
        return this;
    }

    public BaseTextFieldWidget setShowCursorPosition(boolean showCursorPosition)
    {
        this.showCursorPosition = showCursorPosition;
        return this;
    }

    public void setEmptyValueDisplayString(String emptyValueDisplayString)
    {
        this.emptyValueDisplayString = emptyValueDisplayString;
    }

    public BaseTextFieldWidget setText(String newText)
    {
        if (this.updateListenerFromTextSet == false)
        {
            // Set the cached string first to avoid a notification here
            this.lastNotifiedText = newText;
        }

        this.setTextInternal(newText, this.updateListenerFromTextSet);
        this.setCursorToEnd();

        return this;
    }

    public BaseTextFieldWidget setTextNoNotify(String newText)
    {
        this.lastNotifiedText = newText;
        this.setTextInternal(newText, false);
        this.setCursorToEnd();

        return this;
    }

    protected BaseTextFieldWidget setTextInternal(String newText)
    {
        return this.setTextInternal(newText, this.updateListenerAlways);
    }

    protected BaseTextFieldWidget setTextInternal(String newText, boolean updateListener)
    {
        this.text = newText != null ? newText : "";
        this.isValidInput = this.isValidText(this.text);
        this.setVisibleTextNeedsUpdate();

        if (updateListener)
        {
            this.notifyListenerIfNeeded();
        }

        this.addErrorMessage();

        return this;
    }

    protected void addErrorMessage()
    {
        if (this.isValidInput)
        {
            this.messageRenderer.clearMessages();
        }
        else if (this.textValidator != null && this.text.isEmpty() == false)
        {
            String message = this.textValidator.getErrorMessage(this.text);

            if (message != null)
            {
                this.messageRenderer.addMessage(StyledText.parse(message), Message.ERROR, 3000, 200);
                this.updateMessageRendererPosition();
            }
        }
    }

    protected void updateMessageRendererPosition()
    {
        int x = this.getX();
        int y = this.getY();
        int height = this.getHeight();
        int messagesWidth = this.messageRenderer.getWidth();
        int messagesHeight = this.messageRenderer.getHeight();
        int scaledWidth = GuiUtils.getScaledWindowWidth();

        if (x + messagesWidth > scaledWidth)
        {
            this.messageRenderer.setX(scaledWidth - messagesWidth - 2);
        }
        else
        {
            this.messageRenderer.setX(x);
        }

        if (y + height + messagesHeight > GuiUtils.getScaledWindowHeight())
        {
            this.messageRenderer.setY(y - messagesHeight);
        }
        else
        {
            this.messageRenderer.setY(y + height);
        }
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        if (enabled == false && this.isFocused())
        {
            this.setFocused(false);
        }

        super.setEnabled(enabled);
    }

    @Override
    protected void onEnabledStateChanged(boolean isEnabled)
    {
        this.updateColors(isEnabled);
    }

    @Override
    public void setFocused(boolean isFocused)
    {
        boolean enabled = this.isEnabled();
        boolean wasFocused = this.isFocused && enabled;

        super.setFocused(isFocused && enabled);
        this.updateColors(enabled);

        if (wasFocused && this.isFocused == false)
        {
            this.notifyListenerIfNeeded();
        }
    }

    public void updateColors(boolean isEnabled)
    {
        int borderColor = isEnabled ? (this.isFocused ? this.colorFocused : this.colorUnfocused) : this.colorDisabled;
        this.getBorderRenderer().getNormalSettings().setColor(borderColor);
    }

    public BaseTextFieldWidget setInputValidator(@Nullable IInputCharacterValidator inputValidator)
    {
        this.inputValidator = inputValidator;
        return this;
    }

    public BaseTextFieldWidget setTextValidator(@Nullable TextFieldValidator validator)
    {
        this.textValidator = validator;
        return this;
    }

    public BaseTextFieldWidget addChainedTextValidator(TextFieldValidator validator)
    {
        if (this.textValidator != null && validator != null)
        {
            this.textValidator = new ChainedValidator(this.textValidator, validator);
        }
        else if (this.textValidator == null)
        {
            this.textValidator = validator;
        }
        return this;
    }

    /**
     * Set the text change listener to use, if any.
     * <br><br>
     * <b>Note:</b> By default the listener is only notified when Enter is pressed,
     * or the text field loses focus.
     * If the listener should be notified on every change (characters written or removed etc.),
     * then call {@link BaseTextFieldWidget#setUpdateListenerAlways(boolean)}
     */
    public BaseTextFieldWidget setListener(@Nullable Consumer<String> listener)
    {
        this.listener = listener;
        return this;
    }

    /**
     * Set whether to update the listener on every text change (from typing) or not.
     * If this is set to false, then the listener is only notified
     * when Enter is pressed or the text field loses focus.
     */
    public BaseTextFieldWidget setUpdateListenerAlways(boolean updateAlways)
    {
        this.updateListenerAlways = updateAlways;
        return this;
    }

    /**
     * Set whether or not to update the listener from calls to the setText() method, such
     * as initializing the text or changing it from right mouse button clearing etc.
     * @param update
     * @return
     */
    public BaseTextFieldWidget setUpdateListenerFromTextSet(boolean update)
    {
        this.updateListenerFromTextSet = update;
        return this;
    }

    protected boolean isValidText(String text)
    {
        return this.textValidator == null || this.textValidator.isValidInput(text);
    }

    protected void notifyListenerIfNeeded()
    {
        if (this.listener != null &&
            this.text.equals(this.lastNotifiedText) == false &&
            this.isValidText(this.text))
        {
            this.listener.accept(this.text);
            this.lastNotifiedText = this.text;
        }
    }

    public int getCursorPosition()
    {
        return this.cursorPosition;
    }

    protected void setVisibleTextNeedsUpdate()
    {
        this.visibleTextNeedsUpdate = true;
    }

    protected void moveCursor(LeftRight direction, boolean selectText)
    {
        int newPos;

        // Nudging the position left or right while there was a selection,
        // remove the selection and move the cursor to the appropriate end of the selection
        if (this.selectionStartPosition != -1 && selectText == false)
        {
            int p1 = this.cursorPosition;
            int p2 = this.selectionStartPosition;
            newPos = direction == LeftRight.LEFT ? Math.min(p1, p2) : Math.max(p1, p2);
            this.selectionStartPosition = -1;
        }
        else
        {
            newPos = this.cursorPosition + (direction == LeftRight.LEFT ? -1 : 1);
        }

        this.setCursorPosition(newPos, selectText);
    }

    protected BaseTextFieldWidget setCursorPosition(int position, boolean selectText)
    {
        int oldPosition = this.cursorPosition;

        if (selectText == false)
        {
            this.selectionStartPosition = -1;
        }
        else if (this.selectionStartPosition < 0)
        {
            this.selectionStartPosition = oldPosition;
        }

        // The cursor can go after the last character, so not length - 1 here
        this.cursorPosition = MathUtils.clamp(position, 0, this.getTextLength());

        if (this.cursorPosition <= this.visibleText.getStartIndex() ||
            this.visibleText.isIndexVisibleWithCurrentStart(this.cursorPosition, this.text) == false)
        {
            this.setVisibleTextNeedsUpdate();
        }

        this.updateVisibleTextIfNeeded();

        return this;
    }

    protected void moveCursorToEndOfWord(LeftRight direction, boolean allowNonAlphaNumeric, boolean selectText)
    {
        int end = this.getTextLength();

        if (end == 0)
        {
            return;
        }

        int inc = direction == LeftRight.LEFT ? -1 : 1;
        int pos = Math.min(this.cursorPosition, end - 1);
        int next = pos + inc;
        Function<Character, Boolean> isWord = allowNonAlphaNumeric ? (c) -> c != ' ' : this::isAlphanumeric;
        StringBuilder sb = new StringBuilder(this.text);

        // When going left, always change the position by at least one, so that the cursor can leave
        // the word it's currently on.
        if (direction == LeftRight.LEFT)
        {
            pos = next;
            next += inc;
        }

        // First skip past all the space/non-word characters the cursor is currently on
        while (next >= 0 && next <= end && isWord.apply(sb.charAt(pos)) == false)
        {
            pos = next;
            next += inc;
        }

        next = pos + inc;

        // When going left, leave the cursor at the start index of the word
        if (direction == LeftRight.LEFT)
        {
            while (next >= 0 && isWord.apply(sb.charAt(next)))
            {
                pos = next;
                --next;
            }
        }
        // When going right, leave the cursor at the position following the word
        else
        {
            while (next <= end && isWord.apply(sb.charAt(pos)))
            {
                pos = next;
                next += inc;
            }
        }

        this.setCursorPosition(pos, selectText);
    }

    protected boolean isAlphanumeric(char c)
    {
        return Character.isAlphabetic(c);
        //return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }

    public BaseTextFieldWidget setCursorPosition(int position)
    {
        return this.setCursorPosition(position, false);
    }

    public BaseTextFieldWidget setCursorToStart()
    {
        return this.setCursorPosition(0, false);
    }

    public BaseTextFieldWidget setCursorToEnd()
    {
        return this.setCursorPosition(this.getTextLength(), false);
    }

    protected BaseTextFieldWidget setCursorToBeginning(boolean selectText)
    {
        return this.setCursorPosition(0, selectText);
    }

    protected BaseTextFieldWidget setCursorToEnd(boolean selectText)
    {
        return this.setCursorPosition(this.getTextLength(), selectText);
    }

    protected void updateVisibleTextIfNeeded()
    {
        if (this.visibleTextNeedsUpdate)
        {
            // Cursor was moved to outside of the previously visible text region
            if (this.cursorPosition <= this.visibleText.getStartIndex() ||
                this.visibleText.isIndexVisibleWithCurrentStart(this.cursorPosition, this.text) == false)
            {
                this.visibleText.alignToIndexAt(this.text, this.cursorPosition, HorizontalAlignment.CENTER);
            }
            else
            {
                this.visibleText.update(this.text);
            }

            this.visibleTextNeedsUpdate = false;
        }
    }

    protected int getClickedTextIndex(int mouseX)
    {
        int relX = mouseX - (this.getX() + this.getTextStartRelativeX());

        if (relX <= 0)
        {
            return 0;
        }

        String visibleText = this.visibleText.getText();
        String textLeftOfCursor = StringUtils.clampTextToRenderLength(visibleText, relX, LeftRight.RIGHT, "");
        int textLeftLength = textLeftOfCursor.length();
        int index = textLeftLength + this.visibleText.getStartIndex();

        // Set the click position to the right of the clicked character,
        // if the click position is on the right half of the character.
        if (visibleText.length() > textLeftLength)
        {
            int xPosInChar = relX - this.getRawStyledTextWidth(textLeftOfCursor);
            int charWidth = TextRenderer.INSTANCE.getGlyphFor(visibleText.charAt(textLeftLength)).renderWidth;

            if (xPosInChar >= charWidth / 2)
            {
                index += 1;
            }
        }

        return index;
    }

    protected int getTextStartRelativeX()
    {
        return this.getBorderRenderer().getNormalSettings().getActiveBorderWidth() + this.padding.getLeft();
    }

    /**
     * @return the maximum render width for text. Note: always at least 10 pixels.
     */
    protected int getMaxTextWidth()
    {
        int maxWidth = this.getWidth() - this.padding.getLeft() - this.padding.getRight();
        BorderSettings settings = this.getBorderRenderer().getNormalSettings();

        if (settings.isEnabled())
        {
            maxWidth -= settings.getActiveBorderWidth() * 2;
        }

        maxWidth = Math.max(10, maxWidth);

        return maxWidth;
    }

    protected boolean isUsableCharacter(char typedChar, int modifiers)
    {
        if (typedChar != 167 && ChatAllowedCharacters.isAllowedCharacter(typedChar) == false)
        {
            return false;
        }

        return this.inputValidator == null ||
               this.inputValidator.canWriteCharacter(this.cursorPosition, this.text, typedChar, modifiers);
    }

    protected void writeCharacter(char typedChar, int modifiers)
    {
        this.insertText(String.valueOf(typedChar));
    }

    protected void insertText(String text)
    {
        if (this.selectionStartPosition >= 0)
        {
            this.deleteSelectedText();
        }

        int oldLength = this.getTextLength();
        StringBuilder sb = new StringBuilder(oldLength + text.length());

        if (this.cursorPosition > 0)
        {
            sb.append(this.text, 0, this.cursorPosition);
        }

        sb.append(text);

        if (this.cursorPosition < oldLength)
        {
            sb.append(this.text.substring(this.cursorPosition));
        }

        this.setTextInternal(sb.toString());
        this.setCursorPosition(this.cursorPosition + text.length(), false);
        this.updateVisibleTextIfNeeded();
    }

    protected boolean deleteCharacterAtCursor(boolean forward)
    {
        int oldLength = this.getTextLength();
        int index = forward ? this.cursorPosition : this.cursorPosition - 1;

        if (index >= 0 && index < oldLength)
        {
            StringBuilder sb = new StringBuilder(this.text);
            int num = Character.charCount(sb.codePointAt(index));

            sb.delete(index, index + num);
            this.setTextInternal(sb.toString());

            // Move the cursor position back when using backspace to delete characters
            if (forward == false)
            {
                this.setCursorPosition(this.cursorPosition - 1, false);
            }

            this.updateVisibleTextIfNeeded();

            return true;
        }

        return false;
    }

    protected boolean deleteSelectedText()
    {
        int start = Math.min(this.cursorPosition, this.selectionStartPosition);
        int end   = Math.max(this.cursorPosition, this.selectionStartPosition);
        int textLen = this.getTextLength();

        if (start < 0 || end < 0 || start >= textLen || end > textLen)
        {
            return false;
        }

        // Remove the selection
        this.selectionStartPosition = -1;

        StringBuilder sb = new StringBuilder(this.text);
        sb.delete(start, end);
        this.setTextInternal(sb.toString());
        this.setCursorPosition(start, false);

        this.updateVisibleTextIfNeeded();

        return true;
    }

    protected boolean deleteSelectionOrCharacter(boolean forward)
    {
        if (this.getTextLength() == 0)
        {
            return false;
        }

        if (this.selectionStartPosition >= 0)
        {
            return this.deleteSelectedText();
        }
        else
        {
            return this.deleteCharacterAtCursor(forward);
        }
    }

    @Nullable
    protected String getSelectedText()
    {
        if (this.selectionStartPosition >= 0)
        {
            int start = Math.min(this.cursorPosition, this.selectionStartPosition);
            int end   = Math.max(this.cursorPosition, this.selectionStartPosition);
            int textLen = this.getTextLength();

            if (start >= 0 && end >= 0 && start < textLen && end <= textLen)
            {
                return this.text.substring(start, end);
            }
        }

        return null;
    }

    protected boolean cutSelectedText()
    {
        if (this.copySelectedText())
        {
            this.deleteSelectedText();
            return true;
        }

        return false;
    }

    protected boolean copySelectedText()
    {
        String selectedText = this.getSelectedText();

        if (selectedText != null)
        {
            BaseScreen.setStringToClipboard(selectedText);
            return true;
        }

        return false;
    }

    protected void pasteText()
    {
        if (this.selectionStartPosition >= 0)
        {
            this.deleteSelectedText();
        }

        this.insertText(BaseScreen.getStringFromClipboard());
    }

    @Override
    public boolean getShouldReceiveOutsideClicks()
    {
        return this.isFocused() && super.getShouldReceiveOutsideClicks();
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isEnabled() == false)
        {
            return false;
        }

        if (this.isFocused() == false)
        {
            this.setFocused(true);
        }

        // Clear the text field on right click
        if (mouseButton == 1)
        {
            if (this.rightClickContextMenu)
            {
                this.openRightClickContextMenu(mouseX, mouseY);
            }
            else
            {
                this.clearTextField();
            }
        }
        else
        {
            int clickedIndex = this.getClickedTextIndex(mouseX);
            boolean selectText = BaseScreen.isShiftDown();
            this.setCursorPosition(clickedIndex, selectText);
        }

        return true;
    }

    @Override
    public boolean onMouseClickedOutside(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isEnabled() && this.isFocused())
        {
            this.setFocused(false);
        }

        return false;
    }

    @Override
    protected boolean onMouseScrolled(int mouseX, int mouseY, double verticalWheelDelta, double horizontalWheelDelta)
    {
        // Allow selecting text or moving the cursor by scrolling
        if (this.isFocused() && this.getTextLength() > 0)
        {
            boolean selectText = BaseScreen.isShiftDown();
            this.setCursorPosition(this.cursorPosition + (verticalWheelDelta < 0.0 ? 1 : -1), selectText);
            return true;
        }

        return false;
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (this.isFocused())
        {
            boolean selectText = BaseScreen.isShiftDown();

            if (this.canUnFocusWithEsc && keyCode == Keys.KEY_ESCAPE)
            {
                this.setFocused(false);
            }
            else if (keyCode == Keys.KEY_BACKSPACE)
            {
                if (BaseScreen.isCtrlDown())
                {
                    this.selectionStartPosition = -1;
                    this.moveCursorToEndOfWord(LeftRight.LEFT, BaseScreen.isAltDown(), true);
                }

                this.deleteSelectionOrCharacter(false);
            }
            else if (keyCode == Keys.KEY_DELETE)
            {
                if (BaseScreen.isCtrlDown())
                {
                    this.selectionStartPosition = -1;
                    this.moveCursorToEndOfWord(LeftRight.RIGHT, BaseScreen.isAltDown(), true);
                }

                this.deleteSelectionOrCharacter(true);
            }
            else if (keyCode == Keys.KEY_ENTER || keyCode == Keys.KEY_TAB)
            {
                if (keyCode == Keys.KEY_ENTER)
                {
                    this.notifyListenerIfNeeded();
                }

                return keyCode != Keys.KEY_TAB; // Don't cancel on tab input, to allow the container to handle focus change
            }
            else if (keyCode == Keys.KEY_LEFT)
            {
                if (BaseScreen.isCtrlDown())
                {
                    this.moveCursorToEndOfWord(LeftRight.LEFT, BaseScreen.isAltDown(), selectText);
                }
                else
                {
                    this.moveCursor(LeftRight.LEFT, selectText);
                }
            }
            else if (keyCode == Keys.KEY_RIGHT)
            {
                if (BaseScreen.isCtrlDown())
                {
                    this.moveCursorToEndOfWord(LeftRight.RIGHT, BaseScreen.isAltDown(), selectText);
                }
                else
                {
                    this.moveCursor(LeftRight.RIGHT, selectText);
                }
            }
            else if (keyCode == Keys.KEY_HOME)
            {
                this.setCursorToBeginning(selectText);
            }
            else if (keyCode == Keys.KEY_END)
            {
                this.setCursorToEnd(selectText);
            }
            else if (BaseScreen.isKeyComboCtrlA(keyCode))
            {
                this.setCursorToBeginning(false);
                this.setCursorToEnd(true);
            }
            else if (BaseScreen.isKeyComboCtrlX(keyCode))
            {
                this.cutSelectedText();
            }
            else if (BaseScreen.isKeyComboCtrlC(keyCode))
            {
                this.copySelectedText();
            }
            else if (BaseScreen.isKeyComboCtrlV(keyCode))
            {
                this.pasteText();
            }
            else
            {
                return false;
            }

            return true;
        }

        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        if (this.isFocused() && this.isUsableCharacter(charIn, modifiers))
        {
            this.writeCharacter(charIn, modifiers);
            return true;
        }

        return super.onCharTyped(charIn, modifiers);
    }

    protected void clearTextField()
    {
        this.setTextInternal("", true);
        this.setCursorToEnd();
    }

    protected void copyTextToClipboard()
    {
        BaseScreen.setStringToClipboard(this.getText());
    }

    protected void pasteTextFromClipboardReplace()
    {
        this.setTextNoNotify("");
        this.pasteText();
    }

    protected void pasteTextFromClipboardInsert()
    {
        this.selectionStartPosition = -1;
        this.pasteText();
    }

    protected void selectAllText()
    {
        this.selectionStartPosition = 0;
        this.setCursorToEnd(true);
    }

    protected void openRightClickContextMenu(int mouseX, int mouseY)
    {
        this.closeOrOpenContextMenu(mouseX, mouseY, this::getContextMenuEntries);
    }

    protected List<MenuEntryWidget> getContextMenuEntries()
    {
        StyledTextLine textClear        = StyledTextLine.translateFirstLine("malilib.label.text_field.context_menu.clear");
        StyledTextLine textCopy         = StyledTextLine.translateFirstLine("malilib.label.text_field.context_menu.copy");
        StyledTextLine textPasteReplace = StyledTextLine.translateFirstLine("malilib.label.text_field.context_menu.paste_replace");
        StyledTextLine textPasteInsert  = StyledTextLine.translateFirstLine("malilib.label.text_field.context_menu.paste_insert");
        StyledTextLine textSelectAll    = StyledTextLine.translateFirstLine("malilib.label.text_field.context_menu.select_all");
        boolean hasText = this.getText().isEmpty() == false;

        MenuEntryWidget clearEntry = new MenuEntryWidget(textClear, this::clearTextField, hasText);
        clearEntry.translateAndAddHoverString("malilib.hover.text_field.menu_entry.clear",
                                              MaLiLibConfigs.Hotkeys.OPEN_CONFIG_SCREEN.getKeyBind().getKeysDisplayString());

        return ImmutableList.of(clearEntry,
                                new MenuEntryWidget(textCopy,         this::copyTextToClipboard, hasText),
                                new MenuEntryWidget(textPasteReplace, this::pasteTextFromClipboardReplace),
                                new MenuEntryWidget(textPasteInsert,  this::pasteTextFromClipboardInsert),
                                new MenuEntryWidget(textSelectAll,    this::selectAllText, hasText));
    }

    protected void renderCursor(int x, int y, float z, int color, ScreenContext ctx)
    {
        int relIndex = this.cursorPosition - this.visibleText.getStartIndex();
        color = this.selectionStartPosition != -1 ? 0xFFFF5000 : color;

        if (relIndex >= 0)
        {
            String visibleText = this.visibleText.getText();
            int fontHeight = this.getFontHeight();

            // The cursor is at the end of the text, use an underscore cursor
            if (this.cursorPosition == this.getTextLength() && this.selectionStartPosition == -1)
            {
                int offX = this.visibleText.getStyledText().renderWidth;
                ShapeRenderUtils.renderHorizontalLine(x + offX, y + fontHeight, z + 0.1f, 5, color, ctx);
            }
            else
            {
                relIndex = Math.min(relIndex, visibleText.length());
                // Use a different cursor color in selection mode
                int colorTr = (color & 0x00FFFFFF) | 0x50000000;
                int cursorExtraHeight = 2;

                int offX = this.getRawStyledTextWidth(visibleText.substring(0, relIndex));
                int y1 = y - cursorExtraHeight;
                int y2 = y;
                int y3 = y2 + fontHeight;

                ShapeRenderUtils.renderVerticalLine(x + offX, y1, z + 0.1f, cursorExtraHeight    , color, ctx);
                ShapeRenderUtils.renderVerticalLine(x + offX, y2, z + 0.1f, fontHeight           , colorTr, ctx);
                ShapeRenderUtils.renderVerticalLine(x + offX, y3, z + 0.1f, cursorExtraHeight + 1, color, ctx);
            }
        }
    }

    protected void renderVisibleText(int x, int y, float z, int textColor, ScreenContext ctx)
    {
        // A selection exists
        if (this.selectionStartPosition >= 0)
        {
            String visibleText = this.visibleText.getText();
            int selStart = Math.min(this.cursorPosition, this.selectionStartPosition);
            int selEnd   = Math.max(this.cursorPosition, this.selectionStartPosition);
            int start = this.visibleText.getStartIndex();
            int end = this.visibleText.getEndIndex();
            int visLen = visibleText.length();

            // There is at least a part of a selected region visible
            if (selStart <= end && selEnd >= start)
            {
                // Non-selected text at the start
                if (selStart > start)
                {
                    String str = visibleText.substring(0, selStart - start);
                    this.renderPlainString(x, y, z, textColor, false, str, ctx);
                    x += this.getRawStyledTextWidth(str);
                }

                int p1 = Math.max(0     , selStart - start);
                int p2 = Math.min(visLen, selEnd - start);
                String str = visibleText.substring(p1, p2);
                int selWidth = this.getRawStyledTextWidth(str);
                int fontHeight = this.getFontHeight();

                ShapeRenderUtils.renderRectangle(x, y - 2, z, selWidth, fontHeight + 3, textColor, ctx);
                this.renderPlainString(x, y, z, 0xFF000000, false, str, ctx);
                x += selWidth;

                // Non-selected text at the start
                if (selEnd <= end)
                {
                    str = visibleText.substring(selEnd - start, visLen);
                    this.renderPlainString(x, y, z, textColor, false, str, ctx);
                }

                return;
            }
        }

        // No selection
        this.renderTextLine(x, y, z, textColor, false, this.visibleText.getStyledText(), ctx);
    }

    protected void renderEmptyValueDisplayString(int x, int y, float z, int textColor, ScreenContext ctx)
    {
        StyledTextLine text = StyledTextLine.translateFirstLine("malilib.label.text_field.empty_value",
                                                                this.emptyValueDisplayString);
        this.renderTextLine(x, y, z, textColor, false, text, ctx);
    }

    protected void renderCursorPositionInfo(int x, int y, float z, ScreenContext ctx)
    {
        int pos = this.cursorPosition;
        int len = this.getTextLength();
        int start = this.visibleText.getStartIndex();
        int end = this.visibleText.getEndIndex();
        String str;

        if (start > 0 || end < len - 1)
        {
            str = String.format("%d / %d [%d - %d]", pos, len, start, end);
        }
        else
        {
            str = String.format("%d / %d", pos, len);
        }

        StyledTextLine text = StyledTextLine.parseFirstLine(str);
        int tl = text.renderWidth;
        int bw = tl + 10;
        int bh = 12;
        int bx = x + this.getWidth() - bw;
        int by = y + this.getHeight();
        int bgColor = this.getBackgroundRenderer().getNormalSettings().getColor();
        int borderColor = this.getBorderRenderer().getNormalSettings().getColor().getTop();

        ShapeRenderUtils.renderOutlinedRectangle(bx, by, z, bw, bh, bgColor, borderColor, ctx);
        this.renderTextLine(bx + 5, by + 2, z + 0.0125f, 0xFFA0A0A0, true, text, ctx);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        int textX = x + this.getTextStartRelativeX();
        int bw = this.getBorderRenderer().getNormalSettings().getActiveBorderWidth() * 2;
        int fontHeight = this.getFontHeight();
        // The font is usually 1 pixel "too high", as in it's touching the top, but not the bottom
        int yOffset = Math.max((int) Math.ceil((this.getHeight() - bw - fontHeight) / 2.0) + 1, 0);

        if ((yOffset & 0x1) == 1)
        {
            yOffset += 1;
        }

        if (this.text.isEmpty() == false)
        {
            int color;

            if (this.isValidInput)
            {
                color = this.isEnabled() ? (this.isFocused() ? this.colorFocused : this.colorUnfocused) : this.colorDisabled;
            }
            else
            {
                color = this.isEnabled() ? this.colorError : this.colorErrorDisabled;
            }

            this.renderVisibleText(textX, y + yOffset, z + 0.1f, color, ctx);
        }
        else if (org.apache.commons.lang3.StringUtils.isBlank(this.emptyValueDisplayString) == false &&
                 this.isFocused() == false)
        {
            // The translation entry sets the actual color
            this.renderEmptyValueDisplayString(textX, y + yOffset, z + 0.1f, 0xFF707070, ctx);
        }

        if (this.isFocused())
        {
            this.renderCursor(textX, y + yOffset, z + 0.1f, 0xFF00FF00, ctx);

            if (this.showCursorPosition)
            {
                this.renderCursorPositionInfo(x, y, z + 50f, ctx);
            }
        }

        int messagesHeightPre = this.messageRenderer.getHeight();
        this.messageRenderer.render(ctx);

        // Update the position when old messages are removed
        if (this.messageRenderer.getHeight() != messagesHeightPre)
        {
            this.updateMessageRendererPosition();
        }
    }

    public interface IInputCharacterValidator
    {
        boolean canWriteCharacter(int index, String currentText, char typedChar, int modifiers);
    }

    public static class ChainedValidator implements TextFieldValidator
    {
        protected final TextFieldValidator validator1;
        protected final TextFieldValidator validator2;

        public ChainedValidator(TextFieldValidator validator1, TextFieldValidator validator2)
        {
            this.validator1 = validator1;
            this.validator2 = validator2;
        }

        @Override
        public boolean isValidInput(String text)
        {
            return this.validator1.isValidInput(text) && this.validator2.isValidInput(text);
        }

        @Override
        @Nullable
        public String getErrorMessage(String text)
        {
            String error = null;

            if (this.validator1.isValidInput(text) == false)
            {
                error = this.validator1.getErrorMessage(text);
            }

            if (this.validator2.isValidInput(text) == false)
            {
                if (error != null)
                {
                    error += "\n" + this.validator2.getErrorMessage(text);
                }
                else
                {
                    error = this.validator2.getErrorMessage(text);
                }
            }

            return error;
        }
    }
}
