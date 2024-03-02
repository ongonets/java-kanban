package Manager;

import Task.*;


import java.util.ArrayList;

public interface TaskManager {

    Task addNewTask(Task task);

    Task updateTask(Task task);

    void deleteTask(int taskID);

    Task searchTaskByID(int taskID);

    void deleteAllTask();

    ArrayList<Integer> taskList();

    Epic addNewEpic(Epic epic);

    Epic updateEpic(Epic epic);

    void deleteEpic(int epicID);

    void deleteAllEpic();

    Epic searchEpicByID(int epicID);

    ArrayList<Integer> epicList();

    SubTask addNewSubTask(SubTask subTask);

    SubTask updateSubTask(SubTask subTask);

    void deleteSubTask(int subTaskID);

    SubTask searchSubTaskByID(int subTaskID);

    void deleteAllSubTask();

    ArrayList<Integer> subTaskList();
}
