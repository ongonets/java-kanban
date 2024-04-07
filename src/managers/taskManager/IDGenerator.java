package managers.taskManager;

public class IDGenerator {

    static int sequence = 0;

    static int generateNewID() {
        return sequence++;
    }
}
