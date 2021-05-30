package ir.moke.html;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IndexPage implements HttpHandler {
    private static final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

    public IndexPage() {
        System.out.println("IndexPage: singleton object");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/")) {
            path = "index.html"; //like web.xml : <welcome-file-list></welcome-file-list>
        }
        InputStream is = contextClassLoader.getResourceAsStream("html/" + path);
        if (is != null) {
            byte[] indexPageBytes = is.readAllBytes();
            exchange.sendResponseHeaders(200, indexPageBytes.length);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(indexPageBytes);
            responseBody.flush();
            responseBody.close();
        } else {
            exchange.sendResponseHeaders(404, "Not Found".length());
        }
    }
}
