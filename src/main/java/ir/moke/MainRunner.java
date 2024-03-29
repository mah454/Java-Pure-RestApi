package ir.moke;

import com.sun.net.httpserver.HttpServer;
import ir.moke.api.DownloadFileApi;
import ir.moke.api.PersonApi;
import ir.moke.api.SseApi;
import ir.moke.api.UploadFileApi;
import ir.moke.html.IndexPage;
import ir.moke.html.RedirectPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainRunner {
    private static final Logger logger = LoggerFactory.getLogger(MainRunner.class);
    private static int PORT = 8080;

    static {
        String httpPort = System.getenv("HTTP_PORT");
        Optional.ofNullable(httpPort).ifPresent(item -> PORT = Integer.parseInt(item));
    }

    public static void main(String[] args) {
        //Activate multi threading
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
            httpServer.setExecutor(executorService);

            httpServer.createContext("/", new IndexPage());
            httpServer.createContext("/redirect", new RedirectPage());
            httpServer.createContext("/api/v1/person", new PersonApi());
            httpServer.createContext("/api/v1/upload", new UploadFileApi());
            httpServer.createContext("/api/v1/sse", new SseApi());
            httpServer.createContext("/api/v1/download", new DownloadFileApi());

            logger.info(String.format("Http server started on 0.0.0.0:%s%n", PORT));
            httpServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
