package factory.controlledSystem;

import factory.communication.GlobalConstants;
import factory.communication.message.DoWorkMessage;
import factory.communication.message.StartWorkSimulation;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DispenserStation extends FactoryNode{

    public DispenserStation(char k) {
        super(k);

        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDoWorkMessage(DoWorkMessage message){
         if(message.getWorkKey() != key) return;
         Thread t = new Thread(()->{
             try {
                 Thread.sleep((long) message.getTravelCost() * GlobalConstants.TimeFactor);
             } catch (InterruptedException e) {
                 throw new RuntimeException(e);
             }
             eventBus.post(new StartWorkSimulation());
         });
         t.start();
    }
}
