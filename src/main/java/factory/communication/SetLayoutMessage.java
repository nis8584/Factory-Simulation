package factory.communication;

import factory.controlledSystem.FactoryNode;

import java.util.LinkedList;

public class SetLayoutMessage implements Message{

    private final LinkedList<FactoryNode> factoryNodes;

    public SetLayoutMessage(LinkedList<FactoryNode> factoryNodes) {
        this.factoryNodes = factoryNodes;
    }

    public LinkedList<FactoryNode> getFactoryNodes() {
        return factoryNodes;
    }
}
