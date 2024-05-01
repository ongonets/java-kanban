import managers.historyManager.HistoryManager;
import managers.Managers;
import task.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

public class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    void BeforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add_shouldAddTaskInHistory() {
        Task task = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        historyManager.add(task);
        List<Task> list = historyManager.getHistory();
        Task actual = list.get(0);
        Assertions.assertEquals(task, actual);
    }

    @Test
    void add_shouldAddEpicInHistory() {
        Epic epic = new Epic("Задача 1", "Описание 1", TaskStatus.NEW);
        historyManager.add(epic);
        List<Task> list = historyManager.getHistory();
        Epic actual = (Epic) list.get(0);
        Assertions.assertEquals(epic, actual);
    }

    @Test
    void add_shouldAddSubTaskInHistory() {
        SubTask subTask = new SubTask("Задача 1", "Описание 1", TaskStatus.NEW);
        historyManager.add(subTask);
        List<Task> list = historyManager.getHistory();
        SubTask actual = (SubTask) list.get(0);
        Assertions.assertEquals(subTask, actual);
    }
    @Test
    void remove_shouldRemoveTaskInHistory() {
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW, UUID.fromString("39eac2c5-23b7-495c-9441-ca266271a436"));
        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW, UUID.fromString("299d662d-e867-40c7-9e99-04967368dba2"));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(UUID.fromString("39eac2c5-23b7-495c-9441-ca266271a436"));
        List<Task> list = historyManager.getHistory();
        Task actual = list.get(0);
        Assertions.assertEquals(task2, actual);
    }
    @Test
    void add_shouldRemoveDuplicates() {
        Task task = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        historyManager.add(task);
        historyManager.add(task);
        List<Task> list = historyManager.getHistory();
        System.out.println(list.size());
       Assertions.assertEquals(1, list.size());
    }
}
