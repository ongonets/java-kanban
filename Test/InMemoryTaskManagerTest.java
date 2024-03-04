import Managers.Managers;
import Managers.TaskManager.IDGenerator;
import Managers.TaskManager.TaskManager;
import Task.Epic;
import Task.SubTask;
import Task.Task;
import Task.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest {
    TaskManager taskManager = Managers.getDefault();

    @BeforeEach
    void BeforeEach(){
        taskManager = Managers.getDefault();

    }


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
        Task expected = new Task("Задача 1","Описание 1", TaskStatus.NEW,0);
        Task task = new Task("Задача 1","Описание 1", TaskStatus.NEW,123);
        taskManager.addNewTask(task);
        int taskID = task.getTaskID();
        expected.setTaskID(taskID);
        Task actual = taskManager.getTask(taskID);
        Assertions.assertEquals(expected,actual);
    }
    @Test
    void addNewEpic_shouldGenerateNewID(){
        Epic expected = new Epic("Эпик 1","Описание 1", TaskStatus.NEW,0);
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.NEW,123);
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
        SubTask subTask = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID,123);
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

}
