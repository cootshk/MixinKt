package dev.cootshk.mixinkt.platform.neoforge;

import dev.cootshk.mixinkt.MixinKt;
import dev.cootshk.mixinkt.MixinKtBootstrap;
import net.neoforged.fml.common.Mod;

@Mod(MixinKt.MOD_ID)
public class MixinKtNeoForge {
    public MixinKtNeoForge() {
        MixinKtBootstrap.init();
    }
}
