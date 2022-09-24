package tech.brito.app.exceptions;

import tech.brito.app.api.StatusCode;

class InvalidRequestException extends ApplicationException {

    public InvalidRequestException(String message) {
        super(StatusCode.BAD_REQUEST.getCode(), message);
    }
}
