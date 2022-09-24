package tech.brito.app.exceptions;

import tech.brito.app.api.StatusCode;

class ResourceNotFoundException extends ApplicationException {

    ResourceNotFoundException(String message) {
        super(StatusCode.NOT_FOUND.getCode(), message);
    }
}
