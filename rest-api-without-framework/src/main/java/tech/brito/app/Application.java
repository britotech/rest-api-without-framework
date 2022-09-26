package tech.brito.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import tech.brito.app.api.StatusCode;

import java.io.IOException;
import java.net.InetSocketAddress;

import static tech.brito.app.Configuration.*;

public class Application {

    public static void main(String[] args) throws IOException {
        initializeData();
        var server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/users/register", getUserRegistrationHandler()::handle);
        configureContextWithAuthenticator(server, "/api/status", (exchange -> processRequest(exchange)));
        configureContextWithAuthenticator(server, "/api/customers", getCustomerHandler()::handle);
        configureContextWithAuthenticator(server, "/api/account", getAccountHandler()::handle);

        server.setExecutor(null);
        server.start();
    }

    private static void configureContextWithAuthenticator(HttpServer server, String path, HttpHandler handler) {
        var accountContext = server.createContext(path, handler::handle);
        accountContext.setAuthenticator(new ApiAuthenticator(getUserService()));
    }

    private static void processRequest(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            var responseText = "Welcome! This is a java rest-api without framework";
            exchange.sendResponseHeaders(StatusCode.OK.getCode(), responseText.getBytes().length);
            var output = exchange.getResponseBody();
            output.write(responseText.getBytes());
            output.flush();
        } else {
            exchange.sendResponseHeaders(StatusCode.METHOD_NOT_ALLOWED.getCode(), -1);
        }

        exchange.close();
    }
}
