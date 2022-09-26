package tech.brito.app.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import tech.brito.app.api.Constants;
import tech.brito.app.api.Handler;
import tech.brito.app.api.ResponseEntity;
import tech.brito.app.api.StatusCode;
import tech.brito.app.exceptions.ApplicationExceptions;
import tech.brito.app.exceptions.GlobalExceptionHandler;
import tech.brito.domain.models.User;
import tech.brito.domain.services.UserService;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.isNull;
import static tech.brito.app.api.ApiUtils.requestMethodIsPost;

public class UserRegistrationHandler extends Handler {

    private final UserService userService;

    public UserRegistrationHandler(UserService userService, ObjectMapper objectMapper, GlobalExceptionHandler exceptionHandler) {
        super(objectMapper, exceptionHandler);
        this.userService = userService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {
        if(!requestMethodIsPost().test(exchange)){
            throw ApplicationExceptions.methodNotAllowed(formatErrorMessage(exchange)).get();
        }

        ResponseEntity responseEntity = doPost(exchange.getRequestBody());
        configureResponse(exchange, responseEntity);
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
}
