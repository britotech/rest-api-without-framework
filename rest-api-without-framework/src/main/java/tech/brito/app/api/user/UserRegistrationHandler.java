package tech.brito.app.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import tech.brito.app.api.Constants;
import tech.brito.app.api.Handler;
import tech.brito.app.api.ResponseEntity;
import tech.brito.app.api.StatusCode;
import tech.brito.app.exceptions.ApplicationExceptions;
import tech.brito.app.exceptions.GlobalExceptionHandler;
import tech.brito.domain.User;
import tech.brito.domain.UserService;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.isNull;

public class UserRegistrationHandler extends Handler {

    private final UserService userService;

    public UserRegistrationHandler(UserService userService, ObjectMapper objectMapper, GlobalExceptionHandler exceptionHandler) {
        super(objectMapper, exceptionHandler);
        this.userService = userService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            throw ApplicationExceptions.methodNotAllowed(formatErrorMessage(exchange)).get();
        }

        ResponseEntity responseEntity = doPost(exchange.getRequestBody());
        exchange.getResponseHeaders().putAll(responseEntity.getHeaders());
        exchange.sendResponseHeaders(responseEntity.getStatusCode().getCode(), 0);
        byte[] response = super.writeResponse(responseEntity.getBody());

        var outputStream = exchange.getResponseBody();
        outputStream.write(response);
        outputStream.close();
    }

    private String formatErrorMessage(HttpExchange exchange) {
        return String.format("Method %s is not allowed for %s", exchange.getRequestMethod(), exchange.getRequestURI());
    }

    private ResponseEntity doPost(InputStream inputStream) {
        var userRequest = super.readRequest(inputStream, UserRegistrationRequest.class);

        if (isNull(userRequest.getUsername())) {
            return getReponseEntityBadRequest("Username is invalid!");
        }

        if (isNull(userRequest.getPassword())) {
            return getReponseEntityBadRequest("Password is invalid!");
        }

        if (userService.findByUsername(userRequest.getUsername()).isPresent()) {
            return getReponseEntityBadRequest("Username is already taken!");
        }

        var user = userService.create(new User(userRequest.getUsername(), PasswordEncoder.encode(userRequest.getPassword())));
        var userResponse = new UserRegistrationResponse(user.getId(), user.getUsername());
        return new ResponseEntity<>(userResponse, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.CREATED);
    }

    private ResponseEntity<String> getReponseEntityBadRequest(String errorMessage) {
        return new ResponseEntity<>(errorMessage, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_TEXT), StatusCode.BAD_REQUEST);
    }
}
