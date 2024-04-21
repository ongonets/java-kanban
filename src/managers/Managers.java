package managers;

import managers.historyManager.HistoryManager;
import managers.historyManager.InMemoryHistoryManager;
import managers.taskManager.FileBackedTaskManager;
import managers.taskManager.InMemoryTaskManager;

import managers.taskManager.TaskManager;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return  new InMemoryHistoryManager();
    }

    public static TaskManager getFileBacked() {
        return new FileBackedTaskManager(getDefaultHistory(), Paths.get("resources/date.csv").toFile());
    }
}
