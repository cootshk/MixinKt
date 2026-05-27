package dev.cootshk.mixinkt.platform.forge_legacy;

import dev.cootshk.mixinkt.MixinKt;
import dev.cootshk.mixinkt.MixinKtBootstrap;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MixinKt.MOD_ID, name = MixinKt.MOD_NAME, version = MixinKtBootstrap.VERSION, acceptableRemoteVersions = "*")
public class MixinKtForgeLegacy {
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MixinKtBootstrap.init();
    }
}
