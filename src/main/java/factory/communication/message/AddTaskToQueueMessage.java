package factory.communication.message;

import factory.queueAndScheduler.Task;

/**
 * Message that adds a new Task to the queue.
 */
public class AddTaskToQueueMessage implements Message{

    private final Task task;

    public AddTaskToQueueMessage(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
