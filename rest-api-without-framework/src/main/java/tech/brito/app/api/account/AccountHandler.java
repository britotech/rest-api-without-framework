package tech.brito.app.api.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import tech.brito.app.api.Constants;
import tech.brito.app.api.Handler;
import tech.brito.app.api.ResponseEntity;
import tech.brito.app.api.StatusCode;
import tech.brito.app.api.banktransaction.BankTransactionRequest;
import tech.brito.app.api.customer.CustomerResponse;
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

import static java.util.Objects.isNull;
import static tech.brito.app.api.ApiUtils.bigDecimalPositive;
import static tech.brito.app.api.ApiUtils.requestMethodIsPost;

public class AccountHandler extends Handler {

    private final AccountService accountService;
    private final CustomerService customerService;

    public AccountHandler(AccountService accountService,
                          CustomerService customerService,
                          ObjectMapper objectMapper,
                          GlobalExceptionHandler exceptionHandler) {

        super(objectMapper, exceptionHandler);
        this.accountService = accountService;
        this.customerService = customerService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {

        if (!requestMethodIsPost().test(exchange)) {
            throw ApplicationExceptions.methodNotAllowed(formatErrorMessage(exchange)).get();
        }

        var path = exchange.getRequestURI().toString();
        if (path.endsWith("accounts")) {
            ResponseEntity responseEntity = createAccount(exchange.getRequestBody());
            configureResponse(exchange, responseEntity);
            return;
        }

        var accountId = extractAccountId(exchange);
        if (isNull(accountId)) {
            throw ApplicationExceptions.invalidRequest("Invalid parameter!").get();
        }

        var account = accountService.findById(accountId);
        if (account.isEmpty()) {
            throw ApplicationExceptions.notFound("Account not found!").get();
        }

        executeBankTransaction(exchange, account.get());
    }

    private ResponseEntity createAccount(InputStream inputStream) {
        var accountRequest = super.readRequest(inputStream, AccountRequest.class);
        if (isNull(accountRequest.getCustomerId())) {
            return getReponseEntityBadRequest("CostumerId is invalid!");
        }

        var customer = customerService.findById(accountRequest.getCustomerId());
        if (customer.isEmpty()) {
            return getReponseEntityBadRequest("Customer not found!");
        }

        if (isNull(accountRequest.getType())) {
            return getReponseEntityBadRequest("Type is invalid!");
        }

        if (accountRequest.getType().isSavingsAccount() && !bigDecimalPositive().test(accountRequest.getWithdrawalFee())) {
            return getReponseEntityBadRequest("Withdrawal fee is invalid!");
        }

        var accountResponse = convertEntityToResponse(accountService.save(buildAccount(accountRequest, customer.get())));
        return new ResponseEntity<>(accountResponse, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.CREATED);
    }

    private AccountImpl buildAccount(AccountRequest accountRequest, Customer customer) {
        if (accountRequest.getType().isSavingsAccount()) {

            if (accountRequest.isHasAgency()) {
                return new SavingsAccount(customer, accountRequest.getAgency(), accountRequest.getWithdrawalFee());
            }

            return new SavingsAccount(customer, accountRequest.getWithdrawalFee());
        }

        if (accountRequest.isHasAgency()) {
            return new ChekingAccount(customer, accountRequest.getAgency());
        }

        return new ChekingAccount(customer);
    }

    private AccountResponse convertEntityToResponse(AccountImpl account) {
        var response = new AccountResponse();
        response.setId(account.getId());
        response.setCustomer(new CustomerResponse(account.getCustomer().getId()));
        response.setAgency(account.getAgency());
        response.setNumber(account.getNumber());
        response.setBalance(account.getBalance().setScale(2));
        response.setType(account.getAccountType());

        if (account instanceof SavingsAccount savingsAccount) {
            response.setWithdrawalFee(savingsAccount.getWithdrawalFee().setScale(2));
        }

        return response;
    }

    private UUID extractAccountId(HttpExchange exchange) {
        var path = exchange.getRequestURI().toString();
        var values = path.split("/");

        if (values.length != 5 || !validtePath(path)) {
            throw ApplicationExceptions.notFound("No context found for request").get();
        }

        return UUID.fromString(values[3]);
    }

    private boolean validtePath(String path) {
        return path.startsWith("/api/accounts/") && (path.endsWith("/withdraw") || path.endsWith("/deposit") || path.endsWith("/transfer"));
    }

    private void executeBankTransaction(HttpExchange exchange, AccountImpl account) throws IOException {
        var path = exchange.getRequestURI().toString();
        var transaction = super.readRequest(exchange.getRequestBody(), BankTransactionRequest.class);

        if (!bigDecimalPositive().test(transaction.getValue())) {
            throw ApplicationExceptions.invalidRequest("Value is invalid!").get();
        }

        if (path.endsWith("withdraw")) {
            accountService.withdraw(account, transaction.getValue());
            configureResponseOk(exchange);
            return;
        }

        if (path.endsWith("deposit")) {
            accountService.deposit(account, transaction.getValue());
            configureResponseOk(exchange);
            return;
        }

        if (isNull(transaction.getDestinationAccountId())) {
            throw ApplicationExceptions.invalidRequest("DestinationAccountId is invalid!").get();
        }

        var destinationAccount = accountService.findById(transaction.getDestinationAccountId());
        if (destinationAccount.isEmpty()) {
            throw ApplicationExceptions.invalidRequest("DestinationAccount nof found!").get();
        }

        accountService.transfer(account, transaction.getValue(), destinationAccount.get());
        configureResponseOk(exchange);
    }
}
