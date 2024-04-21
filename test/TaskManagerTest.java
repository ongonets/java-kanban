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
        int taskID = task.getTaskID();
        expected.setTaskID(taskID);
        Task actual = taskManager.getTask(taskID);
        Assertions.assertEquals(expected,actual);
    }
    @Test
    void addNewEpic_shouldReturnEpic(){
        Epic expected = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        int taskID = epic.getTaskID();
        expected.setTaskID(taskID);
        Epic actual = taskManager.getEpic(taskID);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    void addNewSubTask_shouldReturnSubTask(){
        SubTask expected = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,0);
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        int epicID = epic.getTaskID();
        SubTask subTask = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID);
        taskManager.addNewSubTask(subTask);
        int taskID = subTask.getTaskID();
        expected.setEpicID(epicID);
        expected.setTaskID(taskID);
        SubTask actual = taskManager.getSubTask(taskID);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void addNewTask_shouldGenerateNewID(){
        Task expected = new Task("Задача 1","Описание 1", TaskStatus.NEW);
        Task task = new Task("Задача 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewTask(task);
        int taskID = task.getTaskID();
        expected.setTaskID(taskID);
        Task actual = taskManager.getTask(taskID);
        Assertions.assertEquals(expected,actual);
    }
    @Test
    void addNewEpic_shouldGenerateNewID(){
        Epic expected = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        int taskID = epic.getTaskID();
        expected.setTaskID(taskID);
        Epic actual = taskManager.getEpic(taskID);
        Assertions.assertEquals(expected,actual);
    }

    @Test
    void addNewSubTask_shouldGenerateNewID(){
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        int epicID = epic.getTaskID();
        SubTask expected = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID,1);
        SubTask subTask = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID);
        taskManager.addNewSubTask(subTask);
        int taskID = subTask.getTaskID();
        expected.setTaskID(taskID);
        SubTask actual = taskManager.getSubTask(taskID);
        Assertions.assertEquals(expected, actual);
    }



    @Test
    void epicShouldUpdateStatus() {
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.IN_PROGRESS);
        taskManager.addNewEpic(epic);
        int epicID = epic.getTaskID();
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epicID).getStatus());
        SubTask subTask = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID);
        taskManager.addNewSubTask(subTask);
        int taskID = subTask.getTaskID();
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
        List<Integer> taskList = taskManager.taskList();
        Assertions.assertTrue(taskList.isEmpty());
    }

    @Test
    void deleteEpic_shouldRemoveEpic() {
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.IN_PROGRESS);
        taskManager.addNewEpic(epic);
        taskManager.deleteEpic(epic.getTaskID());
        List<Integer> epicList = taskManager.epicList();
        Assertions.assertTrue(epicList.isEmpty());
    }

    @Test
    void deleteSubTask_shouldRemoveSubTask() {
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        int epicID = epic.getTaskID();
        SubTask subTask = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID);
        taskManager.addNewSubTask(subTask);
        taskManager.deleteSubTask(subTask.getTaskID());;
        List<Integer> subTaskList = taskManager.subTaskList();
        Assertions.assertTrue(subTaskList.isEmpty());
    }

    @Test
    void deleteEpic_shouldRemoveEpicsSubTask() {
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        int epicID = epic.getTaskID();
        SubTask subTask = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID);
        taskManager.addNewSubTask(subTask);
        taskManager.deleteEpic(epic.getTaskID());;
        List<Integer> subTaskList = taskManager.subTaskList();
        Assertions.assertTrue(subTaskList.isEmpty());
    }

    @Test
    void deleteSubTask_shouldUpdateSubTaskListInEpic() {
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW);
        taskManager.addNewEpic(epic);
        int epicID = epic.getTaskID();
        SubTask subTask = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID);
        taskManager.addNewSubTask(subTask);
        taskManager.deleteSubTask(subTask.getTaskID());;
        List<Integer> subTaskList = epic.getSubTaskID();
        Assertions.assertTrue(subTaskList.isEmpty());
    }
}

