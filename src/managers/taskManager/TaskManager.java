package managers.taskManager;

import task.*;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface TaskManager {

    Task addNewTask(Task task);

    Task updateTask(Task task);

    void deleteTask(UUID taskID);

    Task getTask(UUID taskID);

    void deleteAllTask();

    ArrayList<UUID> taskList();

    Epic addNewEpic(Epic epic);

    Epic updateEpic(Epic epic);

    void deleteEpic(UUID epicID);

    void deleteAllEpic();

    Epic getEpic(UUID epicID);

    ArrayList<UUID> epicList();

    SubTask addNewSubTask(SubTask subTask);

    SubTask updateSubTask(SubTask subTask);

    void deleteSubTask(UUID subTaskID);

    SubTask getSubTask(UUID subTaskID);

    void deleteAllSubTask();

    ArrayList<UUID> subTaskList();

    ArrayList<UUID> subTaskListByEpic(UUID epicID);

    List<Task> getHistory();
}
