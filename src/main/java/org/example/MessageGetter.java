package org.example;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MessageGetter {

    public MessageGetter(EventBus eventBus){
        eventBus.register(this);
    }

    @Subscribe
    public void onBusMessage(BusMessage message){
        System.out.println("received message with number: " + message.getNumber());
    }
}
