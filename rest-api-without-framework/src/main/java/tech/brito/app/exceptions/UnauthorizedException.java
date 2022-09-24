package tech.brito.app.exceptions;

import tech.brito.app.api.StatusCode;

public class UnauthorizedException extends ApplicationException {

    public UnauthorizedException(String message) {
        super(StatusCode.UNAUTHORIZED.getCode(), message);
    }
}
