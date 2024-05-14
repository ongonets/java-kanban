package taskserver.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.taskManager.TaskManager;
import managers.taskManager.taskManagerException.TaskValidateException;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class SubTaskHandler extends BaseHandler implements HttpHandler {
    public SubTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET_SUBTASK_LIST:
                handleGetTasksList(exchange);
                break;
            case GET_SUBTASK:
                handleGetTask(exchange);
                break;
            case POST_SUBTASK:
                handlePostTask(exchange);
                break;
            case DELETE_SUBTASK:
                handleDeleteTask(exchange);
                break;
            default:
                sendCode(exchange, 404);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASK_LIST;
            } else return Endpoint.POST_SUBTASK;
        } else if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASK;
            } else return Endpoint.DELETE_SUBTASK;
        }
        return Endpoint.UNKNOWN;
    }


    private void handleGetTasksList(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(taskManager.subTaskList());
        sendText(httpExchange, response, 200);
    }

    private void handleDeleteTask(HttpExchange httpExchange) throws IOException {
        Optional<UUID> taskIDOpt = getTaskId(httpExchange);
        if (taskIDOpt.isEmpty()) {
            sendCode(httpExchange, 404);
            return;
        }
        taskManager.deleteSubTask(taskIDOpt.get());
        sendCode(httpExchange, 200);
    }

    private void handleGetTask(HttpExchange httpExchange) throws IOException {
        Optional<UUID> taskIDOpt = getTaskId(httpExchange);
        if (taskIDOpt.isEmpty()) {
            sendCode(httpExchange, 404);
            return;
        }
        UUID taskID = taskIDOpt.get();
        Optional<Task> taskOpt = taskManager.getSubTask(taskID);
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
        SubTask task = gson.fromJson(body, SubTask.class);
        if (task.getTaskID() == null) {
            try {
                taskManager.addNewSubTask(task);
            } catch (TaskValidateException e) {
                sendCode(httpExchange, 406);
                return;
            }
            sendCode(httpExchange, 201);
        } else {
            try {
                taskManager.updateSubTask(task);
            } catch (TaskValidateException e) {
                sendCode(httpExchange, 406);
                return;
            }
            sendCode(httpExchange, 201);
        }


    }

}
