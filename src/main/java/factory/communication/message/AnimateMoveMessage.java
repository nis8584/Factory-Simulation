package factory.communication.message;

import factory.controlledSystem.FactoryNode;
import javafx.scene.paint.Color;

import java.util.LinkedList;

/**
 * Message that makes the GraphicsController animate a dot move along a given path.
 */
public class AnimateMoveMessage implements Message{

    private final LinkedList<FactoryNode> path;


    private final Color color;


    public AnimateMoveMessage(LinkedList<FactoryNode> path, Color color) {
        this.path = path;
        this.color = color;
    }

    public LinkedList<FactoryNode> getPath() {
        return path;
    }

    public Color getColor() {
        return color;
    }
}
