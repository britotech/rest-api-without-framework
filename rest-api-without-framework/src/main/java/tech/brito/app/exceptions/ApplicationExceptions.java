package tech.brito.app.exceptions;

import java.util.function.Function;
import java.util.function.Supplier;

public class ApplicationExceptions {

    public static Function<? super Throwable, RuntimeException> invalidRequest() {
        return thr -> new InvalidRequestException(thr.getMessage());
    }

    public static Supplier<RuntimeException> methodNotAllowed(String message) {
        return () -> new MethodNotAllowedException(message);
    }

    public static Supplier<RuntimeException> notFound(String message) {
        return () -> new ResourceNotFoundException(message);
    }
}
