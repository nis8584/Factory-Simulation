package factory.controlledSystem;

import org.example.BusMessage;
import org.greenrobot.eventbus.Subscribe;

public class WorkStation extends FactoryNode{

    public WorkStation(char key){
        super(key);
    }

    @Subscribe
    public void onDoWorkMessage(BusMessage message){
        System.out.println("hallo");
    }
}
