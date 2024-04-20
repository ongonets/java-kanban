package managers.historyManager;

import java.util.List;
import task.*;

public interface HistoryManager {

    void add(Task task);

    void remove(int taskID);

    List<Task> getHistory();

}
