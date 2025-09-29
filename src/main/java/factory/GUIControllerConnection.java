package factory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import factory.communication.message.*;
import factory.controlledSystem.Factory;
import factory.controlledSystem.FactoryInterface;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Class that acts a connection between the GUI and the rest of the simulation system
 */
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

    /**
     * Method to react to an incoming LogMessage. Calls the log method of the GUI controller
     * @param logMessage incoming message
     */
    @Subscribe
    public void onLogMessage(LogMessage logMessage){
        controller.log(logMessage.getMsg());
    }

    /**
     * Method to react to an incoming AnimateMoveMessage. Calls the animate method of the GUI controller.
     * @param message incoming message
     */
    @Subscribe
    public void onAnimateMoveMessage(AnimateMoveMessage message){
        controller.animateMovement(message.getColor(), message.getPath());
    }

    /**
     * Method to react to an incoming SetFactoryMessage. Calls the setter of the GUI controller which then loads the graphical elements and other data.
     * @param message incoming message
     */
    @Subscribe
    public void onSetFactoryMessage(SetFactoryMessage message){
        controller.setFactoryInfo(message.getFactoryNodes(),message.getQueue(), message.getTasksAndSteps());
    }
}
