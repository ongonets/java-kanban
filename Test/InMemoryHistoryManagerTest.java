import Managers.HistoryManager.HistoryManager;
import Managers.Managers;
import Managers.TaskManager.TaskManager;
import Task.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    void BeforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add_shouldAddTaskInHistory() {
        Task task = new Task("Задача 1", "Описание 1", TaskStatus.NEW, 0);
        historyManager.add(task);
        ArrayList<Task> list = historyManager.getHistory();
        Task actual = list.get(0);
        Assertions.assertEquals(task, actual);
    }

    @Test
    void add_shouldAddEpicInHistory() {
        Epic epic = new Epic("Задача 1", "Описание 1", TaskStatus.NEW, 0);
        historyManager.add(epic);
        ArrayList<Task> list = historyManager.getHistory();
        Epic actual = (Epic) list.get(0);
        Assertions.assertEquals(epic, actual);
    }

    @Test
    void add_shouldAddSubTaskInHistory() {
        SubTask subTask = new SubTask("Задача 1", "Описание 1", TaskStatus.NEW, 0,0);
        historyManager.add(subTask);
        ArrayList<Task> list = historyManager.getHistory();
        SubTask actual = (SubTask) list.get(0);
        Assertions.assertEquals(subTask, actual);
    }
}
