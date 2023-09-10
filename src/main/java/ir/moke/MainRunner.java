package ir.moke;

import com.sun.net.httpserver.HttpServer;
import ir.moke.api.PersonApi;
import ir.moke.api.UploadFileApi;
import ir.moke.html.IndexPage;
import ir.moke.html.RedirectPage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainRunner {
    public static void main(String[] args) {
        //Activate multi threading
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);
            httpServer.setExecutor(executorService);

            httpServer.createContext("/", new IndexPage());
            httpServer.createContext("/redirect", new RedirectPage());
            httpServer.createContext("/api/v1/person", new PersonApi());
            httpServer.createContext("/api/v1/upload", new UploadFileApi());
            httpServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
