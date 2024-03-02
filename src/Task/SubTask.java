package Task;

public class SubTask extends Task {

    private int epicID;

    public SubTask(String name, String description, TaskStatus status, int epicID) {
        super(name, description, status);
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

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }
}
