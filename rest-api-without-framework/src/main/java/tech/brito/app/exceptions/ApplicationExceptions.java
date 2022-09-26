package tech.brito.app.exceptions;

import java.util.function.Function;
import java.util.function.Supplier;

public class ApplicationExceptions {

    public static Function<? super Throwable, RuntimeException> invalidRequest() {
        return thr -> invalidRequest(thr.getMessage()).get();
    }

    public static Supplier<RuntimeException> invalidRequest(String message) {
        return () -> new InvalidRequestException(message);
    }

    public static Supplier<RuntimeException> methodNotAllowed(String message) {
        return () -> new MethodNotAllowedException(message);
    }

    public static Supplier<RuntimeException> notFound(String message) {
        return () -> new ResourceNotFoundException(message);
    }
}
