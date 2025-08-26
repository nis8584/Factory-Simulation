package factory.queueAndScheduler;

import java.util.LinkedList;

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
}
