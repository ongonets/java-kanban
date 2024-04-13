package managers.taskManager;

public class IDGenerator {

    static private int sequence = 0;

    static int generateNewID() {
        return sequence++;
    }
}
