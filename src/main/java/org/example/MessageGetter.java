package org.example;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class MessageGetter {

    public MessageGetter(EventBus eventBus){
        eventBus.register(this);
    }

    @Subscribe
    public void onBusMessage(BusMessage message){
        System.out.println("received message with number: " + message.getNumber());
    }
}
