package ir.moke.api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ir.moke.db.Person;
import ir.moke.db.PersonDAO;
import ir.moke.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonApi implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(PersonApi.class);
    private static final PersonDAO dao = PersonDAO.instance;

    public PersonApi() {
        System.out.println("PersonSyncApi: singleton object");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        String requestMethod = exchange.getRequestMethod();
        logger.debug("Receive request with method %s".formatted(requestMethod));
        switch (requestMethod) {
            case "GET" -> getPerson(exchange);
            case "POST", "PUT" -> addPerson(exchange);
            case "DELETE" -> deletePerson(exchange);
        }
    }

    public void deletePerson(HttpExchange exchange) {
        logger.info("try to delete person");
        String query = exchange.getRequestURI().getQuery();
        Gson gson = new Gson();
        if (query != null) {
            String key = query.split("=")[0];
            String value = query.split("=")[1];
            if (key.equals("id")) {
                dao.delete(Integer.parseInt(value));
                String peopleListJson = gson.toJson(dao.getAllPersons());
                sendResponse(exchange, peopleListJson.getBytes(), 200);
                logger.info("person with id %s deleted success".formatted(value));
            }
        } else {
            sendResponse(exchange, "please send request parameter".getBytes(), 400);
        }
    }

    public void getPerson(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        if (query != null) {
            Map<String, String> parameters = extractParameters(query);
            if (parameters.get("id") != null) {
                int id = Integer.parseInt(parameters.get("id"));
                logger.info("get person with id %s".formatted(id));
                Person person = dao.getPerson(id);
                if (person != null) {
                    sendResponse(exchange, person.toJson().getBytes(), 200);
                } else {
                    sendResponse(exchange, "Not found".getBytes(), 404);
                }
            } else {
                sendResponse(exchange, "Bad request parameter".getBytes(), 400);
            }
        } else {
            logger.info("get all persons");
            List<Person> allPersons = dao.getAllPersons();
            String peopleListJson = JsonUtils.toJson(allPersons);
            logger.info("count: %s".formatted(allPersons.size()));
            sendResponse(exchange, peopleListJson.getBytes(), 200);
        }

    }

    public void addPerson(HttpExchange exchange) throws IOException {
        logger.info("try to add new person");
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        Person person = JsonUtils.fromJson(new String(bytes), Person.class);
        dao.save(person);

        String response = JsonUtils.toJson(person); //response with last person id
        sendResponse(exchange, response.getBytes(), 200);
        logger.info("New person added with id %s".formatted(person.getId()));
    }

    private void sendResponse(HttpExchange exchange, byte[] response, int statusCode) {
        try {
            exchange.sendResponseHeaders(statusCode, response.length);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(response);
            responseBody.flush();
            responseBody.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
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
