package moe.caramel.chat.mixin.ftb;

import dev.ftb.mods.ftblibrary.ui.TextBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TextBox.class, remap = false)
public interface TextBoxAccessor {
    @Accessor
    int getHighlightPos();

    @Accessor
    int getDisplayPos();

    @Accessor
    int getMaxLength();
}

