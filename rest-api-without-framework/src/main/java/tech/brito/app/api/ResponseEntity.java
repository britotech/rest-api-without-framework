package tech.brito.app.api;


import com.sun.net.httpserver.Headers;

public class ResponseEntity<T> {

    private final T body;
    private final Headers headers;
    private final StatusCode statusCode;

    public ResponseEntity(T body, Headers headers, StatusCode statusCode) {
        this.body = body;
        this.headers = headers;
        this.statusCode = statusCode;
    }

    public T getBody() {
        return body;
    }

    public Headers getHeaders() {
        return headers;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}