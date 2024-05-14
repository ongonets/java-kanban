package taskserver.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.taskManager.TaskManager;
import taskserver.HttpTaskServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class BaseHandler  {
    TaskManager taskManager;
    Gson gson;

    public BaseHandler(TaskManager taskManager) {
        gson = HttpTaskServer.getGson();
        this.taskManager = taskManager;
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendCode(HttpExchange exchange, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, 0);
        exchange.close();
    }

    protected Optional<UUID> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(UUID.fromString(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
