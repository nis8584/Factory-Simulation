package factory.communication.message;

import factory.queueAndScheduler.Task;

public class SendTaskToNextMessage {
    private final char key;
    private final Task task;

    public SendTaskToNextMessage(char key, Task task) {
        this.key = key;
        this.task = task;
    }

    public char getKey() {
        return key;
    }

    public Task getTask() {
        return task;
    }
}
