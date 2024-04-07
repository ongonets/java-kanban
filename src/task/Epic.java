package task;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subTaskID;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        subTaskID = new ArrayList<>();
    }

    public Epic(String name, String description, TaskStatus status, int taskID) {
        super(name, description, status, taskID);
        subTaskID = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", taskID=" + super.getTaskID() +
                ", status=" + super.getStatus() +
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

    public ArrayList<Integer> getSubTaskID() {
        return new ArrayList<>(subTaskID);
    }

    public void setSubTaskID(ArrayList<Integer> subTaskID) {

        this.subTaskID = new ArrayList<>(subTaskID);
    }

    public void addSubTask(int subTaskID) {
        this.subTaskID.add(subTaskID);
    }

    public void removeSubTask(int subTaskID) {
        this.subTaskID.remove((Integer) subTaskID);
    }

    public void removeAllSubTask() {
        this.subTaskID.clear();
    }
}
