package Managers.HistoryManager;

import Task.*;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> history;
    static final int HISTORY_VOLUME = 10;


    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<Task>(history);
    }

    @Override
    public void add(Task task) {
        if (!(history.size() < HISTORY_VOLUME)) {
            history.remove(0);
        }
        history.add(task);
    }
}
