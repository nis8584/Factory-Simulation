package factory.communication.message;

import factory.queueAndScheduler.Task;

/**
 * Message that notifies a FactoryNode of an incoming Task and its travel cost.
 */
public class DoWorkMessage implements Message{

    private final Task task;

    private final int travelCost;

    private final char workKey;

    public DoWorkMessage(Task task, int travelCost, char workKey) {
        this.task = task;
        this.travelCost = travelCost;
        this.workKey = workKey;
    }

    public Task getTask() {
        return task;
    }

    public int getTravelCost() {
        return travelCost;
    }

    public char getWorkKey() {
        return workKey;
    }
}
