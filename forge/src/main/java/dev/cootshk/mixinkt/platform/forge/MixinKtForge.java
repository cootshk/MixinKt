package dev.cootshk.mixinkt.platform.forge;

import dev.cootshk.mixinkt.MixinKt;
import dev.cootshk.mixinkt.MixinKtBootstrap;
import net.minecraftforge.fml.common.Mod;

@Mod(MixinKt.MOD_ID)
public class MixinKtForge {
    public MixinKtForge() {
        MixinKtBootstrap.init();
    }
}
