package factory.queueAndScheduler;

import java.util.List;

public class TaskX extends Task{


    public TaskX(List<String> requiredTasks) {
        super(requiredTasks);
    }

    @Override
    public String toString() {
        return "TaskX{"+super.toString() +"}";
    }
}
