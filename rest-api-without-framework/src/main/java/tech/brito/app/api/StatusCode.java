package tech.brito.app.api;

public enum StatusCode {

    OK(200),
    CREATED(201),
    ACCEPTED(202),
    BAD_REQUEST(400),

    UNAUTHORIZED(401),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    INTERNAL_SERVER_ERRO(500);
    private final int code;

    StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
