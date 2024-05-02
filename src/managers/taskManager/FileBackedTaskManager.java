package managers.taskManager;

import managers.Managers;
import managers.historyManager.HistoryManager;
import managers.taskManager.taskManagerException.ManagerReadException;
import managers.taskManager.taskManagerException.ManagerSaveException;
import task.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");
    File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;


    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = (FileBackedTaskManager) Managers.getFileBacked(file);
        List<String> lines;
        try {
             lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new ManagerReadException("Ошибка чтения файла.",e);
        }
        if (lines.isEmpty()) {
            return taskManager;
        } else {
            lines.removeFirst();
            lines.stream().forEach(line -> taskManager.addTaskToMap(fromString(line)));
        }
        taskManager.updateEpicId();
        return taskManager;
    }

    public void addTaskToMap(Task task) {
        if (task.getClass() == Task.class) {
            tasks.put(task.getTaskID(),task);
        } else if (task.getClass() == Epic.class) {
            epics.put(task.getTaskID(),(Epic) task);
        } else {
            subTasks.put(task.getTaskID(),(SubTask) task);
        }
    }

    public void updateEpicId() {
        subTasks.values().forEach(subTask -> epics.get(subTask.getEpicID()).addSubTask(subTask.getTaskID()));
        epicList().forEach(this::updateEpicStatus);
    }

    public static Task fromString(String value) {
        String[] split = value.split(",");
        TaskType taskType = TaskType.valueOf(split[1]);
        TaskStatus taskStatus = TaskStatus.valueOf(split[3]);
        UUID taskID = UUID.fromString(split[0]);
        LocalDateTime startTime = null;
        Duration duration = null;
        if (!split[5].isEmpty()) {
            startTime = LocalDateTime.parse(split[5],DATE_TIME_FORMATTER);
        }
        if (!split[6].isEmpty()) {
            duration = Duration.ofMinutes(Integer.parseInt(split[6]));
        }
        Task task;
        switch (taskType) {
            case TaskType.TASK:
                task = new Task(split[2], split[4], taskStatus,duration,startTime, taskID);
                break;
            case TaskType.EPIC:
                task = new Epic(split[2], split[4], taskStatus,duration, startTime, taskID);
                break;
            default:
                UUID epicID = UUID.fromString(split[7]);
                task = new SubTask(split[2], split[4], taskStatus, duration, startTime, taskID, epicID);
                break;
        }
        return task;
    }

    public String toString(Task task) {
        String resultLine = task.getTaskID().toString();
        if (task.getClass() == Task.class) {
            resultLine = String.join(",",resultLine,TaskType.TASK.toString());
        } else if (task.getClass() == Epic.class) {
            resultLine = String.join(",",resultLine,TaskType.EPIC.toString());
        } else {
            resultLine = String.join(",",resultLine,TaskType.SUBTASK.toString());
        }
        String startTime = Optional.ofNullable(task.getStartTime())
                .map(localDateTime -> localDateTime.format(DATE_TIME_FORMATTER))
                .orElse("");


        String duration = Optional.ofNullable(task.getDuration())
                .map(taskDuration -> String.valueOf(taskDuration.toMinutes()))
                .orElse("");
        resultLine = String.join(",",resultLine,
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                startTime,
                duration);
        if (task.getClass() == SubTask.class) {
            SubTask subTask = (SubTask) task;
            resultLine = String.join(",",resultLine, subTask.getEpicID().toString());
        } else {
            resultLine = String.join(",",resultLine, "");
        }

        return resultLine;
    }

    public void save() {
        try (FileWriter output = new FileWriter(file)) {
            output.write("id,type,name,status,description,startTime, duration, epic\n");
            for (UUID i : tasks.keySet()) {
                output.write(toString(tasks.get(i)) + "\n");
            }
            for (UUID i : epics.keySet()) {
                output.write(toString(epics.get(i)) + "\n");
            }
            for (UUID i : subTasks.keySet()) {
                output.write(toString(subTasks.get(i)) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ощибка записи файла.", e);
        }

    }

    public File getFile() {
        return file;
    }

    @Override
    public Task addNewTask(Task task)  {
        Task newTask = super.addNewTask(task);
        save();
        return newTask;
    }

    @Override
    public Task updateTask(Task task)  {
        Task newTask = super.updateTask(task);
        save();
        return newTask;
    }

    @Override
    public void deleteTask(UUID taskID) {
        super.deleteTask(taskID);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public Epic addNewEpic(Epic epic) {
        Epic newEpic = super.addNewEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic newEpic = super.updateEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public void deleteEpic(UUID epicID) {
        super.deleteEpic(epicID);
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public SubTask addNewSubTask(SubTask subTask) {
        SubTask newSubTask = super.addNewSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        SubTask newSubTask = super.updateSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public void deleteSubTask(UUID subTaskID) {
        super.deleteSubTask(subTaskID);
        save();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }
}
