package tech.brito.app.api.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import tech.brito.app.api.Constants;
import tech.brito.app.api.Handler;
import tech.brito.app.api.ResponseEntity;
import tech.brito.app.api.StatusCode;
import tech.brito.app.exceptions.ApplicationExceptions;
import tech.brito.app.exceptions.GlobalExceptionHandler;
import tech.brito.domain.models.Customer;
import tech.brito.domain.services.AccountService;
import tech.brito.domain.services.CustomerService;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.isNull;
import static tech.brito.app.api.ApiUtils.requestMethodIsGet;
import static tech.brito.app.api.ApiUtils.requestMethodIsPost;

public class CustomerHandler extends Handler {

    private final CustomerService customerService;
    private final AccountService accountService;

    public CustomerHandler(CustomerService customerService,
                           AccountService accountService,
                           ObjectMapper objectMapper,
                           GlobalExceptionHandler exceptionHandler) {
        super(objectMapper, exceptionHandler);
        this.customerService = customerService;
        this.accountService = accountService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {

        var uri = exchange.getRequestURI().toString();
        if (uri.endsWith("customers")) {
            executeCustomerHandler(exchange);
            return;
        }

        var accountHandler = new CustomerAccountHandler(accountService, customerService, objectMapper, exceptionHandler);
        accountHandler.handle(exchange);
    }

    private void executeCustomerHandler(HttpExchange exchange) throws IOException {
        if (requestMethodIsPost().test(exchange)) {
            ResponseEntity responseEntity = doPost(exchange.getRequestBody());
            configureResponse(exchange, responseEntity);
            return;
        }

        if (requestMethodIsGet().test(exchange)) {
            ResponseEntity responseEntity = doGet(exchange.getRequestBody());
            configureResponse(exchange, responseEntity);
            return;
        }

        throw ApplicationExceptions.methodNotAllowed(formatErrorMessage(exchange)).get();
    }

    private ResponseEntity doPost(InputStream inputStream) {
        var customerRequest = super.readRequest(inputStream, CustomerRequest.class);

        if (isNull(customerRequest.getName())) {
            return getReponseEntityBadRequest("Name is invalid!");
        }

        if (isNull(customerRequest.getLastName())) {
            return getReponseEntityBadRequest("Last name is invalid!");
        }

        var customer = customerService.create(new Customer(customerRequest.getName(), customerRequest.getLastName()));
        return new ResponseEntity<>(customer, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.CREATED);
    }

    private ResponseEntity doGet(InputStream inputStream) {
        var customers = customerService.findAll();
        return new ResponseEntity<>(customers, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }
}
