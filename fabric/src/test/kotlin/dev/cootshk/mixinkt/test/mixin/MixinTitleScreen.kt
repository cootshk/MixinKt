
package dev.cootshk.mixinkt.test.mixin

import dev.cootshk.mixinkt.injector.Inject
import net.minecraft.client.gui.screens.TitleScreen
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Overwrite
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(TitleScreen::class)
class MixinTitleScreen {
    @Inject(method = "init", at = At("HEAD"))
    fun init(ci: CallbackInfo) {
        println("MixinTitleScreen init")
    }

    @Overwrite
    fun shouldCloseOnEsc(): Boolean {
        println("Pressed Esc!")
        return true // pressing Escape should switch the MOTD
    }
}