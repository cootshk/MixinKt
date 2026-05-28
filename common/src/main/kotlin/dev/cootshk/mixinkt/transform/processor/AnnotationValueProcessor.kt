package dev.cootshk.mixinkt.transform.processor

/**
 * The function to process MixinKt annotation parameters (i.e. collapse method and methods)
 */
@FunctionalInterface
fun interface AnnotationValueProcessor {
    operator fun invoke(annotations: Collection<Any>): MutableList<Any>
    companion object {
        /**
         * No changes to values need to occur.
         */
        val NO_CHANGE: AnnotationValueProcessor = { it.toMutableList() }
    }
}