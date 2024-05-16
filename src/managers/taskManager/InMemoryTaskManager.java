package managers.taskManager;

import managers.historyManager.HistoryManager;
import managers.taskManager.taskManagerException.TaskValidateException;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    protected Map<UUID, Task> tasks;
    protected Map<UUID, Epic> epics;
    protected Map<UUID, SubTask> subTasks;
    HistoryManager historyManager;
    Set<Task> tasksWithPriority;


    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        tasksWithPriority = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        this.historyManager = historyManager;
    }

    @Override
    public Task addNewTask(Task task) {
        try {
            validateTime(task);
            task.setTaskID(IDGenerator.generateNewID(tasks.keySet()));
            tasks.put(task.getTaskID(), task);
            addToPriority(task);
        } catch (RuntimeException e) {
            throw new TaskValidateException("Время задачи пересекается по времени выполнения");
        }

        return task;
    }

    @Override
    public Task updateTask(Task task) {
        try {
            validateTime(task);
        } catch (RuntimeException e) {
            throw new TaskValidateException("Время задачи пересекается по времени выполнения");
        }
        tasks.put(task.getTaskID(), task);
        addToPriority(task);
        return task;
    }

    @Override
    public void deleteTask(UUID taskID) {
        deleteTaskInPriority(taskID);
        tasks.remove(taskID);
        historyManager.remove(taskID);
    }

    @Override
    public Optional<Task> getTask(UUID taskID) {
        Optional<Task> taskOpt = Optional.ofNullable(tasks.get(taskID));
        taskOpt.ifPresent(task -> historyManager.add(task));
        return taskOpt;
    }

    @Override
    public void deleteAllTask() {
        tasks.keySet().forEach(this::deleteTaskInPriority);
        tasks.clear();
    }

    @Override
    public List<UUID> taskList() {
        return tasks.values().stream()
                .map(Task::getTaskID)
                .collect(Collectors.toList());
    }

    @Override
    public Epic addNewEpic(Epic epic) {
        epic.setTaskID(IDGenerator.generateNewID(epics.keySet()));
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
        epicList().stream()
                .peek(this::clearSubTask)
                .forEach(this::deleteEpic);
    }

    private void clearSubTask(UUID epicID) {
        Epic epic = epics.get(epicID);
        epic.getSubTaskID().stream()
                .peek(subTaskID -> subTasks.remove(subTaskID))
                .peek(this::deleteSubTaskInPriority)
                .forEach(subTaskID -> historyManager.remove(subTaskID));
    }

    @Override
    public List<UUID> epicList() {
        return epics.values().stream()
                .map(Task::getTaskID)
                .collect(Collectors.toList());
    }

    protected void updateEpicStatus(UUID epicID) {
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
    }

    @Override
    public List<UUID> subTaskListByEpic(UUID epicID) {
        List<UUID> subTaskList = new ArrayList<>();
        Optional<Epic> epicOpt = Optional.ofNullable(epics.get(epicID));
        if (epicOpt.isPresent()) {
            subTaskList = epicOpt.get().getSubTaskID();
        }
        return subTaskList;
    }

    @Override
    public Optional<Task> getEpic(UUID taskID) {
        Optional<Task> taskOpt = Optional.ofNullable(epics.get(taskID));
        taskOpt.ifPresent(task -> historyManager.add(task));
        return taskOpt;
    }

    @Override
    public SubTask addNewSubTask(SubTask subTask) {
        try {
            validateTime(subTask);
        } catch (RuntimeException e) {
            throw new TaskValidateException("Время задачи пересекается по времени выполнения");
        }
        subTask.setTaskID(IDGenerator.generateNewID(subTasks.keySet()));
        subTasks.put(subTask.getTaskID(), subTask);
        Epic epic = epics.get(subTask.getEpicID());
        epic.addSubTask(subTask.getTaskID());
        updateEpic(epic);
        addToPriority(subTask);
        return subTask;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        try {
            validateTime(subTask);
        } catch (RuntimeException e) {
            throw new TaskValidateException("Время задачи пересекается по времени выполнения");
        }
        subTasks.put(subTask.getTaskID(), subTask);
        updateEpicStatus(subTask.getEpicID());
        addToPriority(subTask);
        return subTask;

    }

    @Override
    public void deleteSubTask(UUID subTaskID) {
        SubTask subTask = subTasks.get(subTaskID);
        Epic epic = epics.get(subTask.getEpicID());
        epic.removeSubTask(subTaskID);
        deleteSubTaskInPriority(subTaskID);
        subTasks.remove(subTaskID);
        historyManager.remove(subTaskID);
        updateEpic(epic);
    }


    @Override
    public void deleteAllSubTask() {
        subTaskList().forEach(this::deleteSubTask);
    }

    @Override
    public List<UUID> subTaskList() {
        return subTasks.values().stream()
                .map(Task::getTaskID)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Task> getSubTask(UUID taskID) {
        Optional<Task> taskOpt = Optional.ofNullable(subTasks.get(taskID));
        taskOpt.ifPresent(task -> historyManager.add(task));
        return taskOpt;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return tasksWithPriority;
    }

    @Override
    public void addToPriority(Task task) {
        if (task.getStartTime() != null) {
            tasksWithPriority.remove(task);
            tasksWithPriority.add(task);
        }
    }

    public void deleteTaskInPriority(UUID taskID) {
        if (getPrioritizedTasks().stream().anyMatch(task -> task.getTaskID().equals(taskID))) {
            tasksWithPriority.remove(tasks.get(taskID));
        }
    }

    public void deleteSubTaskInPriority(UUID taskID) {
        if (getPrioritizedTasks().stream().anyMatch(task -> task.getTaskID().equals(taskID))) {
            tasksWithPriority.remove(tasks.get(taskID));
        }
    }

    @Override
    public void validateTime(Task validateTask) {
        if (validateTask.getStartTime() != null) {
            LocalDateTime startTime = validateTask.getStartTime();
            LocalDateTime endTime = validateTask.getEndTime();
            boolean validate = getPrioritizedTasks().stream()
                    .filter(task -> startTime.isAfter(task.getStartTime()) && startTime.isBefore(task.getEndTime()) ||
                            endTime.isAfter(task.getStartTime()) && endTime.isBefore(task.getEndTime()))
                    .findFirst()
                    .isEmpty();
            if (!validate) throw new RuntimeException();
        }
    }
}
