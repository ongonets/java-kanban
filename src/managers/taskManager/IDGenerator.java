package managers.taskManager;

import java.util.Set;
import java.util.UUID;

public class IDGenerator {



    static UUID generateNewID(Set<UUID> list) {
        UUID newUUID = UUID.randomUUID();
        while (list.contains(newUUID))
            newUUID = UUID.randomUUID();
        return newUUID;
    }
}
