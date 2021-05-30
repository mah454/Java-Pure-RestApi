package ir.moke.api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ir.moke.db.Person;
import ir.moke.db.PersonDAO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class PersonApi implements HttpHandler {

    private static final PersonDAO dao = PersonDAO.instance;

    public PersonApi() {
        System.out.println("PersonSyncApi: singleton object");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        String requestMethod = exchange.getRequestMethod();
        switch (requestMethod) {
            case "GET":
                getPerson(exchange);
                break;
            case "POST":
            case "PUT":
                addPerson(exchange);
                break;
            case "DELETE":
                deletePerson(exchange);
                break;
        }
    }

    public void deletePerson(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        Gson gson = new Gson();
        if (query != null) {
            String key = query.split("=")[0];
            String value = query.split("=")[1];
            if (key.equals("id")) {
                dao.delete(Integer.parseInt(value));
                String peopleListJson = gson.toJson(dao.getAllPersons());
                sendResponse(exchange, peopleListJson.getBytes(), 200);
            }
        } else {
            sendResponse(exchange, "please send request parameter".getBytes(), 400);
        }
    }

    public void getPerson(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        Gson gson = new Gson();
        if (query != null) {
            Map<String, String> parameters = extractParameters(query);
            if (parameters.get("id") != null) {
                int id = Integer.parseInt(parameters.get("id"));
                Person person = dao.getPerson(id);
                if (person != null) {
                    sendResponse(exchange, gson.toJson(person).getBytes(), 200);
                } else {
                    sendResponse(exchange, "Not found".getBytes(), 404);
                }
            } else {
                sendResponse(exchange, "Bad request parameter".getBytes(), 400);
            }
        } else {
            String peopleListJson = gson.toJson(dao.getAllPersons());
            sendResponse(exchange, peopleListJson.getBytes(), 200);
        }

    }

    public void addPerson(HttpExchange exchange) throws IOException {
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        Gson gson = new Gson();
        Person person = gson.fromJson(new String(bytes), Person.class);
        dao.save(person);

        String response = gson.toJson(person); //response with last person id
        sendResponse(exchange, response.getBytes(), 200);
    }

    private void sendResponse(HttpExchange exchange, byte[] response, int statusCode) {
        try {
            exchange.sendResponseHeaders(statusCode, response.length);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(response);
            responseBody.flush();
            responseBody.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> extractParameters(String query) {
        Map<String, String> queryParams = new HashMap<>();
        String[] split = query.split("&");
        for (String part : split) {
            String key = part.split("=")[0];
            String value = part.split("=")[1];
            queryParams.put(key, value);
        }
        return queryParams;
    }
}
