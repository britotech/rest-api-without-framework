package tech.brito.app.api.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import tech.brito.app.api.Constants;
import tech.brito.app.api.Handler;
import tech.brito.app.api.ResponseEntity;
import tech.brito.app.api.StatusCode;
import tech.brito.app.api.account.AccountRequest;
import tech.brito.app.api.account.AccountResponse;
import tech.brito.app.exceptions.ApplicationExceptions;
import tech.brito.app.exceptions.GlobalExceptionHandler;
import tech.brito.domain.models.AccountImpl;
import tech.brito.domain.models.ChekingAccount;
import tech.brito.domain.models.Customer;
import tech.brito.domain.models.SavingsAccount;
import tech.brito.domain.services.AccountService;
import tech.brito.domain.services.CustomerService;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static tech.brito.app.api.ApiUtils.*;

public class CustomerAccountHandler extends Handler {

    private final AccountService accountService;
    private final CustomerService customerService;

    public CustomerAccountHandler(AccountService accountService,
                                  CustomerService customerService,
                                  ObjectMapper objectMapper,
                                  GlobalExceptionHandler exceptionHandler) {

        super(objectMapper, exceptionHandler);
        this.accountService = accountService;
        this.customerService = customerService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {

        if (!requestMethodIsGet().test(exchange)) {
            throw ApplicationExceptions.methodNotAllowed(formatErrorMessage(exchange)).get();
        }

        var customerId = extractCustomerId(exchange);
        if (isNull(customerId)) {
            throw ApplicationExceptions.invalidRequest("Invalid parameter!").get();
        }

        var customer = customerService.findById(customerId);
        if (customer.isEmpty()) {
            throw ApplicationExceptions.notFound("Customer not found!").get();
        }

        ResponseEntity responseEntity = doGet(customer.get(), exchange);
        configureResponse(exchange, responseEntity);
    }

    private UUID extractCustomerId(HttpExchange exchange) {
        var path = exchange.getRequestURI().toString();
        var values = path.split("/");

        if (values.length != 5 || !path.startsWith("/api/customers/") || !path.endsWith("/accounts")) {
            throw ApplicationExceptions.notFound("No context found for request").get();
        }

        return UUID.fromString(values[3]);
    }

    private ResponseEntity doGet(Customer customer, HttpExchange exchange) {
        var accounts = accountService.findByCustomer(customer);
        var accountsResponse = accounts.stream().map(a -> convertEntityToResponse(a)).collect(Collectors.toList());
        accountsResponse.sort((a1,a2) -> a1.getAgency().compareTo(a2.getAgency()));

        return new ResponseEntity<>(accountsResponse, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }

    private AccountResponse convertEntityToResponse(AccountImpl account){
        var response = new AccountResponse();
        response.setId(account.getId());
        response.setAgency(account.getAgency());
        response.setNumber(account.getNumber());
        response.setBalance(account.getBalance().setScale(2));
        response.setType(account.getAccountType());

        if(account instanceof SavingsAccount savingsAccount){
            response.setWithdrawalFee(savingsAccount.getWithdrawalFee().setScale(2));
        }

        return response;
    }
}
