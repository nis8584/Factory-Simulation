package factory.controlledSystem;

import com.google.inject.Inject;
import org.example.BusMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class WorkStation extends FactoryNode{

    private EventBus eventBus;

    @Inject
    public WorkStation(char key, EventBus eventBus){
        super(key);
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Subscribe
    public void onBusMessage(BusMessage message){
        System.out.println("hallo");
    }
}
