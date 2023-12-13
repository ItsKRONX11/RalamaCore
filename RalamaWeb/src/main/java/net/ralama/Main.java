package net.ralama;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static final Map<String, String> userCredentials = Map.of("user1", "password1", "user2", "password2");
    private static final Map<String, String> userTokens = new HashMap<>();

    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new LoginHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("Server started on port " + port);
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Check if the request contains a valid token
            String token = extractTokenFromRequest(exchange);

            if (token != null && isValidToken(token)) {
                // User is authenticated, proceed with handling the request
                handleAuthenticatedRequest(exchange);
            } else {
                // User is not authenticated, redirect to the login page or handle accordingly
                sendLoginForm(exchange);
            }
        }

        private void sendLoginForm(HttpExchange exchange) throws IOException {
            String response = "<html><body>" +
                    "<h1>Login</h1>" +
                    "<form method=\"post\">" +
                    "   <label for=\"username\">Username:</label>" +
                    "   <input type=\"text\" id=\"username\" name=\"username\"><br>" +
                    "   <label for=\"password\">Password:</label>" +
                    "   <input type=\"password\" id=\"password\" name=\"password\"><br>" +
                    "   <input type=\"submit\" value=\"Login\">" +
                    "</form>" +
                    "</body></html>";

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void handleAuthenticatedRequest(HttpExchange exchange) throws IOException {
            // Process the authenticated request
            String response = "Authenticated content goes here.";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String extractTokenFromRequest(HttpExchange exchange) {
            // Check the headers for the token (you might also want to check cookies, etc.)
            return exchange.getRequestHeaders().getFirst("Cookie");
        }

        private boolean isValidToken(String token) {
            // Check if the token is valid (e.g., exists in the userTokens map)
            return userTokens.containsValue(token);
        }
    }
}
