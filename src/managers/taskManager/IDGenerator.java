package managers.taskManager;

import java.util.List;
import java.util.UUID;

public class IDGenerator {



    static UUID generateNewID(List<UUID> list) {
        UUID newUUID = UUID.randomUUID();
        while (list.contains(newUUID))
            newUUID = UUID.randomUUID();
        return newUUID;
    }
}
