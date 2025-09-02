package factory.communication;

import factory.queueAndScheduler.Task;

public class DoWorkMessage implements Message{

    private final Task task;

    public DoWorkMessage(Task task) {
        this.task = task;
    }

}
