package tech.brito.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import tech.brito.app.api.account.AccountHandler;
import tech.brito.app.api.customer.CustomerHandler;
import tech.brito.app.api.user.PasswordEncoder;
import tech.brito.app.api.user.UserRegistrationHandler;
import tech.brito.app.exceptions.GlobalExceptionHandler;
import tech.brito.domain.models.ChekingAccount;
import tech.brito.domain.models.Customer;
import tech.brito.domain.models.SavingsAccount;
import tech.brito.domain.models.User;
import tech.brito.domain.repositories.*;
import tech.brito.domain.services.AccountService;
import tech.brito.domain.services.CustomerService;
import tech.brito.domain.services.UserService;

import java.math.BigDecimal;
import java.util.UUID;

public class Configuration {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final UserRepository USER_REPOSITORY = new UserRepositoryImpl();
    private static final CustomerRepository CUSTOMER_REPOSITORY = new CustomerRepositoryImpl();
    private static final AccountRepository ACCOUNT_REPOSITORY = new AccountRepositoryImpl();
    private static final UserService USER_SERVICE = new UserService(USER_REPOSITORY);
    private static final CustomerService CUSTOMER_SERVICE = new CustomerService(CUSTOMER_REPOSITORY);
    private static final AccountService ACCOUNT_SERVICE = new AccountService(ACCOUNT_REPOSITORY);
    private static final GlobalExceptionHandler GLOBAL_ERROR_HANDLER = new GlobalExceptionHandler(OBJECT_MAPPER);

    static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    static UserService getUserService() {
        return USER_SERVICE;
    }

    static CustomerService getCustomerService() {
        return CUSTOMER_SERVICE;
    }

    static AccountService getAccountService() {
        return ACCOUNT_SERVICE;
    }

    public static GlobalExceptionHandler getErrorHandler() {
        return GLOBAL_ERROR_HANDLER;
    }

    public static AccountHandler getAccountHandler() {
        return new AccountHandler(getAccountService(), getCustomerService(), getObjectMapper(), getErrorHandler());
    }

    public static CustomerHandler getCustomerHandler() {
        return new CustomerHandler(getCustomerService(), getAccountService(), getObjectMapper(), getErrorHandler());
    }

    public static UserRegistrationHandler getUserRegistrationHandler() {
        return new UserRegistrationHandler(getUserService(), getObjectMapper(), getErrorHandler());
    }

    public static void initializeData() {
        getUserService().create(new User("admin", PasswordEncoder.encode("admin")));
        var customer = new Customer(UUID.fromString("4839d3d5-6e0f-4d23-a0b7-f29f647fb9f2"), "Anderson", "Garcia");
        customer = getCustomerService().create(customer);

        var cc1 = new ChekingAccount(customer);
        cc1.setId(UUID.fromString("2e9fe912-05c1-4703-b2a0-310ec0b8d720"));
        cc1.deposit(BigDecimal.valueOf(20000));

        var cc2 = new ChekingAccount(customer);
        cc2.setId(UUID.fromString("e802f4c4-ba53-4369-9bac-429a22f7ed5c"));
        cc2.deposit(BigDecimal.TEN);

        var cp = new SavingsAccount(customer, BigDecimal.ONE);
        cp.deposit(BigDecimal.valueOf(350000));

        var cc3 = new ChekingAccount(customer, 789);
        cc3.deposit(BigDecimal.TEN);

        getAccountService().save(cc1);
        getAccountService().save(cc2);
        getAccountService().save(cp);
        getAccountService().save(cc3);
    }
}
