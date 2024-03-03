package Managers.HistoryManager;

import java.util.ArrayList;
import Task.*;

public interface HistoryManager {

    void add(Task task);

    ArrayList<Task> getHistory();

}
