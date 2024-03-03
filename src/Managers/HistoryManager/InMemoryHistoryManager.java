package Managers.HistoryManager;

import Task.*;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> history;


    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void add(Task task) {
        if (history.size() <= 10) {
            history.add(task);
        } else {
            history.remove(0);
            history.add(task);

        }

    }
}
