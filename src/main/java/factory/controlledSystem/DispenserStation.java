package factory.controlledSystem;

import org.greenrobot.eventbus.Subscribe;

public class DispenserStation extends FactoryNode{

    public DispenserStation(char k) {
        super(k);
    }

    @Subscribe
    public void onDropItem(){
        //idk if this is needed
    }
}
