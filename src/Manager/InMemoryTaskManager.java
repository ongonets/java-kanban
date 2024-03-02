package Manager;

import Task.*;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, SubTask> subTasks;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    @Override
    public Task addNewTask(Task task) {
        task.setTaskID(IDGenerator.generateNewID());
        tasks.put(task.getTaskID(), task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        tasks.put(task.getTaskID(), task);
        return task;
    }

    @Override
    public void deleteTask(int taskID) {
        tasks.remove(taskID);
    }

    @Override
    public Task searchTaskByID(int taskID) {
            return tasks.get(taskID);
    }

    @Override
    public void deleteAllTask() {
        tasks.clear();
    }

    @Override
    public ArrayList<Integer> taskList() {
        return new ArrayList<>(tasks.keySet());
    }

    @Override
    public Epic addNewEpic(Epic epic) {
        epic.setTaskID(IDGenerator.generateNewID());
        epics.put(epic.getTaskID(), epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        epics.put(epic.getTaskID(), epic);
        updateEpicStatus(epic.getTaskID());
        return epic;
    }

    @Override
    public void deleteEpic(int epicID) {
        clearSubTask(epicID);
        epics.remove(epicID);
    }

    @Override
    public void deleteAllEpic() {
        for (Integer epicID : epics.keySet()) {
            clearSubTask(epicID);
        }
        epics.clear();

    }

    public void clearSubTask(int epicID) {
        Epic epic = epics.get(epicID);
        for (Integer subTaskID : epic.getSubTaskID()) {
            subTasks.remove(subTaskID);
        }
    }

    @Override
    public Epic searchEpicByID(int epicID) {
            return epics.get(epicID);
    }

    @Override
    public ArrayList<Integer> epicList() {
        return new ArrayList<>(epics.keySet());
    }

    public Task updateEpicStatus(int epicID) {
        Epic epic = epics.get(epicID);
        int subTaskNew = 0;
        int subTaskDone = 0;
        for (Integer subTaskID : epic.getSubTaskID()) {
            SubTask subTask = searchSubTaskByID(subTaskID);
            if (TaskStatus.NEW.equals(subTask.getStatus())) {
                subTaskNew++;
            }
            if (TaskStatus.DONE.equals(subTask.getStatus())) {
                subTaskDone++;
            }
        }
        if (subTaskNew == epic.getSubTaskID().size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (subTaskDone == epic.getSubTaskID().size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
        return epic;
    }

    public ArrayList<Integer> subTaskListByEpic(int epicID) {
        Epic epic = epics.get(epicID);
        return epic.getSubTaskID();

    }

    @Override
    public SubTask addNewSubTask(SubTask subTask) {
        subTask.setTaskID(IDGenerator.generateNewID());
        subTasks.put(subTask.getTaskID(), subTask);
        Epic epic = searchEpicByID(subTask.getEpicID());
        epic.addSubTask(subTask.getTaskID());
        updateEpic(epic);
        return subTask;

    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getTaskID(), subTask);
        updateEpicStatus(subTask.getEpicID());
        return subTask;

    }

    @Override
    public void deleteSubTask(int subTaskID) {
        SubTask subTask = subTasks.get(subTaskID);
        Epic epic = searchEpicByID(subTask.getEpicID());
        epic.removeSubTask(subTaskID);
        subTasks.remove(subTaskID);
        updateEpic(epic);
    }


    @Override
    public SubTask searchSubTaskByID(int subTaskID) {
            return subTasks.get(subTaskID);
    }

    @Override
    public void deleteAllSubTask() {
        for (SubTask subTask : subTasks.values()) {
            deleteSubTask(subTask.getTaskID());
        }
    }

    @Override
    public ArrayList<Integer> subTaskList() {
        return new ArrayList<>(subTasks.keySet());
    }
}
