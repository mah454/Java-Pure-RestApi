package ir.moke.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

public class UploadFileApi implements HttpHandler {
    private static final Gson gson = new Gson();

    public UploadFileApi() {
        System.out.println("UploadFileApi: singleton object");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        System.out.println("Request method: " + method);
        if (method.equalsIgnoreCase("post")) {
            byte[] allBytes = exchange.getRequestBody().readAllBytes();
            System.out.println("Start fetch data, Request length: " + allBytes.length);
            if (allBytes.length > 0) {
                try {
                    String content = new String(allBytes);
                    JsonObject jsonObject = gson.fromJson(content, JsonObject.class);
                    if (jsonObject.has("file")) {
                        String base64File = jsonObject.get("file").getAsString();
                        storeFile(base64File);
                        System.out.println("Upload Finished");
                    } else {
                        sendResponse(exchange, "Invalid json".getBytes(), 400);
                    }
                } catch (Exception e) {
                    sendResponse(exchange, "Invalid Json".getBytes(), 500);
                    return;
                }
            }
        }
        setResponseCORSHeaders(exchange);
        sendResponse(exchange, "".getBytes(), 204);
    }

    private static void storeFile(String b64) {
        byte[] fileContent = Base64.getDecoder().decode(b64);
        File uploadedFile = new File("/tmp/uploadedFile");
        try (FileOutputStream fos = new FileOutputStream(uploadedFile)) {
            fos.write(fileContent);
            fos.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendResponse(HttpExchange exchange, byte[] response, int statusCode) {
        try {
            int contentLength = statusCode == 204 ? -1 : response.length;
            exchange.sendResponseHeaders(statusCode, contentLength);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(response);
            responseBody.flush();
            responseBody.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setResponseCORSHeaders(HttpExchange httpExchange) {
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        if (httpExchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
            httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
        }
    }
}
