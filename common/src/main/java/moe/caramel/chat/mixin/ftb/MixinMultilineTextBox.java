package moe.caramel.chat.mixin.ftb;

import dev.ftb.mods.ftblibrary.ui.MultilineTextBox;
import moe.caramel.chat.controller.MultilineTextFieldController;
import moe.caramel.chat.wrapper.ftb.WrapperMultilineTextBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MultilineTextBox.class, remap = false)
public abstract class MixinMultilineTextBox {
    @Unique private WrapperMultilineTextBox caramelChat$wrapper;

    @Inject(
            method = "<init>(Ldev/ftb/mods/ftblibrary/ui/Panel;)V",
            at = @At("TAIL")
    )
    private void init(final CallbackInfo ci) {
        this.caramelChat$wrapper = new WrapperMultilineTextBox((MultilineTextBox) (Object) this);
        MultilineTextFieldController.setWrapper(this.caramelChat$wrapper.getTextField(), this.caramelChat$wrapper);
    }

    @Inject(
            method = "createTextField(Ljava/lang/String;I)V",
            at = @At("TAIL")
    )
    private void createTextField(final CallbackInfo ci) {
        if (this.caramelChat$wrapper != null) {
            MultilineTextFieldController.setWrapper(this.caramelChat$wrapper.getTextField(), this.caramelChat$wrapper);
        }
    }

    @Inject(method = "setFocused", at = @At("TAIL"))
    private void setFocused(final boolean focused, final CallbackInfo ci) {
        if (this.caramelChat$wrapper != null) {
            this.caramelChat$wrapper.setFocused(focused);
        }
    }
}
