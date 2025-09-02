package factory.communication;

import factory.queueAndScheduler.Queue;

public class SetQueueMessage implements Message {

    public SetQueueMessage(Queue queue) {
        this.queue = queue;
    }

    private final Queue queue;

    public Queue getQueue() {
        return queue;
    }
}
