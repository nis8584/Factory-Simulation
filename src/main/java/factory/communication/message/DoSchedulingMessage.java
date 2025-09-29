package factory.communication.message;

import factory.queueAndScheduler.Task;

/**
 * Message that makes the scheduler check if it can send more Tasks to WorkStations.
 */
public class DoSchedulingMessage implements Message{

    private final Task task;

    public DoSchedulingMessage(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
