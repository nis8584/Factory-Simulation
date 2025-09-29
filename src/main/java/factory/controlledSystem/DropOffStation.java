package factory.controlledSystem;

import factory.communication.GlobalConstants;
import factory.communication.message.DoSchedulingMessage;
import factory.communication.message.DoWorkMessage;
import factory.communication.message.LogTimeMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Class that represents the last stop in a tasks' lifecycle. When a task arrives here, the scheduler is prompted to start another task. Also logs the runtime of the task.
 */
public class DropOffStation extends FactoryNode{

    public DropOffStation(char key) {
        super(key);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }


    @Subscribe()
    public void onDoWorkMessage(DoWorkMessage message){
        if (message.getWorkKey() == getKey()){
            Thread t = new Thread(()->{
                try {
                    Thread.sleep((long) message.getTravelCost() * GlobalConstants.TimeFactor);
                    eventBus.post(new DoSchedulingMessage(null));
                    eventBus.post(new LogTimeMessage(message.getTask().getRunningTime()));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            t.start();
        }
    }

    @Override
    public String toString() {
        return "DropOffStation{" +
                ", position='" + position + '\'' +
                ", key=" + key +
                '}';
    }
}
