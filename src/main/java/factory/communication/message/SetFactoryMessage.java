package factory.communication.message;

import factory.controlledSystem.FactoryNode;
import factory.queueAndScheduler.Queue;

import java.util.LinkedList;
import java.util.Map;

/**
 * Message that is sent after parsing an input to queue, factory node and task data.
 *
 * @see factory.queueAndScheduler.FileParser
 * @see factory.controlledSystem.Factory
 * @see factory.queueAndScheduler.Scheduler
 */
public class SetFactoryMessage implements Message{

    private final LinkedList<FactoryNode> factoryNodes;

    private final Queue queue;

    private final Map<String,LinkedList<String>> tasksAndSteps;

    public SetFactoryMessage(LinkedList<FactoryNode> factoryNodes, Queue queue, Map<String,LinkedList<String>> tasksAndSteps) {
        this.factoryNodes = factoryNodes;
        this.queue = queue;
        this.tasksAndSteps = tasksAndSteps;
    }

    public Map<String, LinkedList<String>> getTasksAndSteps() {
        return tasksAndSteps;
    }

    public LinkedList<FactoryNode> getFactoryNodes() {
        return factoryNodes;
    }

    public Queue getQueue() {
        return queue;
    }
}
