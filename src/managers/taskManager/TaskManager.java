package managers.taskManager;

import task.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TaskManager {

    Task addNewTask(Task task);

    Task updateTask(Task task);

    void deleteTask(UUID taskID);

    Task getTask(UUID taskID);

    void deleteAllTask();

    List<UUID> taskList();

    Task addNewEpic(Epic epic);

    Task updateEpic(Epic epic);

    void deleteEpic(UUID epicID);

    void deleteAllEpic();

    List<UUID> epicList();

    Task getEpic(UUID taskID);

    Task addNewSubTask(SubTask subTask);

    Task updateSubTask(SubTask subTask);

    void deleteSubTask(UUID subTaskID);

    void deleteAllSubTask();

    List<UUID> subTaskList();

    ArrayList<UUID> subTaskListByEpic(UUID epicID);

    Task getSubTask(UUID taskID);

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();

    void addToPriority(Task task);

    void validateTime(Task task);
}
