package dev.cootshk.mixinkt.transform

import dev.cootshk.mixinkt.ext.not
import dev.cootshk.mixinkt.transform.processor.AnnotationValueProcessor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import kotlin.reflect.KClass

/**
 * The transformer responsible for transforming a dev.cootshk annotation into an org.spongepowered one.
 * The dev.cootshk annotation stays on the class, but is unused otherwise.
 */
abstract class AnnotationTransformer(
    open val mixinClass: KClass<out Annotation>,
    open val mixinKtClass: KClass<out Annotation>,
    open val downgradeAccess: Boolean = false
) {
    protected open val transformer: AnnotationValueProcessor = AnnotationValueProcessor.NO_CHANGE

    // Descriptor of the SpongePowered annotation
    protected val mixinDesc: String get()
        = Type.getDescriptor(mixinClass.java)

    // Descriptor of our annotation
    protected val mixinKtDesc: String get()
        = Type.getDescriptor(mixinKtClass.java)

    /**
     * Returns `true` if a new annotation was added.
     */
    fun transformAnnotation(method: MethodNode): Boolean {
        val annotations = method.visibleAnnotations ?: return false
        val source = annotations.find { it.desc == mixinKtDesc } ?: return false

        // We don't strip the MixinKt annotation, we just add a @Mixin onto it.
        annotations += AnnotationNode(mixinDesc).apply {
            values = transformer(source.values)
        }

        if (downgradeAccess)
            method.access = (method.access and !VISIBILITY_MASK) or Opcodes.ACC_PRIVATE

        return true
    }

    fun applyTransformations(node: ClassNode): Int {
        return node.methods.count(::transformAnnotation)
    }

    companion object {
        private const val VISIBILITY_MASK: Int =
            Opcodes.ACC_PUBLIC or Opcodes.ACC_PROTECTED or Opcodes.ACC_PRIVATE
    }
}
