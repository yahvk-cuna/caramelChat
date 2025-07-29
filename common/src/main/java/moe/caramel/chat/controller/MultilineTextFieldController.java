package moe.caramel.chat.controller;

import moe.caramel.chat.wrapper.ftb.WrapperMultilineTextBox;
import net.minecraft.client.gui.components.MultilineTextField;

public interface MultilineTextFieldController {
    static WrapperMultilineTextBox getWrapper(final MultilineTextField box) {
        return ((MultilineTextFieldController) box).caramelChat$wrapper();
    }

    static void setWrapper(final MultilineTextField box, WrapperMultilineTextBox w) {
        ((MultilineTextFieldController) box).setCaramelChat$wrapper(w);
    }

    WrapperMultilineTextBox caramelChat$wrapper();

    void setCaramelChat$wrapper(WrapperMultilineTextBox w);
}
