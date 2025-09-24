package factory.controlledSystem;

import factory.communication.message.DoWorkMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class DispenserStation extends FactoryNode{

    public DispenserStation(char k) {
        super(k);

        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Subscribe
    public void onDoWorkMessage(DoWorkMessage message){

    }

    @Override
    public String toString() {
        return "DispenserStation{" +
                ", position='" + position + '\'' +
                ", key=" + key +
                '}';
    }
}
