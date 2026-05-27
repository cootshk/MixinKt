package dev.cootshk.mixinkt;

import com.llamalad7.mixinextras.utils.Blackboard;
import dev.cootshk.mixinkt.exception.MixinInitializationError;
import dev.cootshk.mixinkt.transform.MixinKtExtension;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.CommandLineOptions;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.throwables.MixinError;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.service.IMixinInternal;
import org.spongepowered.asm.service.MixinService;

import java.lang.reflect.Constructor;

public class MixinKtBootstrap {
    public static final String VERSION = "0.0.0";

    private static final ILogger logger = MixinService.getService().getLogger("mixin");

    // Stage0 Init
    public static void init() {
        MixinBootstrap.init();
        logger.info("Injecting MixinKt v{}", VERSION);
        if (!start()) return;
        doInit(CommandLineOptions.defaultArgs());
    }

    // Stage1 Init
    public static boolean start() {
        var installedVersion = Blackboard.get("mixinkt.version");
        if (installedVersion != null) {
            if (!installedVersion.equals(VERSION)) {
                throw new MixinInitializationError("MixinKt version %s is already initialized, cannot init %s".formatted(installedVersion,  VERSION));
            }
            return false;
        }
        Blackboard.put("MixinKtBootstrap", VERSION);
        return true;
    }

    // Stage2 Init
    public static void doInit(CommandLineOptions args) {
        registerTransformerExtension();
    }

    /**
     * Plug {@link MixinKtExtension} into the active Mixin transformer so it
     * sees every mixin class before Mixin's applicator validates and applies
     * it. We add to {@code extensions} and re-run {@code select(...)} so the
     * extension is picked up in {@code activeExtensions} regardless of whether
     * the environment had already been selected before we registered.
     */
    private static void registerTransformerExtension() {
        MixinEnvironment env = MixinEnvironment.getDefaultEnvironment();
        IMixinTransformer transformer = (IMixinTransformer) env.getActiveTransformer();
        if (transformer == null) {
            throw new MixinInitializationError(
                    "Cannot register MixinKt extension: Mixin transformer is not active yet.");
        }
        Extensions extensions = (Extensions) transformer.getExtensions();
        for (var existing : extensions.getExtensions()) {
            if (existing instanceof MixinKtExtension) return;
        }
        extensions.add(MixinKtExtension.INSTANCE);
        extensions.select(env);
    }

    /** @see MixinBootstrap#getInternals() */
    @SuppressWarnings({"JavadocReference", "unchecked"})
    private static @NotNull IMixinInternal getInternal() throws MixinError {
        try {
            var clTransformerFactory = (Class<IMixinInternal>) Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer$Factory");
            Constructor<IMixinInternal> ctor = clTransformerFactory.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (ReflectiveOperationException ex) {
            throw new MixinError(ex);
        }
    }

    // Stage... -1?
    static {
        MixinService.boot();
        MixinService.getService().prepare();
    }
}
