package tech.brito.app.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import io.vavr.control.Try;
import tech.brito.app.exceptions.ApplicationExceptions;
import tech.brito.app.exceptions.GlobalExceptionHandler;

import java.io.IOException;
import java.io.InputStream;

public abstract class Handler {
    protected final ObjectMapper objectMapper;
    protected final GlobalExceptionHandler exceptionHandler;

    public Handler(ObjectMapper objectMapper, GlobalExceptionHandler exceptionHandler) {
        this.objectMapper = objectMapper;
        this.exceptionHandler = exceptionHandler;
    }

    public void handle(HttpExchange exchange) {
        Try.run(() -> execute(exchange)).onFailure(thr -> exceptionHandler.handle(thr, exchange));
    }

    protected abstract void execute(HttpExchange exchange) throws Exception;

    protected <T> T readRequest(InputStream inputStream, Class<T> type) {
        return Try.of(() -> objectMapper.readValue(inputStream, type)).getOrElseThrow(ApplicationExceptions.invalidRequest());
    }

    protected <T> byte[] writeResponse(T response) {
        return Try.of(() -> objectMapper.writeValueAsBytes(response)).getOrElseThrow(ApplicationExceptions.invalidRequest());
    }

    protected static Headers getHeaders(String key, String value) {
        var headers = new Headers();
        headers.set(key, value);
        return headers;
    }

    protected void configureResponseOk(HttpExchange exchange) throws IOException {
        var responseEntity = new ResponseEntity<>("Ok", getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
        configureResponse(exchange, responseEntity);
    }

    protected void configureResponse(HttpExchange exchange, ResponseEntity responseEntity) throws IOException {
        exchange.getResponseHeaders().putAll(responseEntity.getHeaders());
        exchange.sendResponseHeaders(responseEntity.getStatusCode().getCode(), 0);
        byte[] response = writeResponse(responseEntity.getBody());
        var outputStream = exchange.getResponseBody();
        outputStream.write(response);
        outputStream.close();
    }

    protected ResponseEntity<String> getReponseEntityBadRequest(String errorMessage) {
        return new ResponseEntity<>(errorMessage, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_TEXT), StatusCode.BAD_REQUEST);
    }

    protected String formatErrorMessage(HttpExchange exchange) {
        return String.format("Method %s is not allowed for %s", exchange.getRequestMethod(), exchange.getRequestURI());
    }
}
