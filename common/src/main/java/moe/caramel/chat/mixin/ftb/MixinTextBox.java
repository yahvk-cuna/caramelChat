package moe.caramel.chat.mixin.ftb;

import dev.ftb.mods.ftblibrary.ui.TextBox;
import moe.caramel.chat.wrapper.ftb.WrapperTextBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(value = TextBox.class, remap = false)
public abstract class MixinTextBox {
    @Unique private WrapperTextBox caramelChat$wrapper;
    @Unique private int caramelChat$cacheCursorPos, caramelChat$cacheHighlightPos;
    @Shadow private String text;
    @Shadow private int cursorPos;
    @Shadow private int highlightPos;

    @Inject(
            method = "<init>(Ldev/ftb/mods/ftblibrary/ui/Panel;)V",
            at = @At("TAIL")
    )
    private void init(final CallbackInfo ci) {
        this.caramelChat$wrapper = new WrapperTextBox((TextBox) (Object) this);
    }

    // ================================ (IME)

    @Inject(method = "setText(Ljava/lang/String;Z)V", at = @At("HEAD"))
    private void setValueHead(String string, boolean triggerChange, CallbackInfo ci) {
        // setStatusToNone -> forceUpdateOrigin -> onValueChange
        if (caramelChat$wrapper.valueChanged) {
            this.caramelChat$cacheCursorPos = 0;
            this.caramelChat$cacheHighlightPos = 0;
        } else {
            this.caramelChat$setStatusToNone();
        }
    }

    @Redirect(
            method = "setText(Ljava/lang/String;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"
            )
    )
    private boolean setValuePredicateTest(final Predicate<String> predicate, final Object value) {
        if (caramelChat$wrapper.valueChanged) {
            this.caramelChat$cacheCursorPos = this.cursorPos;
            this.caramelChat$cacheHighlightPos = this.highlightPos;
            return true;
        }

        return predicate.test((String) value);
    }

    @Inject(
            method = "setText(Ljava/lang/String;Z)V",
            at = @At(
                    value = "INVOKE", shift = At.Shift.BEFORE,
                    target = "Ldev/ftb/mods/ftblibrary/ui/TextBox;moveCursorToEnd(Z)V"
            ), cancellable = true
    )
    private void setValueInvoke(String string, boolean triggerChange, CallbackInfo ci) {
        if (caramelChat$wrapper.valueChanged) {
            ci.cancel();
            // caxton Compatibility
            this.cursorPos = this.caramelChat$cacheCursorPos;
            this.highlightPos = this.caramelChat$cacheHighlightPos;
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
                    target = "Ldev/ftb/mods/ftblibrary/ui/TextBox;onTextChanged()V"
            )
    )
    private void insertTextInvoke(final String text, final CallbackInfo ci) {
        this.caramelChat$forceUpdateOrigin();
    }

    @Inject(method = "onTextChanged", at = @At("HEAD"))
    private void onTextChanged(CallbackInfo ci) {
        if (this.caramelChat$wrapper != null) {
            this.text = this.caramelChat$wrapper.getOrigin();
        }
    }

    @Inject(
            method = "deleteCharsToPos",
            at = @At(
                    value = "INVOKE", shift = At.Shift.BEFORE,
                    target = "Ldev/ftb/mods/ftblibrary/ui/TextBox;moveCursorTo(IZ)V"
            )
    )
    private void deleteCharsToPos(final int pos, final CallbackInfo ci) {
        this.caramelChat$wrapper.setOrigin(this.text);
    }

    @Inject(method = "setFocused", at = @At("TAIL"))
    private void setFocused(final boolean focused, final CallbackInfo ci) {
        if (this.caramelChat$wrapper != null) {
            this.caramelChat$wrapper.setFocused(focused);
        }
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
            this.caramelChat$wrapper.setOrigin(text);
        }
    }
}
