import managers.Managers;
import managers.taskManager.TaskManager;
import task.Task;
import task.TaskStatus;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        taskManager.addNewTask(new Task("task1","csjc", TaskStatus.NEW));
        taskManager.addNewTask(new Task("task2","csjc", TaskStatus.NEW));
        taskManager.addNewTask(new Task("task3","csjc", TaskStatus.NEW));
        taskManager.addNewTask(new Task("task4","csjc", TaskStatus.NEW));
        taskManager.addNewTask(new Task("task5","csjc", TaskStatus.NEW));
        taskManager.addNewTask(new Task("task6","csjc", TaskStatus.NEW));

        taskManager.getTask(0);
        taskManager.getTask(1);
        taskManager.getTask(1);


        System.out.println(taskManager.getHistory());




    }
}
