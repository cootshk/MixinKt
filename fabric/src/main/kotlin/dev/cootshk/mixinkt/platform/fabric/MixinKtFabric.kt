@file:JvmName("MixinKtFabric")

package dev.cootshk.mixinkt.platform.fabric

import dev.cootshk.mixinkt.MixinKtBootstrap

fun onPreLaunch() {
    MixinKtBootstrap.init()
}