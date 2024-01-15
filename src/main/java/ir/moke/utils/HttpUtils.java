package ir.moke.utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {
    public static void sendResponse(HttpExchange exchange, String content, int statusCode) {
        sendResponse(exchange, content.getBytes(), statusCode);
    }

    public static void sendResponseHeader(HttpExchange exchange, int code, int length) {
        try {
            exchange.sendResponseHeaders(code, length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendResponse(HttpExchange exchange, byte[] content, int statusCode) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, content.length);
            OutputStream response = exchange.getResponseBody();
            response.write(content);
            response.flush();
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> extractParameters(String query) {
        Map<String, String> queryParams = new HashMap<>();
        String[] split = query.split("&");
        for (String part : split) {
            String[] kv = part.split("=");
            if (kv.length != 2) continue;
            String key = kv[0];
            String value = kv[1];
            queryParams.put(key, value);
        }
        return queryParams;
    }
}
