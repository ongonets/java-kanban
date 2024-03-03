package Managers;

import Managers.HistoryManager.HistoryManager;
import Managers.HistoryManager.InMemoryHistoryManager;
import Managers.TaskManager.InMemoryTaskManager;
import Managers.TaskManager.TaskManager;

public class Managers {

    static public TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    static public HistoryManager getDefaultHistory(){
        return  new InMemoryHistoryManager();
    }
}
