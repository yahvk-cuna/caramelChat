package moe.caramel.chat.mixin.ftb;

import dev.ftb.mods.ftblibrary.ui.MultilineTextBox;
import net.minecraft.client.gui.components.MultilineTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MultilineTextBox.class, remap = false)
public interface MultilineTextBoxAccessor {
    @Accessor
    MultilineTextField getTextField();
}

