package ir.moke.api;

import com.sun.net.httpserver.HttpExchange;
import ir.moke.utils.HttpUtils;

import java.util.List;

public class UploadFileApi extends AbstractMultipartParser {
    @Override
    public void handle(HttpExchange httpExchange, List<MultiPart> parts) {
        System.out.println(parts);
        HttpUtils.sendResponse(httpExchange, "".getBytes(), 200);
    }
}
