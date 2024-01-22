package ir.moke.api;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class SseApi implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(SseApi.class);
    private static final Map<Long, OutputStream> CONNECTION_CACHE = new HashMap<>();

    @Override
    public void handle(HttpExchange exchange) {
        Long identity = ZonedDateTime.now().toEpochSecond();
        try {
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.add("Content-Type", "text/event-stream");
            responseHeaders.add("Connection", "keep-alive");
            responseHeaders.add("X-Powered-By", "Java Native Application Server");

            exchange.sendResponseHeaders(200, 0);
            OutputStream outputStream = exchange.getResponseBody();
            CONNECTION_CACHE.put(identity, outputStream);
            logger.info("SSE Client with identity %s connected".formatted(identity));
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            boolean error = false;
            OutputStream outputStream = CONNECTION_CACHE.get(identity);
            PrintWriter writer = new PrintWriter(outputStream);
            while (!error) {
                writer.write(new char[]{0});
                writer.flush();
                error = writer.checkError();
            }
            closeConnection(identity);
        }
    }

    private static void closeConnection(long identity) {
        try {
            CONNECTION_CACHE.remove(identity);
            logger.info("SSE client %s disconnected".formatted(identity));
            logger.info("Current active sse sessions %s".formatted(CONNECTION_CACHE.size()));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
