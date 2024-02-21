import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, SubTask> subTasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    public Task addNewTask(Task task) {
        task.setTaskID(IDGenerator.generateNewID());
        tasks.put(task.getTaskID(), task);
        return task;
    }

    public Task updateTask(Task task) {
        tasks.put(task.getTaskID(), task);
        return task;
    }

    public void deleteTask(int taskID) {
        tasks.remove(taskID);
    }

    public Task searchTaskByID(int taskID) {
        if (tasks.containsKey(taskID)) {
            return tasks.get(taskID);
        } else {
            return null;
        }
    }

    public void deleteAllTask() {
        tasks.clear();
    }

    public ArrayList<Integer> taskList() {
        return new ArrayList<>(tasks.keySet());
    }

    public Epic addNewEpic(Epic epic) {
        epic.setTaskID(IDGenerator.generateNewID());
        epics.put(epic.getTaskID(), epic);
        return epic;
    }

    public Epic updateEpic(Epic epic) {
        epics.put(epic.getTaskID(), epic);
        updateEpicStatus(epic.getTaskID());
        return epic;
    }

    public void deleteEpic(int epicID) {
        clearSubTask(epicID);
        epics.remove(epicID);
    }

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

    public Epic searchEpicByID(int epicID) {
        if (epics.containsKey(epicID)) {
            return epics.get(epicID);
        } else {
            return null;
        }
    }

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

    public SubTask addNewSubTask(SubTask subTask) {
        subTask.setTaskID(IDGenerator.generateNewID());
        subTasks.put(subTask.getTaskID(), subTask);
        Epic epic = searchEpicByID(subTask.getEpicID());
        ArrayList<Integer> listID = epic.getSubTaskID();
        listID.add(subTask.getTaskID());
        epic.setSubTaskID(listID);
        updateEpic(epic);
        return subTask;

    }

    public SubTask updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getTaskID(), subTask);
        updateEpicStatus(subTask.getEpicID());
        return subTask;

    }

    public void deleteSubTask(int subTaskID) {
        SubTask subTask = subTasks.get(subTaskID);
        Epic epic = searchEpicByID(subTask.getEpicID());
        ArrayList<Integer> listID = epic.getSubTaskID();
        listID.remove((Integer) subTaskID);
        epic.setSubTaskID(listID);
        subTasks.remove(subTaskID);
        updateEpic(epic);
    }


    public SubTask searchSubTaskByID(int subTaskID) {
        if (subTasks.containsKey(subTaskID)) {
            return subTasks.get(subTaskID);
        } else {
            return null;
        }
    }

    public void deleteAllSubTask() {
        for (SubTask subTask : subTasks.values()) {
            deleteSubTask(subTask.getTaskID());
        }
    }

    public ArrayList<Integer> subTaskList() {
        return new ArrayList<>(subTasks.keySet());
    }
}
