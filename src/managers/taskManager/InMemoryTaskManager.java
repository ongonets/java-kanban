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
    HistoryManager historyManager;
    Set<Task> tasksWithPriority;


    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        tasksWithPriority = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        this.historyManager = historyManager;
    }

    @Override
    public Task addNewTask(Task task)  {
        try {
            validateTime(task);
        } catch (RuntimeException e) {
            throw new TaskValidateException("Время задачи пересекается по времени выполнения");
        }
        task.setTaskID(IDGenerator.generateNewID(tasks.keySet()));
        tasks.put(task.getTaskID(), task);
        addToPriority(task);
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
        deleteInPriority(taskID);
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
        tasksWithPriority.clear();
    }

    @Override
    public List<UUID> taskList() {
        return tasks.values().stream()
                .filter(task -> task.getClass() == Task.class)
                .map(Task::getTaskID)
                .collect(Collectors.toList());
    }

    @Override
    public Epic addNewEpic(Epic epic) {
        epic.setTaskID(IDGenerator.generateNewID(tasks.keySet()));
        tasks.put(epic.getTaskID(), epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        tasks.put(epic.getTaskID(), epic);
        updateEpicStatus(epic.getTaskID());
        return epic;
    }

    @Override
    public void deleteEpic(UUID epicID) {
        clearSubTask(epicID);
        tasks.remove(epicID);
        historyManager.remove(epicID);
    }

    @Override
    public void deleteAllEpic() {
        epicList().stream()
                .peek(this::clearSubTask)
                .forEach(this::deleteEpic);
    }

    private void clearSubTask(UUID epicID) {
        Epic epic = (Epic) tasks.get(epicID);
        epic.getSubTaskID().stream()
                .peek(subTaskID -> tasks.remove(subTaskID))
                .peek(this::deleteInPriority)
                .forEach(subTaskID -> historyManager.remove(subTaskID));
    }

    @Override
    public List<UUID> epicList() {
        return tasks.values().stream()
                .filter(task -> task.getClass() == Epic.class)
                .map(Task::getTaskID)
                .collect(Collectors.toList());
    }

    protected void updateEpicStatus(UUID epicID) {
        Epic epic = (Epic) tasks.get(epicID);
        Supplier<Stream<Task>> subTaskStream = () -> epic.getSubTaskID().stream()
                .map(taskID -> tasks.get(taskID));

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
        tasks.put(epic.getTaskID(), epic);
    }

    @Override
    public ArrayList<UUID> subTaskListByEpic(UUID epicID) {
        Epic epic = (Epic) tasks.get(epicID);
        return epic.getSubTaskID();
    }

    @Override
    public SubTask addNewSubTask(SubTask subTask) {
        try {
            validateTime(subTask);
        } catch (RuntimeException e) {
            throw new TaskValidateException("Время задачи пересекается по времени выполнения");
        }
        subTask.setTaskID(IDGenerator.generateNewID(tasks.keySet()));
        tasks.put(subTask.getTaskID(), subTask);
        Epic epic = (Epic) tasks.get(subTask.getEpicID());
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
        tasks.put(subTask.getTaskID(), subTask);
        updateEpicStatus(subTask.getEpicID());
        addToPriority(subTask);
        return subTask;

    }

    @Override
    public void deleteSubTask(UUID subTaskID) {
        SubTask subTask = (SubTask) tasks.get(subTaskID);
        Epic epic = (Epic) tasks.get(subTask.getEpicID());
        epic.removeSubTask(subTaskID);
        deleteInPriority(subTaskID);
        tasks.remove(subTaskID);
        historyManager.remove(subTaskID);
        updateEpic(epic);
    }


    @Override
    public void deleteAllSubTask() {
        subTaskList().forEach(this::deleteSubTask);
    }

    @Override
    public List<UUID> subTaskList() {
        return tasks.values().stream()
                .filter(task -> task.getClass() == Task.class)
                .map(Task::getTaskID)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return tasksWithPriority;
    }

    public void addToPriority(Task task) {
        if (task.getStartTime() != null) {
            tasksWithPriority.remove(task);
            tasksWithPriority.add(task);
        }
    }

    public void deleteInPriority(UUID taskID) {
        tasksWithPriority.remove(tasks.get(taskID));
    }


    public void validateTime(Task validateTask) {
        LocalDateTime startTime = validateTask.getStartTime();
        LocalDateTime endTime = validateTask.getEndTime();
        boolean validate = getPrioritizedTasks().stream()
                .filter(task -> startTime.isAfter(task.getStartTime()) && startTime.isBefore(task.getEndTime()) ||
                        endTime.isAfter(task.getStartTime()) && endTime.isBefore(task.getEndTime()))
                .findFirst()
                .isEmpty();
        if(!validate) throw new RuntimeException();
    }
}
