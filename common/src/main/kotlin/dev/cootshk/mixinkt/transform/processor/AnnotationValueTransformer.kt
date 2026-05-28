package dev.cootshk.mixinkt.transform.processor

import kotlin.emptyArray

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
    operator fun invoke(map: AnnotationValueMap)
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

        /**
         * Flatten an optional single parameter + an optional array parameter into one singular array inside the map.
         */
        inline fun <reified T> flattenValues(
            map: AnnotationValueMap,
            singleName: String,
            pluralName: String = singleName + "s",
            noinline skipOn: (T?) -> Boolean = ::_skipOn
        ) {
            @Suppress("UNCHECKED_CAST")
            val arr = (map[pluralName] ?: emptyList<T>()) as List<T>
            val initialObj: T? = map[singleName] as T?
            if (skipOn(initialObj)) {
                map[singleName] = arr
                map.remove(pluralName)
            } else {
                map[singleName] = listOf(initialObj, *arr.toTypedArray())
                map.remove(pluralName)
            }
        }

        /**
         * The default conditions for not adding the singleName value of the map to the array in [flattenValues]
         */
        @Suppress("FunctionName")
        fun _skipOn(item: Any?): Boolean {
            return when (item) {
                is String -> item.isBlank()
                is Array<*> -> item.isEmpty()
                is List<*> -> item.isEmpty()
                is Any -> false
                else -> true
            }
        }
    }
}