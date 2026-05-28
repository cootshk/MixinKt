package dev.cootshk.mixinkt.transform.injector

import dev.cootshk.mixinkt.transform.AnnotationTransformer
import dev.cootshk.mixinkt.transform.processor.AnnotationValueTransformer
import dev.cootshk.mixinkt.injector.Inject as KtInject
import org.spongepowered.asm.mixin.injection.Inject as MixinInject

object InjectTransformer: AnnotationTransformer(
    mixinClass = MixinInject::class,
    mixinKtClass = KtInject::class,
    downgradeAccess = true
) {
    override val transformer = AnnotationValueTransformer { map ->
        map.forEach { (name, value) ->
            println("Argument $name is of type ${value.javaClass.simpleName} and value is $value")
            // TODO: map
        }
    }
}