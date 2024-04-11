import managers.historyManager.HistoryManager;
import managers.historyManager.InMemoryHistoryManager;
import managers.Managers;
import managers.taskManager.InMemoryTaskManager;
import managers.taskManager.TaskManager;
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
