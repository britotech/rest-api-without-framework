package tech.brito.app.api;

public class ErrorResponse {

    private int code;
    private String message;

    public ErrorResponse(StatusCode statusCode, String message) {
        this.code = statusCode.getCode();
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
