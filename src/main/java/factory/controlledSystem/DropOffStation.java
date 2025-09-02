package factory.controlledSystem;

import org.greenrobot.eventbus.Subscribe;

public class DropOffStation extends WorkStation{

    public DropOffStation(char key) {
        super(key);
    }

    @Subscribe
    public void onDropOff(){
        //some sort of deliver function, maybe log stats from here?
    }
}
