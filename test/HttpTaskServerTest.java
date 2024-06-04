import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import managers.Managers;
import managers.taskManager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;
import taskserver.HttpTaskServer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class HttpTaskServerTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();


    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    void beforeEach() throws IOException {
        httpTaskServer.start();
    }

    @AfterEach
    void afterEach() {
        httpTaskServer.stop();
    }

    @Test
    void TaskHandle_shouldAddTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<UUID> tasksFromManager = taskManager.taskList();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void TaskHandle_shouldUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание 1",
                TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.of(2024, 1, 1, 0, 0));
        taskManager.addNewTask(task);

        UUID taskID = taskManager.taskList().getFirst();
        task = new Task("Задача 1", "Описание 2",
                TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.of(2024, 1, 1, 0, 0), taskID);

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<UUID> tasksFromManager = taskManager.taskList();

        Task actualTask = taskManager.getTask(taskID).get();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(task, actualTask, "Некорректная задача");

    }

    @Test
    void TaskHandle_shouldDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание 1",
                TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.of(2024, 1, 1, 0, 0));
        taskManager.addNewTask(task);

        UUID taskID = taskManager.taskList().getFirst();


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(String.format("http://localhost:8080/tasks/%s", taskID.toString()));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<UUID> tasksFromManager = taskManager.taskList();

        Assertions.assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void TaskHandle_shouldGetTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание 1",
                TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.of(2024, 1, 1, 0, 0));
        taskManager.addNewTask(task);

        UUID taskID = taskManager.taskList().getFirst();


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(String.format("http://localhost:8080/tasks/%s", taskID.toString()));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<UUID> tasksFromManager = taskManager.taskList();

        Task actualTask = gson.fromJson(response.body(), Task.class);

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(task, actualTask, "Некорректная задача");
    }

    @Test
    void TaskHandle_shouldGetTaskList() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание 1",
                TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.of(2024, 1, 1, 0, 0));
        taskManager.addNewTask(task);

        UUID taskID = taskManager.taskList().getFirst();


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Type listOfUUID = new TypeToken<List<UUID>>() {
        }.getType();
        List<UUID> tasksFromManager = gson.fromJson(response.body(), listOfUUID);

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(taskID, tasksFromManager.getFirst(), "Некорректное ID задачи");
    }

    @Test
    void EpicHandle_shouldAddTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.IN_PROGRESS);
        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<UUID> tasksFromManager = taskManager.epicList();

        Assertions.assertNotNull(tasksFromManager, "Эпик не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    void EpicHandle_shouldUpdateTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.NEW);

        taskManager.addNewEpic(epic);

        UUID taskID = taskManager.epicList().getFirst();
        epic = new Epic("Эпик 1", "Описание 1", TaskStatus.NEW, taskID);

        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<UUID> tasksFromManager = taskManager.epicList();

        Task actualTask = taskManager.getEpic(taskID).get();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(epic, actualTask, "Некорректная задача");

    }

    @Test
    void EpicHandle_shouldDeleteTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.NEW);

        taskManager.addNewEpic(epic);

        UUID taskID = taskManager.epicList().getFirst();


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(String.format("http://localhost:8080/epics/%s", taskID.toString()));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<UUID> tasksFromManager = taskManager.epicList();

        Assertions.assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void EpicHandle_shouldGetTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);

        UUID taskID = taskManager.epicList().getFirst();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(String.format("http://localhost:8080/epics/%s", taskID.toString()));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<UUID> tasksFromManager = taskManager.epicList();

        Epic actualEpic = gson.fromJson(response.body(), Epic.class);

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(epic, actualEpic, "Некорректная задача");
    }

    @Test
    void EpicHandle_shouldGetTaskList() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);

        UUID taskID = taskManager.epicList().getFirst();


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Type listOfUUID = new TypeToken<List<UUID>>() {
        }.getType();
        List<UUID> tasksFromManager = gson.fromJson(response.body(), listOfUUID);

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(taskID, tasksFromManager.getFirst(), "Некорректное ID задачи");
    }

    @Test
    void SubTaskHandle_shouldAddTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.IN_PROGRESS);
        UUID epicID = taskManager.addNewEpic(epic).getTaskID();
        SubTask subTask = new SubTask("Подзадача 1", "Описание 1", TaskStatus.NEW, epicID);

        String taskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<UUID> tasksFromManager = taskManager.subTaskList();

        Assertions.assertNotNull(tasksFromManager, "Подзадача не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
    }

    @Test
    void SubTaskHandle_shouldUpdateTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.IN_PROGRESS);
        UUID epicID = taskManager.addNewEpic(epic).getTaskID();
        SubTask subTask = new SubTask("Подзадача 1", "Описание 1", TaskStatus.NEW, epicID);

        taskManager.addNewSubTask(subTask);

        UUID taskID = taskManager.subTaskList().getFirst();
        subTask = new SubTask("Подзадача 1", "Описание 2", TaskStatus.NEW, epicID, taskID);

        String taskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<UUID> tasksFromManager = taskManager.subTaskList();

        Task actualTask = taskManager.getSubTask(taskID).get();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(subTask, actualTask, "Некорректная задача");

    }

    @Test
    void SubTaskHandle_shouldDeleteTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.IN_PROGRESS);
        UUID epicID = taskManager.addNewEpic(epic).getTaskID();
        SubTask subTask = new SubTask("Подзадача 1", "Описание 1", TaskStatus.NEW, epicID);

        taskManager.addNewSubTask(subTask);
        UUID taskID = taskManager.subTaskList().getFirst();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(String.format("http://localhost:8080/subtasks/%s", taskID.toString()));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<UUID> tasksFromManager = taskManager.subTaskList();

        Assertions.assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void SubTaskHandle_shouldGetTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.IN_PROGRESS);
        UUID epicID = taskManager.addNewEpic(epic).getTaskID();
        SubTask subTask = new SubTask("Подзадача 1", "Описание 1", TaskStatus.NEW, epicID);

        taskManager.addNewSubTask(subTask);
        UUID taskID = taskManager.subTaskList().getFirst();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(String.format("http://localhost:8080/subtasks/%s", taskID.toString()));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<UUID> tasksFromManager = taskManager.epicList();

        SubTask actualSubTask = gson.fromJson(response.body(), SubTask.class);

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(subTask, actualSubTask, "Некорректная задача");
    }

    @Test
    void SubTaskHandle_shouldGetTaskList() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.IN_PROGRESS);
        UUID epicID = taskManager.addNewEpic(epic).getTaskID();
        SubTask subTask = new SubTask("Подзадача 1", "Описание 1", TaskStatus.NEW, epicID);

        taskManager.addNewSubTask(subTask);
        UUID taskID = taskManager.subTaskList().getFirst();


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Type listOfUUID = new TypeToken<List<UUID>>() {
        }.getType();
        List<UUID> tasksFromManager = gson.fromJson(response.body(), listOfUUID);

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(taskID, tasksFromManager.getFirst(), "Некорректное ID задачи");
    }

    @Test
    void HistoryHandle_shouldGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание 1",
                TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.of(2024, 1, 1, 0, 0));
        taskManager.addNewTask(task);

        UUID taskID = taskManager.taskList().getFirst();
        taskManager.getTask(taskID);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Type listOfTask = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksFromManager = gson.fromJson(response.body(), listOfTask);

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(task, tasksFromManager.getFirst(), "Некорректное задача");
    }

    @Test
    void PriorityHandle_shouldGetPrioritizedTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание 1",
                TaskStatus.NEW, Duration.ofMinutes(5),
                LocalDateTime.of(2024, 1, 1, 0, 0));
        taskManager.addNewTask(task);

        UUID taskID = taskManager.taskList().getFirst();


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Type listOfTask = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksFromManager = gson.fromJson(response.body(), listOfTask);

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(task, tasksFromManager.getFirst(), "Некорректное задача");
    }


    @Test
    void TaskHandle_shouldValidateTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание 1",
                TaskStatus.NEW, Duration.ofHours(5),
                LocalDateTime.of(2024, 1, 1, 0, 0));
        taskManager.addNewTask(task);

        UUID taskID = taskManager.taskList().getFirst();
        task = new Task("Задача 1", "Описание 2",
                TaskStatus.NEW, Duration.ofHours(5),
                LocalDateTime.of(2024, 1, 1, 1, 0), taskID);

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode());

        Task task2 = new Task("Задача 2", "Описание 2",
                TaskStatus.NEW, Duration.ofHours(5),
                LocalDateTime.of(2024, 1, 1, 1, 0));

        taskJson = gson.toJson(task2);


        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode());


    }

    @Test
    void SubTaskHandle_shouldValidateTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание 1", TaskStatus.IN_PROGRESS);
        UUID epicID = taskManager.addNewEpic(epic).getTaskID();
        SubTask subTask = new SubTask("Подзадача 1", "Описание 1", TaskStatus.NEW, Duration.ofHours(5),
                LocalDateTime.of(2024, 1, 1, 0, 0), epicID);

        taskManager.addNewSubTask(subTask);

        UUID taskID = taskManager.subTaskList().getFirst();
        subTask = new SubTask("Подзадача 1", "Описание 2", TaskStatus.NEW, Duration.ofHours(5),
                LocalDateTime.of(2024, 1, 1, 1, 0), epicID, taskID);

        String taskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode());
        SubTask  subTask2 = new SubTask("Подзадача 1", "Описание 2", TaskStatus.NEW, Duration.ofHours(5),
                LocalDateTime.of(2024, 1, 1, 1, 0), epicID, taskID);

        taskJson = gson.toJson(subTask2);


        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode());



    }
}
