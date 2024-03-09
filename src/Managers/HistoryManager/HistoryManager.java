package Managers.HistoryManager;

import java.util.List;
import Task.*;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

}
