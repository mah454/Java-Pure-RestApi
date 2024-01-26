package ir.moke.api;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ir.moke.utils.HttpUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UploadFileApi implements HttpHandler {

    public UploadFileApi() {
        System.out.println("UploadFileApi: singleton object");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        System.out.println("Request method: " + method);
        Headers requestHeaders = exchange.getRequestHeaders();
        String contentType = requestHeaders.get("content-type").get(0);
        List<MultipartItem> itemList = new ArrayList<>();
        if (contentType.startsWith("multipart/form-data") && method.equalsIgnoreCase("post")) {
            /*
             * --------------------------7ef8140c649d8c54
             * Content-Disposition: form-data; name="file"; filename="file.img"
             * Content-Type: application/octet-stream
             *
             * [File bytes]
             * */
            byte[] allBytes = exchange.getRequestBody().readAllBytes();
            StringBuilder content = new StringBuilder();
            List<Byte> fileByes = new ArrayList<>();
            MultipartItem multipartItem = new MultipartItem();
            for (byte b : allBytes) {
                content.append((char) b);
                if (content.toString().startsWith("--------") && content.toString().endsWith("\r\n")) {
                    content.setLength(0);
                } else if (content.toString().startsWith("Content-Disposition") && content.toString().endsWith("\r\n")) {
                    for (String p : content.toString().split(";")) {
                        String[] kv = p.split("=");
                        if (kv.length > 1) {
                            if (kv[0].trim().equalsIgnoreCase("name")) multipartItem.setName(kv[1].trim().substring(1).substring(0, kv[1].trim().length() - 2));
                            if (kv[0].trim().equalsIgnoreCase("filename")) multipartItem.setFileName(kv[1].trim().substring(1).substring(0, kv[1].trim().length() - 2));
                        }
                    }
                    content.setLength(0);
                } else if (content.toString().startsWith("Content-Type") && content.toString().endsWith("\r\n")) {
                    content.setLength(0);
                } else if (content.toString().startsWith("\r\n")) {
                    fileByes.add(b);
                    if (content.toString().endsWith("\r\n-----")) {
                        byte[] bytes = new byte[fileByes.size() - 8];
                        for (int i = 1; i < fileByes.size() - 7; i++) {
                            bytes[i - 1] = fileByes.get(i);
                        }

                        multipartItem.setBytes(bytes);
                        itemList.add(multipartItem);
                        multipartItem = new MultipartItem();
                        content.setLength(0);
                        fileByes.clear();
                        content.append("-----");
                    }
                }
            }

            for (MultipartItem item : itemList) {
                saveFile(item);
            }
        }

        HttpUtils.sendResponse(exchange, "".getBytes(), 204);
    }

    private static void saveFile(MultipartItem item) throws IOException {
        FileOutputStream fos = new FileOutputStream("/tmp/" + item.getFileName());
        fos.write(item.getBytes());
        fos.flush();
        fos.close();
    }
}
