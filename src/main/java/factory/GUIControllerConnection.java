package factory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import factory.communication.message.AnimateMoveMessage;
import factory.communication.message.LogMessage;
import factory.communication.message.SetLayoutMessage;
import factory.communication.message.SetQueueMessage;
import factory.controlledSystem.Factory;
import factory.controlledSystem.FactoryInterface;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class GUIControllerConnection implements GUIControllerConnectionInterface{

    private GraphicsController controller;

    private final Factory factory;

    @Inject
    public GUIControllerConnection(EventBus eventBus, Injector injector){
        eventBus.register(this);
        this.factory = (Factory) injector.getInstance(FactoryInterface.class);
    }

    public void setController(GraphicsController controller) {
        this.controller = controller;
    }

    @Subscribe
    public void onLogMessage(LogMessage logMessage){
        controller.log(logMessage.getMsg());
    }

    @Subscribe
    public void onAnimateMoveMessage(AnimateMoveMessage message){
        controller.animateMovement(message.getColor(), message.getPath());
    }

    @Subscribe
    public void onSetQueue(SetQueueMessage message) {
        controller.setQueue(message.getQueue());
    }

    @Subscribe
    public void onSetLayout(SetLayoutMessage message){
        controller.onSetLayout(message.getFactoryNodes());
    }
}
