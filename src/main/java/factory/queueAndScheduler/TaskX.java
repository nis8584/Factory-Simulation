package factory.queueAndScheduler;

import java.util.LinkedList;

public class TaskX extends Task{


    protected TaskX(LinkedList<String> requiredTasks) {
        super(requiredTasks);
    }

    @Override
    public String toString() {
        return "TaskX{"+super.toString() +"}";
    }
}
