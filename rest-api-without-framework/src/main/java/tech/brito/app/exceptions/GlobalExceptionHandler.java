package tech.brito.app.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import tech.brito.app.api.Constants;
import tech.brito.app.api.ErrorResponse;
import tech.brito.app.api.StatusCode;

import java.io.IOException;

public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void handle(Throwable throwable, HttpExchange exchange) {
        try {
            throwable.printStackTrace();
            exchange.getResponseHeaders().set(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            var response = getErrorResponse(throwable, exchange);
            var responseBody = exchange.getResponseBody();
            responseBody.write(objectMapper.writeValueAsBytes(response));
            responseBody.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ErrorResponse getErrorResponse(Throwable throwable, HttpExchange exchange) throws IOException {

        if (throwable instanceof UnauthorizedException ex) {
            return updateExchangeAndGenerateErrorResponse(exchange, StatusCode.UNAUTHORIZED, ex.getMessage());
        }

        if (throwable instanceof InvalidRequestException ex) {
            return updateExchangeAndGenerateErrorResponse(exchange, StatusCode.BAD_REQUEST, ex.getMessage());
        }

        if (throwable instanceof ResourceNotFoundException ex) {
            return updateExchangeAndGenerateErrorResponse(exchange, StatusCode.NOT_FOUND, ex.getMessage());
        }

        if (throwable instanceof MethodNotAllowedException ex) {
            return updateExchangeAndGenerateErrorResponse(exchange, StatusCode.METHOD_NOT_ALLOWED, ex.getMessage());
        }

        return updateExchangeAndGenerateErrorResponse(exchange, StatusCode.INTERNAL_SERVER_ERRO, throwable.getMessage());
    }

    private ErrorResponse updateExchangeAndGenerateErrorResponse(HttpExchange exchange,
                                                                 StatusCode statusCode,
                                                                 String errorMessage) throws IOException {

        exchange.sendResponseHeaders(statusCode.getCode(), 0);
        return new ErrorResponse(statusCode, errorMessage);
    }
}
