import java.util.ArrayList;

public class Epic extends Task{

    private ArrayList<Integer> subTaskID;

    public Epic(String name, String description,  TaskStatus status) {
        super(name, description, status);
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

    public ArrayList<Integer> getSubTaskID() {
        return subTaskID;
    }

    public void setSubTaskID(ArrayList<Integer> subTaskID) {
        this.subTaskID = subTaskID;
    }


}
