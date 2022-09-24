package tech.brito.app.exceptions;

import tech.brito.app.api.StatusCode;

class MethodNotAllowedException extends ApplicationException {

    MethodNotAllowedException(String message) {
        super(StatusCode.METHOD_NOT_ALLOWED.getCode(), message);
    }
}
