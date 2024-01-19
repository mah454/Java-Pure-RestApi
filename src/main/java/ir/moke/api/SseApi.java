package ir.moke.api;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class SseApi implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type", "text/event-stream");
        responseHeaders.add("Connection", "keep-alive");
        responseHeaders.add("X-Powered-By", "Native Application Server");

        exchange.sendResponseHeaders(200, 0);
        OutputStream writer = exchange.getResponseBody();
        for (int i = 0; i < 10; i++) {
            writer.write("Hello\n".getBytes());
            writer.flush();
            sleep();
        }

        writer.close();
    }

    private static void sleep() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
