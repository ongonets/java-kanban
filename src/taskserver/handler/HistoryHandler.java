package taskserver.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.taskManager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHandler implements HttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            String response = gson.toJson(taskManager.getHistory());
            sendText(exchange, response, 200);
        } else {
            sendCode(exchange,404);
        }
    }
}





