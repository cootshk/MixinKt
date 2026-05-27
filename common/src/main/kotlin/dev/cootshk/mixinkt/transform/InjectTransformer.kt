package dev.cootshk.mixinkt.transform

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import dev.cootshk.mixinkt.injector.Inject as KtInject
import org.spongepowered.asm.mixin.injection.Inject as MixinInject

private const val VISIBILITY_MASK: Int =
    Opcodes.ACC_PUBLIC or Opcodes.ACC_PROTECTED or Opcodes.ACC_PRIVATE

private val KT_INJECT_DESC: String = Type.getDescriptor(KtInject::class.java)
private val MIXIN_INJECT_DESC: String = Type.getDescriptor(MixinInject::class.java)

/**
 * Rewrites every Kotlin [KtInject] on a method of [mixinNode] into the
 * equivalent SpongePowered [MixinInject] so Mixin's own injector picks the
 * handler up. Returns the number of methods that were rewritten.
 */
fun applyInjectAnnotations(mixinNode: ClassNode): Int =
    mixinNode.methods.count { applyInject(it) }

/**
 * If [method] carries a Kotlin [KtInject] annotation, replaces it with the
 * equivalent SpongePowered [MixinInject]. Returns true on rewrite.
 *
 * Both annotations declare identical parameter names whose ASM-encoded values
 * are interchangeable — `@At`, `@Desc`, and `LocalCapture` already reference
 * the SpongePowered classes through the Kotlin annotation's imports — so the
 * values list copies over verbatim.
 *
 * Kotlin top-level functions and `object` members compile to `public` methods,
 * but Mixin rejects public injector handlers ("contains non-private static
 * method"), so we also downgrade the access flags to `private`.
 */
fun applyInject(method: MethodNode): Boolean {
    val annotations = method.visibleAnnotations ?: return false
    val idx = annotations.indexOfFirst { it.desc == KT_INJECT_DESC }
    if (idx < 0) return false

    val source = annotations[idx]
    annotations[idx] = AnnotationNode(MIXIN_INJECT_DESC).apply {
        values = source.values?.toMutableList()
    }
    method.access = (method.access and VISIBILITY_MASK.inv()) or Opcodes.ACC_PRIVATE
    return true
}
