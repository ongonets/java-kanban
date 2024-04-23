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
import java.util.List;
import java.util.UUID;

public class FileBackedTaskManager extends InMemoryTaskManager {
    File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;


    }

    public static TaskManager loadFromFile(File file) {
        TaskManager taskManager = Managers.getFileBacked(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty()) {
                return taskManager;
            } else {
                lines.remove(0);
                for (String line : lines) {
                    if (!line.isEmpty()) {
                        Task task = fromString(line);
                        if (task.getClass() == Task.class) {
                            taskManager.updateTask(task);
                        } else if (task.getClass() == Epic.class) {
                            taskManager.updateEpic((Epic) task);
                        } else {
                            taskManager.updateSubTask((SubTask) task);

                        }
                    } else {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerReadException("Ошибка чтения файла.",e);
        }
        return taskManager;
    }


    public static Task fromString(String value) {
        String[] split = value.split(",");
        TaskType taskType = TaskType.valueOf(split[1]);
        TaskStatus taskStatus = TaskStatus.valueOf(split[3]);
        UUID taskID = UUID.fromString(split[0]);
        Task task;
        switch (taskType) {
            case TaskType.TASK:
                task = new Task(split[2], split[4], taskStatus, taskID);
                break;
            case TaskType.EPIC:
                task = new Epic(split[2], split[4], taskStatus, taskID);
                break;
            default:
                UUID epicID = UUID.fromString(split[5]);
                task = new SubTask(split[2], split[4], taskStatus, epicID, taskID);
                break;
        }
        return task;
    }

    public String toString(Task task) {
        String resultLine;
        if (task.getClass() == Task.class) {
            resultLine = String.join(",",
                    task.getTaskID().toString(),
                    TaskType.TASK.toString(),
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    "");
        } else if (task.getClass() == Epic.class) {
            resultLine = String.join(",",
                    task.getTaskID().toString(),
                    TaskType.EPIC.toString(),
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    "");
        } else {
            SubTask subTask = (SubTask) task;
            resultLine = String.join(",",
                    task.getTaskID().toString(),
                    TaskType.SUBTASK.toString(),
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    subTask.getEpicID().toString()
            );
        }
        return resultLine;
    }

    public void save() {
        try (FileWriter output = new FileWriter(file)) {
            output.write("id,type,name,status,description,epic\n");
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
