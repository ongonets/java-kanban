package managers.taskManager;

import managers.historyManager.HistoryManager;
import task.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
        loadFromFile(file);

    }

    public void loadFromFile(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty()) {
                return;
            }
            lines.remove(0);
            for (String line : lines) {
                Task task = fromString(line);
                if (task.getClass() == Task.class) {
                    tasks.put(task.getTaskID(), task);
                } else if (task.getClass() == Epic.class) {
                    epics.put(task.getTaskID(), (Epic) task);
                } else {
                    SubTask subTask = (SubTask) task;
                    subTasks.put(task.getTaskID(), subTask);
                    Epic epic = epics.get(subTask.getEpicID());
                    epic.addSubTask(task.getTaskID());
                    updateEpic(epic);
                }
            }
        } catch (IOException e) {
            throw new ManagerReadException("Ошибка чтения файла.",e);
        }
    }


    public Task fromString(String value) {
        String[] split = value.split(",");
        TaskType taskType = TaskType.valueOf(split[1]);
        TaskStatus taskStatus = TaskStatus.valueOf(split[3]);
        int taskID = Integer.parseInt(split[0]);
        Task task;
        switch (taskType) {
            case TaskType.TASK:
                task = new Task(split[2], split[4], taskStatus, taskID);
                break;
            case TaskType.EPIC:
                task = new Epic(split[2], split[4], taskStatus, taskID);
                break;
            default:
                int epicID = Integer.parseInt(split[5]);
                task = new SubTask(split[2], split[4], taskStatus, epicID, taskID);
                break;
        }
        return task;
    }

    public String toString(Task task) {
        String resultLine;
        if (task.getClass() == Task.class) {
            resultLine = String.join(",",
                    Integer.toString(task.getTaskID()),
                    TaskType.TASK.toString(),
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    "");
        } else if (task.getClass() == Epic.class) {
            resultLine = String.join(",",
                    Integer.toString(task.getTaskID()),
                    TaskType.EPIC.toString(),
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    "");
        } else {
            SubTask subTask = (SubTask) task;
            resultLine = String.join(",",
                    Integer.toString(task.getTaskID()),
                    TaskType.SUBTASK.toString(),
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    Integer.toString(subTask.getEpicID())
            );
        }
        return resultLine;
    }

    public void save() {
        try (FileWriter output = new FileWriter(file)) {
            output.write("id,type,name,status,description,epic\n");
            for (Integer i : tasks.keySet()) {
                output.write(toString(tasks.get(i)) + "\n");
            }
            for (Integer i : epics.keySet()) {
                output.write(toString(epics.get(i)) + "\n");
            }
            for (Integer i : subTasks.keySet()) {
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
    public void deleteTask(int taskID) {
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
    public void deleteEpic(int epicID) {
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
    public void deleteSubTask(int subTaskID) {
        super.deleteSubTask(subTaskID);
        save();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }
}
