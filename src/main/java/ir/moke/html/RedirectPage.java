package ir.moke.html;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class RedirectPage implements HttpHandler {

    public RedirectPage() {
        System.out.println("RedirectPage: singleton object");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Location", "https://www.google.com/");
        exchange.sendResponseHeaders(301, "redirect".length());
        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write("redirect".getBytes());
        responseBody.flush();
        responseBody.close();
    }
}
