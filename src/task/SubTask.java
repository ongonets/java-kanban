package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class SubTask extends Task {

    private UUID epicID;

    public SubTask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public SubTask(String name, String description, TaskStatus status, UUID epicID) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public SubTask(String name, String description, TaskStatus status, UUID epicID, UUID taskID) {
        super(name, description, status, taskID);
        this.epicID = epicID;
    }

    public SubTask(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime, UUID epicID) {
        super(name, description, status, duration, startTime);
        this.epicID = epicID;
    }

    public SubTask(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime, UUID taskID, UUID epicID) {
        super(name, description, status, duration, startTime, taskID);
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "SubTusk{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", taskID=" + super.getTaskID() +
                ", status=" + super.getStatus() +
                ", duration=" + super.getDuration() +
                ", startTime=" + super.getStartTime() +
                ", epicID=" + epicID +
                '}';
    }

     @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        SubTask subTask = (SubTask) object;
        return Objects.equals(epicID,(subTask.epicID));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public UUID getEpicID() {
        return epicID;
    }

    public void setEpicID(UUID epicID) {
        this.epicID = epicID;
    }
}
