package dev.cootshk.mixinkt.ext

import org.spongepowered.asm.mixin.injection.At

fun At.isEmpty(): Boolean {
    return this.value.isBlank()
}
