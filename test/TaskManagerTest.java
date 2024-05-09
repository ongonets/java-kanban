import managers.taskManager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
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
        Task actual = taskManager.getEpic(taskID);
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
        SubTask actual = (SubTask) taskManager.getSubTask(taskID);
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
        Epic actual = (Epic) taskManager.getEpic(taskID);
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
        SubTask actual = (SubTask) taskManager.getSubTask(taskID);
        Assertions.assertEquals(expected, actual);
    }



    @Test
    void epicShouldUpdateStatus() {
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.IN_PROGRESS);
        taskManager.addNewEpic(epic);
        UUID epicID = epic.getTaskID();
        SubTask subTask1 = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW,epicID);
        SubTask subTask2 = new SubTask("Подзадача 2","Описание 2", TaskStatus.DONE,epicID);
        taskManager.addNewSubTask(subTask1);
        Assertions.assertEquals(TaskStatus.NEW, taskManager.getEpic(epicID).getStatus());
        taskManager.addNewSubTask(subTask2);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epicID).getStatus());
        subTask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask1);
        Assertions.assertEquals(TaskStatus.DONE, taskManager.getEpic(epicID).getStatus());
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epicID).getStatus());
    }

    @Test
    void epicShouldUpdateTime() {
        Epic epic = new Epic("Эпик 1","Описание 1", TaskStatus.IN_PROGRESS);
        taskManager.addNewEpic(epic);
        UUID epicID = epic.getTaskID();
        SubTask subTask1 = new SubTask("Подзадача 1","Описание 1", TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2024,1,1,0,0), epicID);
        SubTask subTask2 = new SubTask("Подзадача 2","Описание 2", TaskStatus.DONE,Duration.ofHours(1),
                LocalDateTime.of(2024,1,1,2,0),epicID);
        taskManager.addNewSubTask(subTask1);
        Assertions.assertEquals( LocalDateTime.of(2024,1,1,0,0),
                taskManager.getEpic(epicID).getStartTime());
        Assertions.assertEquals( LocalDateTime.of(2024,1,1,1,0),
                taskManager.getEpic(epicID).getEndTime());
        taskManager.addNewSubTask(subTask2);
        Assertions.assertEquals( LocalDateTime.of(2024,1,1,0,0),
                taskManager.getEpic(epicID).getStartTime());
        Assertions.assertEquals( LocalDateTime.of(2024,1,1,3,0),
                taskManager.getEpic(epicID).getEndTime());
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
        taskManager.deleteSubTask(subTask.getTaskID());
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
        taskManager.deleteEpic(epic.getTaskID());
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
        taskManager.deleteSubTask(subTask.getTaskID());
        List<UUID> subTaskList = epic.getSubTaskID();
        Assertions.assertTrue(subTaskList.isEmpty());
    }

    @Test
    void validateTime_shouldValidateTaskByTime() {
        Task task1 = new Task("Задача 1","Описание 1", TaskStatus.NEW, Duration.ofHours(2),
                LocalDateTime.of(2024,1,2,0,0));
        Task task2 = new Task("Задача 2","Описание 2", TaskStatus.DONE,Duration.ofHours(1),
                LocalDateTime.of(2024,1,2,1,0));
        taskManager.addNewTask(task1);
        Assertions.assertThrows(RuntimeException.class, () -> taskManager.validateTime(task2));
        task2.setStartTime(LocalDateTime.of(2024,1,2,3,0));
        Assertions.assertDoesNotThrow(() -> taskManager.validateTime(task2));
        task2.setStartTime(LocalDateTime.of(2024,1,1,0,0));
        task2.setDuration(Duration.ofHours(25));
        Assertions.assertThrows(RuntimeException.class, () -> taskManager.validateTime(task2));
    }
}

