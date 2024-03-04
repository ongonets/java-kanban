package Task;

import java.util.Objects;

public class SubTask extends Task {

    private int epicID;

    public SubTask(String name, String description, TaskStatus status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
    }
    public SubTask(String name, String description, TaskStatus status, int epicID, int taskID) {
        super(name, description, status, taskID);
        this.epicID = epicID;
    }
    @Override
    public String toString() {
        return "SubTusk{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", taskID=" + super.getTaskID() +
                ", status=" + super.getStatus() +
                ", epicID=" + epicID +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        SubTask subTask = (SubTask) object;
        return epicID == subTask.epicID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicID);
    }

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }
}
