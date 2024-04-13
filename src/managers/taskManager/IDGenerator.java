package managers.taskManager;

public class IDGenerator {

    private static int sequence = 0;

    static int generateNewID() {
        return sequence++;
    }
}
