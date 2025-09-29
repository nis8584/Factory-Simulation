package factory.queueAndScheduler;

import java.util.List;

/**
 * Class that represents Tasks
 */
public class TaskX extends Task{

    public TaskX(List<String> requiredTasks) {
        super(requiredTasks);
    }

    @Override
    public String toString() {
        return "TaskX{"+super.toString() +"}";
    }
}
