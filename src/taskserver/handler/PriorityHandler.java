package taskserver.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.taskManager.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class PriorityHandler extends BaseHandler implements HttpHandler {

    TaskManager taskManager;

    public PriorityHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
            String response = gson.toJson(taskManager.getPrioritizedTasks());
            sendText(exchange, response, 200);
        } else {
            sendCode(exchange,404);
        }
    }

}





