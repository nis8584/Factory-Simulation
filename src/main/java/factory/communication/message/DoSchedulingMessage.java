package factory.communication.message;

import factory.queueAndScheduler.Task;

public class DoSchedulingMessage implements Message{

    private final Task task;

    public DoSchedulingMessage(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
