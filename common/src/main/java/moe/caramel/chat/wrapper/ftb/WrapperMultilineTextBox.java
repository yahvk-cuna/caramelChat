package moe.caramel.chat.wrapper.ftb;

import dev.ftb.mods.ftblibrary.ui.MultilineTextBox;
import moe.caramel.chat.mixin.ftb.MultilineTextBoxAccessor;
import moe.caramel.chat.util.Rect;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;
import net.minecraft.client.gui.components.MultilineTextField;

/**
 * EditBox Component Wrapper
 */
public final class WrapperMultilineTextBox extends AbstractIMEWrapper {

    private final MultilineTextBox wrapped;
    public boolean valueChanged;

    public WrapperMultilineTextBox(final MultilineTextBox box) {
        super(box.getText());
        this.wrapped = box;
    }

    @Override
    protected void insert(final String text) {
        if (this.editable()) {
            this.wrapped.insertText(text);
        }
    }

    @Override
    protected int getCursorPos() {
        return wrapped.cursorPos();
    }

    @Override
    protected int getHighlightPos() {
        return getTextField().selectCursor;
    }

    @Override
    public boolean blockTyping() {
        final int remain = (getTextField().characterLimit() - wrapped.getText().length()) - (wrapped.getSelectedText().length());
        return remain <= 0;
    }

    @Override
    protected String getTextWithPreview() {
        return wrapped.getText();
    }

    @Override
    protected void setPreviewText(final String text) {
        this.valueChanged = true;
        this.wrapped.setText(text);
    }

    @Override
    public Rect getRect() {
        final int lineNo = getTextField().getLineAtCursor();
        var cursorPos = getCursorPos();
        var line = wrapped.getLineView();
        final int xWidth = wrapped.getGui().getTheme().getFont().width(wrapped.getText().substring(line.start(), cursorPos));
        final float x = xWidth + wrapped.getX() + 4;
        final float y = wrapped.getY() + 4 + 9 * lineNo;
        return new Rect(x, y, wrapped.getWidth(), wrapped.getHeight());
    }

    public MultilineTextField getTextField() {
        return ((MultilineTextBoxAccessor) wrapped).getTextField();
    }
}
