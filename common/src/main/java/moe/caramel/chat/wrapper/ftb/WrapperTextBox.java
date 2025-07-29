package moe.caramel.chat.wrapper.ftb;

import dev.ftb.mods.ftblibrary.ui.TextBox;
import moe.caramel.chat.mixin.ftb.TextBoxAccessor;
import moe.caramel.chat.util.Rect;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;

/**
 * EditBox Component Wrapper
 */
public final class WrapperTextBox extends AbstractIMEWrapper {

    private final TextBox wrapped;
    public boolean valueChanged;

    public WrapperTextBox(final TextBox box) {
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
        return wrapped.getCursorPos();
    }

    @Override
    protected int getHighlightPos() {
        return ((TextBoxAccessor) wrapped).getHighlightPos();
    }

    @Override
    public boolean editable() {
        return wrapped.allowInput();
    }

    @Override
    public boolean blockTyping() {
        if (!this.wrapped.allowInput()) {
            return true;
        }

        final int remain = (((TextBoxAccessor) wrapped).getMaxLength() - wrapped.getText().length()) - (wrapped.getSelectedText().length());
        return remain <= 0;
    }

    @Override
    protected String getTextWithPreview() {
        return wrapped.getText();
    }

    @Override
    protected void setPreviewText(final String text) {
        this.valueChanged = true;
        this.wrapped.setText(text, false);
    }

    @Override
    public Rect getRect() {
        final int xWidth = wrapped.getGui().getTheme().getFont().width(wrapped.getText().substring(((TextBoxAccessor) wrapped).getDisplayPos(), wrapped.getCursorPos()));
        final float x = xWidth + wrapped.getX() + 4;
        final float y = wrapped.getGui().getTheme().getFont().lineHeight + wrapped.getY() + ((wrapped.getHeight() - 8) / 2.0f);
        return new Rect(x, y, wrapped.getWidth(), wrapped.getHeight());
    }
}
