package tech.brito.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import tech.brito.app.api.StatusCode;
import tech.brito.app.api.user.PasswordEncoder;
import tech.brito.app.api.user.UserRegistrationHandler;
import tech.brito.domain.User;

import java.io.IOException;
import java.net.InetSocketAddress;

import static tech.brito.app.Configuration.*;

public class Application {

    public static void main(String[] args) throws IOException {
        initializeData();

        var server = HttpServer.create(new InetSocketAddress(8080), 0);

        var userRegistrationHandler = new UserRegistrationHandler(getUserService(), getObjectMapper(), getErrorHandler());
        server.createContext("/api/users/register", userRegistrationHandler::handle);

        var context = server.createContext("/api/status", (exchange -> processRequest(exchange)));
        context.setAuthenticator(new ApiAuthenticator(getUserService()));
        server.setExecutor(null);
        server.start();
    }

    private static void initializeData() {
        getUserService().create(new User("admin", PasswordEncoder.encode("admin")));
    }

    private static void processRequest(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {

            var responseText = "Bem vindo! Essa é uma rest-api java sem framework";
            exchange.sendResponseHeaders(StatusCode.OK.getCode(), responseText.getBytes().length);
            var output = exchange.getResponseBody();
            output.write(responseText.getBytes());
            output.flush();
        } else {
            exchange.sendResponseHeaders(StatusCode.METHOD_NOT_ALLOWED.getCode(), -1);// 405 Method Not Allowed
        }

        exchange.close();
    }
}
