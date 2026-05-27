package dev.cootshk.mixinkt.injector

import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Desc
import org.spongepowered.asm.mixin.injection.callback.LocalCapture

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
/**
 * @see org.spongepowered.asm.mixin.injection.Inject
 */
annotation class Inject(
    val id: String = "",
    val method: Array<String> = [],
    val target: Array<Desc> = [],
    val slice: Array<String> = [],
    val at: Array<At>,
    val cancellable: Boolean = false,
    val locals: LocalCapture = LocalCapture.NO_CAPTURE,
    val remap: Boolean = false,
    val require: Int = -1,
    val expect: Int = 1,
    val allow: Int = -1,
    val constraints: String = "",
    val order: Int = 1000,
)