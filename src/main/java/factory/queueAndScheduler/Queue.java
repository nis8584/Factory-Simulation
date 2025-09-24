package factory.queueAndScheduler;

import java.util.LinkedList;

/**
 * Class that holds Tasks
 */
public class Queue {
    private LinkedList<Task> tasks;

    public Queue(LinkedList<Task> tasks){
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "Queue{" +
                "tasks=" + tasks +
                '}';
    }

    public LinkedList<Task> getTasks() {
        return tasks;
    }

    public void addToQueue(Task task){
        tasks.add(task);
    }
}
