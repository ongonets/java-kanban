package taskserver.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.taskManager.TaskManager;
import managers.taskManager.taskManagerException.TaskValidateException;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class TaskHandler extends BaseHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET_TASK_LIST:
                handleGetTasksList(exchange);
                break;
            case GET_TASK:
                handleGetTask(exchange);
                break;
            case POST_TASK:
                handlePostTask(exchange);
                break;
            case DELETE_TASK:
                handleDeleteTask(exchange);
                break;
            default:
                sendCode(exchange, 404);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASK_LIST;
            } else return Endpoint.POST_TASK;
        } else if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASK;
            } else return Endpoint.DELETE_TASK;
        }
        return Endpoint.UNKNOWN;
    }



    private void handleGetTasksList(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(taskManager.taskList());
        sendText(httpExchange, response, 200);
    }

    private void handleDeleteTask(HttpExchange httpExchange) throws IOException {
        Optional<UUID> taskIDOpt = getTaskId(httpExchange);
        if (taskIDOpt.isEmpty()) {
            sendCode(httpExchange, 404);
            return;
        }
        taskManager.deleteTask(taskIDOpt.get());
        sendCode(httpExchange, 200);
    }

    private void handleGetTask(HttpExchange httpExchange) throws IOException {
        Optional<UUID> taskIDOpt = getTaskId(httpExchange);
        if (taskIDOpt.isEmpty()) {
            sendCode(httpExchange, 404);
            return;
        }
        UUID taskID = taskIDOpt.get();
        Optional<Task> taskOpt = taskManager.getTask(taskID);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            String response = gson.toJson(task);
            sendText(httpExchange, response, 200);
            return;
        }

        sendCode(httpExchange, 404);
    }

    private void handlePostTask(HttpExchange httpExchange) throws IOException {
        String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body,Task.class);
        if (task.getTaskID() == null) {
            try {
                taskManager.addNewTask(task);
            } catch (TaskValidateException e) {
                sendCode(httpExchange, 406);
                return;
            }
            sendCode(httpExchange, 201);
        } else {
            try {
                taskManager.updateTask(task);
            } catch (TaskValidateException e) {
                sendCode(httpExchange, 406);
                return;
            }
            sendCode(httpExchange, 201);
        }


    }


}
