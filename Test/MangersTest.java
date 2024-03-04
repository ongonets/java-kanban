import Managers.HistoryManager.HistoryManager;
import Managers.HistoryManager.InMemoryHistoryManager;
import Managers.Managers;
import Managers.TaskManager.InMemoryTaskManager;
import Managers.TaskManager.TaskManager;
import Task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MangersTest {

    @Test
    void getDefault_shouldReturnInMemoryTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        Assertions.assertNotNull(taskManager);
        Assertions.assertInstanceOf(InMemoryTaskManager.class, taskManager);
    }

    @Test
    void getDefaultHistory_shouldReturnInMemoryHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(historyManager);
        Assertions.assertInstanceOf(InMemoryHistoryManager.class, historyManager);
    }
}
