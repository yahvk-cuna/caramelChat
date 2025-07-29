package moe.caramel.chat.mixin.ftb;

import moe.caramel.chat.controller.MultilineTextFieldController;
import moe.caramel.chat.wrapper.ftb.WrapperMultilineTextBox;
import net.minecraft.client.gui.components.MultilineTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MultilineTextField.class)
public abstract class MixinMultilineTextField implements MultilineTextFieldController {
    @Unique public WrapperMultilineTextBox caramelChat$wrapper;
    @Unique private int caramelChat$cacheCursorPos, caramelChat$cacheHighlightPos;
    @Shadow private int cursor;
    @Shadow private int selectCursor;
    @Shadow private String value;

    public WrapperMultilineTextBox caramelChat$wrapper() {
        return caramelChat$wrapper;
    }

    public void setCaramelChat$wrapper(WrapperMultilineTextBox w) {
        this.caramelChat$wrapper = w;
    }


    // ================================ (IME)

    @Inject(method = "setValue(Ljava/lang/String;)V", at = @At("HEAD"))
    private void setValueHead(final String string, final CallbackInfo ci) {
        if (caramelChat$wrapper == null) return;

        // setStatusToNone -> forceUpdateOrigin -> onValueChange
        if (caramelChat$wrapper.valueChanged) {
            this.caramelChat$cacheCursorPos = this.cursor;
            this.caramelChat$cacheHighlightPos = this.selectCursor;
        } else {
            this.caramelChat$setStatusToNone();
        }
    }

    @Inject(
            method = "onValueChange",
            at = @At(
                    value = "INVOKE", shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/client/gui/components/MultilineTextField;reflowDisplayLines()V"
            ), cancellable = true
    )
    private void onValueChange(CallbackInfo ci) {
        if (caramelChat$wrapper == null) return;

        if (caramelChat$wrapper.valueChanged) {
            ci.cancel();
            // caxton Compatibility
            this.cursor = this.caramelChat$cacheCursorPos;
            this.selectCursor = this.caramelChat$cacheHighlightPos;
            this.caramelChat$wrapper.valueChanged = false;
            return;
        }

        this.caramelChat$forceUpdateOrigin();
    }


    @Inject(method = "insertText", at = @At("HEAD"))
    private void insertTextHead(final String text, final CallbackInfo ci) {
        // setStatusToNone -> forceUpdateOrigin -> onValueChange
        this.caramelChat$setStatusToNone();
    }

    @Inject(
            method = "insertText",
            at = @At(
                    value = "INVOKE", shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/client/gui/components/MultilineTextField;onValueChange()V"
            )
    )
    private void insertTextInvoke(final String text, final CallbackInfo ci) {
        this.caramelChat$forceUpdateOrigin();
    }

    @Unique
    private void caramelChat$setStatusToNone() {
        if (this.caramelChat$wrapper != null) {
            this.caramelChat$wrapper.setToNoneStatus();
        }
    }

    @Unique
    private void caramelChat$forceUpdateOrigin() {
        if (this.caramelChat$wrapper != null) {
            this.caramelChat$wrapper.setOrigin(value);
        }
    }
}
