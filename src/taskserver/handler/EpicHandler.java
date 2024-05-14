package taskserver.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.taskManager.TaskManager;
import managers.taskManager.taskManagerException.TaskValidateException;
import task.Epic;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EpicHandler extends BaseHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET_EPIC_LIST:
                handleGetTasksList(exchange);
                break;
            case GET_EPIC:
                handleGetTask(exchange);
                break;
            case POST_EPIC:
                handlePostTask(exchange);
                break;
            case DELETE_EPIC:
                handleDeleteTask(exchange);
                break;
            case GET_EPICS_SUBTASKS_LIST:
                handleGetSubTasksList(exchange);
                break;
            default:
                sendCode(exchange, 404);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPIC_LIST;
            } else return Endpoint.POST_EPIC;
        } else if (pathParts.length == 3 && pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPIC;
            } else return Endpoint.DELETE_EPIC;
        } else if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")) {
            return Endpoint.GET_EPICS_SUBTASKS_LIST;
        }
        return Endpoint.UNKNOWN;
    }


    private void handleGetTasksList(HttpExchange httpExchange) throws IOException {
        String response = gson.toJson(taskManager.epicList());
        sendText(httpExchange, response, 200);
    }

    private void handleDeleteTask(HttpExchange httpExchange) throws IOException {
        Optional<UUID> taskIDOpt = getTaskId(httpExchange);
        if (taskIDOpt.isEmpty()) {
            sendCode(httpExchange, 404);
            return;
        }
        taskManager.deleteEpic(taskIDOpt.get());

        sendCode(httpExchange, 200);
    }

    private void handleGetTask(HttpExchange httpExchange) throws IOException {
        Optional<UUID> taskIDOpt = getTaskId(httpExchange);
        if (taskIDOpt.isEmpty()) {
            sendCode(httpExchange, 404);
            return;
        }
        UUID taskID = taskIDOpt.get();
        Optional<Task> taskOpt = taskManager.getEpic(taskID);
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
        Epic task = gson.fromJson(body, Epic.class);
        if (task.getTaskID() == null) {
            try {
                taskManager.addNewEpic(task);
            } catch (TaskValidateException e) {
                sendCode(httpExchange, 406);
                return;
            }
            sendCode(httpExchange, 201);
        } else {
            try {
                taskManager.updateEpic(task);
            } catch (TaskValidateException e) {
                sendCode(httpExchange, 406);
                return;
            }
            sendCode(httpExchange, 201);
        }


    }



    private void handleGetSubTasksList(HttpExchange httpExchange) throws IOException {
        Optional<UUID> taskIDOpt = getTaskId(httpExchange);
        if (taskIDOpt.isEmpty()) {
            sendCode(httpExchange, 404);
            return;
        }
        UUID taskID = taskIDOpt.get();
        List<UUID> subTaskList = taskManager.subTaskListByEpic(taskID);

        String response = gson.toJson(subTaskList);
        sendText(httpExchange, response, 200);


        sendCode(httpExchange, 404);
    }
}

