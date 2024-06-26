package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Epic extends Task {

    private ArrayList<UUID> subTaskID;
    private LocalDateTime endTime;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        subTaskID = new ArrayList<>();
    }

    public Epic(String name, String description, TaskStatus status, UUID taskID) {
        super(name, description, status, taskID);
        subTaskID = new ArrayList<>();
    }

    public Epic(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        subTaskID = new ArrayList<>();
    }

    public Epic(String name, String description, TaskStatus status,
                Duration duration, LocalDateTime startTime, UUID taskID) {
        super(name, description, status, duration, startTime, taskID);
        subTaskID = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", taskID=" + super.getTaskID() +
                ", status=" + super.getStatus() +
                ", duration=" + super.getDuration() +
                ", startTime=" + super.getStartTime() +
                ", subTaskID=" + subTaskID +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Epic epic = (Epic) object;
        return Objects.equals(subTaskID, epic.subTaskID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskID);
    }

    public ArrayList<UUID> getSubTaskID() {
        return new ArrayList<>(subTaskID);
    }

    public void setSubTaskID(ArrayList<UUID> subTaskID) {
        this.subTaskID = new ArrayList<>(subTaskID);
    }

    public void addSubTask(UUID subTaskID) {
        this.subTaskID.add(subTaskID);
    }

    public void removeSubTask(UUID subTaskID) {
        this.subTaskID.remove(subTaskID);
    }

    public void removeAllSubTask() {
        this.subTaskID.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
