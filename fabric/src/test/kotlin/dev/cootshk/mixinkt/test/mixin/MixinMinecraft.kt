@file:Mixin(MinecraftClient::class)

package dev.cootshk.mixinkt.test.mixin

import dev.cootshk.mixinkt.injector.Inject
import net.minecraft.client.Minecraft as MinecraftClient
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Inject(method = ["<init>"],  at = [At("HEAD")])
fun onInit(ci: CallbackInfo) {
    println("Hello, from Kotlin!")
}