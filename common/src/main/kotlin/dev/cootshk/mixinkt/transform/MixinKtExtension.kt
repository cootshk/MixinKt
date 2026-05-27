package dev.cootshk.mixinkt.transform

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.mixin.extensibility.IMixinInfo
import org.spongepowered.asm.mixin.transformer.ext.IExtension
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext

/**
 * Mixin [IExtension] that, during `preApply`, rewrites every MixinKt
 * annotation it knows about on each mixin destined for the target class so
 * Mixin's own applicator sees the SpongePowered equivalents (and the methods
 * that carry them pass Mixin's visibility checks).
 *
 * Registered from [dev.cootshk.mixinkt.MixinKtBootstrap].
 */
object MixinKtExtension : IExtension {
    override fun checkActive(environment: MixinEnvironment): Boolean = true

    override fun preApply(context: ITargetClassContext) {
        for (mixinNode in mixinNodesOf(context)) {
            applyInjectAnnotations(mixinNode)
        }
    }

    override fun postApply(context: ITargetClassContext) {}

    override fun export(
        env: MixinEnvironment,
        name: String,
        force: Boolean,
        classNode: ClassNode,
    ) {}
}

// Mixin's `ITargetClassContext` only exposes the *target* class node; the
// applied mixin class nodes live on the concrete `TargetClassContext.mixins`
// field. MixinExtras reaches for them the same way — see
// `MixinInternals.getMixinsFor`.
private val TARGET_CONTEXT_MIXINS = run {
    val cls = Class.forName("org.spongepowered.asm.mixin.transformer.TargetClassContext")
    cls.getDeclaredField("mixins").apply { isAccessible = true }
}

private val MIXIN_INFO_GET_STATE = run {
    val cls = Class.forName("org.spongepowered.asm.mixin.transformer.MixinInfo")
    cls.getDeclaredMethod("getState").apply { isAccessible = true }
}

private val MIXIN_STATE_CLASS_NODE = run {
    val cls = Class.forName("org.spongepowered.asm.mixin.transformer.MixinInfo\$State")
    cls.getDeclaredField("classNode").apply { isAccessible = true }
}

@Suppress("UNCHECKED_CAST")
private fun mixinNodesOf(context: ITargetClassContext): List<ClassNode> {
    val mixins = TARGET_CONTEXT_MIXINS.get(context) as Iterable<IMixinInfo>
    return mixins.map { info ->
        val state = MIXIN_INFO_GET_STATE.invoke(info)
        MIXIN_STATE_CLASS_NODE.get(state) as ClassNode
    }
}
