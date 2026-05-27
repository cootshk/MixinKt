package dev.cootshk.mixinkt.test

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object MixinKtTest : ModInitializer {
    const val MOD_ID: String = "mixinkt_test"
    private val logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        logger.info("MixinKt test mod initialized")
    }
}