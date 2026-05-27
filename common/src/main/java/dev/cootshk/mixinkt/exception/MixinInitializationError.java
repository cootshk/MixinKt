package dev.cootshk.mixinkt.exception;

import org.spongepowered.asm.launch.MixinInitialisationError;

import java.io.Serial;

/** A {@link MixinInitialisationError}, but it's spelled correctly.
 * @see org.spongepowered.asm.launch.MixinInitialisationError
 */
public class MixinInitializationError extends MixinInitialisationError {

    @Serial
    private static final long serialVersionUID = 1L;

    public MixinInitializationError() {
    }

    public MixinInitializationError(String message) {
        super(message);
    }

    public MixinInitializationError(Throwable cause) {
        super(cause);
    }

    public MixinInitializationError(String message, Throwable cause) {
        super(message, cause);
    }
}
