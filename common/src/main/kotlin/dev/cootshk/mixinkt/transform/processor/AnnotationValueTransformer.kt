package dev.cootshk.mixinkt.transform.processor

/**
 * A cleaner function that allows you to modify a map of annotation name=value parameters, without having to touch the list directly.
 * @see AnnotationValueProcessor
 */
@FunctionalInterface
fun interface AnnotationValueTransformer : AnnotationValueProcessor {
    /**
     * Mutate the map of annotation parameter values for when the annotation is transformed.
     * @param map A mutable map of the parameter name: the parameter value.
     * @return Nothing, you should mutate the map directly.
     */
    operator fun invoke(map: MutableMap<String, Any>)
    override operator fun invoke(annotations: Collection<Any>): MutableList<Any> =
        from(annotations)
        .also(::invoke)
        .consume()
    companion object {
        private typealias AnnotationValueMap = MutableMap<String, Any>

        /**
         * Turns an annotation list into a map.
         * @see org.objectweb.asm.tree.AnnotationNode.values
         */
        fun from(annotations: Collection<Any>): AnnotationValueMap {
            val annotations = annotations.toTypedArray()
            val map = mutableMapOf<String, Any>()
            var i = 0
            while (i < annotations.size) {
                val key = annotations[i++] as? String ?: throw IllegalStateException("List desynced?")
                val value = annotations[i++]
                map[key] = value
            }
            return map
        }


        /**
         * Turns an annotation map into a list
         * @see from
         */
        fun consume(map: AnnotationValueMap): MutableList<Any> {
            val out: MutableList<Any> = mutableListOf()
            for ((key, value) in map) {
                out += key
                out += value
            }
            return out
        }
        internal fun AnnotationValueMap.consume(): MutableList<Any> = consume(this)
    }
}