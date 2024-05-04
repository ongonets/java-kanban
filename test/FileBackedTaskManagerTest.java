import managers.Managers;
import managers.taskManager.FileBackedTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager>{
    @Override
    protected FileBackedTaskManager createTaskManager() {
        File tempFile;
        try {
            tempFile = File.createTempFile("date", ".tmp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (FileBackedTaskManager) Managers.getFileBacked(tempFile);
    }

    @Test
    void save_shouldWriteTaskInFile() {
        Task task = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        taskManager.addNewTask(task);
        Task actual;
        try{
            List<String> lines = Files.readAllLines(taskManager.getFile().toPath());
            actual = taskManager.fromString(lines.get(1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(task, actual);
    }
    @Test
    void save_shouldWriteEpicInFile() {
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        Task actual;
        try{
            List<String> lines = Files.readAllLines(taskManager.getFile().toPath());
            System.out.println(lines.get(1));
            actual = FileBackedTaskManager.fromString(lines.get(1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(epic,  actual);
    }

    @Test
    void save_shouldWriteSubTaskInFile() {
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        UUID epicID = epic.getTaskID();
        SubTask subTask = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID);
        taskManager.addNewSubTask(subTask);
        Task actual;
        try{
            List<String> lines = Files.readAllLines(taskManager.getFile().toPath());
            actual =  FileBackedTaskManager.fromString(lines.get(1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(subTask,  actual);
    }

    @Test
    void loadFromFile_shouldrestoreTaskManager() {
        taskManager.addNewTask(new Task("Задача 1", "Описание 1", TaskStatus.NEW));
        taskManager.addNewTask(new Task("Задача 2", "Описание 2", TaskStatus.NEW));
        taskManager.addNewTask(new Task("Задача 3", "Описание 3", TaskStatus.NEW));
        List<UUID> taskList = taskManager.taskList();
        Set<Task> taskWithPriority = taskManager.getPrioritizedTasks();
        String lines;
        try {
            lines = Files.readString(taskManager.getFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        taskManager.deleteAllTask();

        try (FileWriter output = new FileWriter(taskManager.getFile())) {
            output.write(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        taskManager =  (FileBackedTaskManager) FileBackedTaskManager.loadFromFile(taskManager.getFile());
        List<UUID> actualTaskList = taskManager.taskList();
        Set<Task> ActualTaskWithPriority = taskManager.getPrioritizedTasks();
        Assertions.assertEquals(taskList,actualTaskList);
        Assertions.assertEquals(taskWithPriority,ActualTaskWithPriority);
    }
}
