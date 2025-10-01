package factory.queueAndScheduler;

import java.util.LinkedList;

/**
 * Class that represents Tasks
 */
public class TaskX extends Task{

    public TaskX(LinkedList<LinkedList<String>> requiredTasks, String name, int id) {
        super(requiredTasks, name, id);
    }

    @Override
    public String toString() {
        return "TaskX{"+super.toString() +"}";
    }
}
