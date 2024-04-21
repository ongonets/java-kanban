package managers.taskManager;

import java.io.IOException;

public class ManagerReadException extends RuntimeException {
    public ManagerReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
