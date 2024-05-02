package managers.taskManager;

import managers.historyManager.HistoryManager;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    protected Map<UUID, Task> tasks;
    protected Map<UUID, Epic> epics;
    protected Map<UUID, SubTask> subTasks;
    HistoryManager historyManager;


    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    @Override
    public Task addNewTask(Task task) {
        task.setTaskID(IDGenerator.generateNewID(taskList()));
        tasks.put(task.getTaskID(), task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        tasks.put(task.getTaskID(), task);
        return task;
    }

    @Override
    public void deleteTask(UUID taskID) {
        tasks.remove(taskID);
        historyManager.remove(taskID);
    }

    @Override
    public Task getTask(UUID taskID) {
        Task task = tasks.get(taskID);
        historyManager.add(task);
        return task;
    }

    @Override
    public void deleteAllTask() {
        tasks.clear();
    }

    @Override
    public ArrayList<UUID> taskList() {
        return new ArrayList<>(tasks.keySet());
    }

    @Override
    public Epic addNewEpic(Epic epic) {
        epic.setTaskID(IDGenerator.generateNewID(epicList()));
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
    public void deleteEpic(UUID epicID) {
        clearSubTask(epicID);
        epics.remove(epicID);
        historyManager.remove(epicID);
    }

    @Override
    public void deleteAllEpic() {
        for (UUID epicID : epics.keySet()) {
            clearSubTask(epicID);
        }
        epics.clear();

    }

    private void clearSubTask(UUID epicID) {
        Epic epic = epics.get(epicID);
        for (UUID subTaskID : epic.getSubTaskID()) {
            subTasks.remove(subTaskID);
            historyManager.remove(subTaskID);
        }
    }

    @Override
    public Epic getEpic(UUID epicID) {
        Epic epic = epics.get(epicID);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public ArrayList<UUID> epicList() {
        return new ArrayList<>(epics.keySet());
    }

    protected Task updateEpicStatus(UUID epicID) {
        Epic epic = epics.get(epicID);
        Supplier<Stream<Task>> subTaskStream = () -> epic.getSubTaskID().stream()
                .map(taskID -> subTasks.get(taskID));

        Optional<LocalDateTime> endTime = subTaskStream.get()
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);
        Optional<LocalDateTime> startTime = subTaskStream.get()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);
        if (startTime.isPresent() && endTime.isPresent()) {
            epic.setEndTime(endTime.get());
            epic.setStartTime(startTime.get());
            epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime()));
        }

        long subTaskNew = subTaskStream.get()
                .map(Task::getStatus)
                .filter(taskStatus -> taskStatus.equals(TaskStatus.NEW))
                .count();
        long subTaskDone = subTaskStream.get()
                .map(Task::getStatus)
                .filter(taskStatus -> taskStatus.equals(TaskStatus.DONE))
                .count();
        if (subTaskNew == epic.getSubTaskID().size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (subTaskDone == epic.getSubTaskID().size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
        epics.put(epic.getTaskID(), epic);
        return epic;
    }

    @Override
    public ArrayList<UUID> subTaskListByEpic(UUID epicID) {
        Epic epic = epics.get(epicID);
        return epic.getSubTaskID();

    }

    @Override
    public SubTask addNewSubTask(SubTask subTask) {
        subTask.setTaskID(IDGenerator.generateNewID(subTaskList()));
        subTasks.put(subTask.getTaskID(), subTask);
        Epic epic = epics.get(subTask.getEpicID());
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
    public void deleteSubTask(UUID subTaskID) {
        SubTask subTask = subTasks.get(subTaskID);
        Epic epic = epics.get(subTask.getEpicID());
        epic.removeSubTask(subTaskID);
        subTasks.remove(subTaskID);
        historyManager.remove(subTaskID);
        updateEpic(epic);
    }


    @Override
    public SubTask getSubTask(UUID subTaskID) {
        SubTask subTask = subTasks.get(subTaskID);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public void deleteAllSubTask() {
        for (SubTask subTask : subTasks.values()) {
            deleteSubTask(subTask.getTaskID());
            historyManager.remove(subTask.getTaskID());
        }
    }

    @Override
    public ArrayList<UUID> subTaskList() {
        return new ArrayList<>(subTasks.keySet());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


}
