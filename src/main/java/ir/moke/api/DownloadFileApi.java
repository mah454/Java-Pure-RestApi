package ir.moke.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class DownloadFileApi implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        byte[] b = new byte[10000];
        new Random().nextBytes(b);

        String fileName = "random-byte-file.data";

        exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        exchange.sendResponseHeaders(200,b.length);
        OutputStream os = exchange.getResponseBody();
        os.write(b);
        os.flush();
        os.close();
    }
}
