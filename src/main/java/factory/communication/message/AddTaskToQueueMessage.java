package factory.communication.message;

import factory.queueAndScheduler.Task;

public class AddTaskToQueueMessage implements Message{

    private final Task task;

    public AddTaskToQueueMessage(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
