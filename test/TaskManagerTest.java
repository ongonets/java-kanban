import managers.taskManager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.util.List;
import java.util.UUID;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @BeforeEach
    public void init() {
        taskManager = createTaskManager();
    }

    protected abstract T createTaskManager();

    @Test
    void addNewTask_shouldReturnTask(){
        Task expected = new Task("Задача 1","Описание 1", TaskStatus.NEW);
        Task task = new Task("Задача 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewTask(task);
        UUID taskID = task.getTaskID();
        expected.setTaskID(taskID);
        Task actual = taskManager.getTask(taskID);
        Assertions.assertEquals(expected,actual);
    }
    @Test
    void addNewEpic_shouldReturnEpic(){
        Epic expected = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        UUID taskID = epic.getTaskID();
        expected.setTaskID(taskID);
        Epic actual = taskManager.getEpic(taskID);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    void addNewSubTask_shouldReturnSubTask(){
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        UUID epicID = epic.getTaskID();
        SubTask subTask = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID);
        taskManager.addNewSubTask(subTask);
        UUID taskID = subTask.getTaskID();
        SubTask expected = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID);
        expected.setTaskID(taskID);
        SubTask actual = taskManager.getSubTask(taskID);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void addNewTask_shouldGenerateNewID(){
        Task expected = new Task("Задача 1","Описание 1", TaskStatus.NEW);
        Task task = new Task("Задача 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewTask(task);
        UUID taskID = task.getTaskID();
        expected.setTaskID(taskID);
        Task actual = taskManager.getTask(taskID);
        Assertions.assertEquals(expected,actual);
    }
    @Test
    void addNewEpic_shouldGenerateNewID(){
        Epic expected = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        UUID taskID = epic.getTaskID();
        expected.setTaskID(taskID);
        Epic actual = taskManager.getEpic(taskID);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    void addNewSubTask_shouldGenerateNewID(){
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        UUID epicID = epic.getTaskID();
        SubTask expected = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID);
        SubTask subTask = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID);
        taskManager.addNewSubTask(subTask);
        UUID taskID = subTask.getTaskID();
        expected.setTaskID(taskID);
        SubTask actual = taskManager.getSubTask(taskID);
        Assertions.assertEquals(expected, actual);
    }



    @Test
    void epicShouldUpdateStatus() {
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.IN_PROGRESS);
        taskManager.addNewEpic(epic);
        UUID epicID = epic.getTaskID();
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epicID).getStatus());
        SubTask subTask = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID);
        taskManager.addNewSubTask(subTask);
        UUID taskID = subTask.getTaskID();
        Assertions.assertEquals(TaskStatus.NEW, taskManager.getEpic(epicID).getStatus());
        subTask = new SubTask("Подзадача 1","Описание 1", TaskStatus.DONE,epicID, taskID);
        taskManager.updateSubTask(subTask);
        Assertions.assertEquals(TaskStatus.DONE, taskManager.getEpic(epicID).getStatus());
    }

    @Test
    void deleteTask_shouldRemoveTask() {
        Task task = new Task("Задача 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewTask(task);
        taskManager.deleteTask(task.getTaskID());
        List<UUID> taskList = taskManager.taskList();
        Assertions.assertTrue(taskList.isEmpty());
    }

    @Test
    void deleteEpic_shouldRemoveEpic() {
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.IN_PROGRESS);
        taskManager.addNewEpic(epic);
        taskManager.deleteEpic(epic.getTaskID());
        List<UUID> epicList = taskManager.epicList();
        Assertions.assertTrue(epicList.isEmpty());
    }

    @Test
    void deleteSubTask_shouldRemoveSubTask() {
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        UUID epicID = epic.getTaskID();
        SubTask subTask = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID);
        taskManager.addNewSubTask(subTask);
        taskManager.deleteSubTask(subTask.getTaskID());;
        List<UUID> subTaskList = taskManager.subTaskList();
        Assertions.assertTrue(subTaskList.isEmpty());
    }

    @Test
    void deleteEpic_shouldRemoveEpicsSubTask() {
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        UUID epicID = epic.getTaskID();
        SubTask subTask = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID);
        taskManager.addNewSubTask(subTask);
        taskManager.deleteEpic(epic.getTaskID());;
        List<UUID> subTaskList = taskManager.subTaskList();
        Assertions.assertTrue(subTaskList.isEmpty());
    }

    @Test
    void deleteSubTask_shouldUpdateSubTaskListInEpic() {
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        UUID epicID = epic.getTaskID();
        SubTask subTask = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID);
        taskManager.addNewSubTask(subTask);
        taskManager.deleteSubTask(subTask.getTaskID());;
        List<UUID> subTaskList = epic.getSubTaskID();
        Assertions.assertTrue(subTaskList.isEmpty());
    }
}

