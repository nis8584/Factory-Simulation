package factory.controlledSystem;

import factory.communication.GlobalConstants;
import factory.communication.PostingService;
import factory.communication.message.DoWorkMessage;
import factory.communication.message.StartWorkSimulation;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DropOffStation extends FactoryNode{

    public DropOffStation(char key) {
        super(key);

        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDoWorkMessage(DoWorkMessage message){
        if(message.getWorkKey()!= key) return;
        Thread t = new Thread(()->{
            try {
                Thread.sleep((long) message.getTravelCost() * GlobalConstants.TimeFactor);
            } catch (InterruptedException e){
                throw new RuntimeException(e);
            }
            PostingService.log("Task finished. Time to finish: " + message.getTask().getRunningTime());
            eventBus.post(new StartWorkSimulation());
        });
        t.start();
    }
}
