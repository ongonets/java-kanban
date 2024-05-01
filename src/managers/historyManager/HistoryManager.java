package managers.historyManager;

import java.util.List;
import java.util.UUID;

import task.*;

public interface HistoryManager {

    void add(Task task);

    void remove(UUID taskID);

    List<Task> getHistory();

}
