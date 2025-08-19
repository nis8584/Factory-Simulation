package org.example;

import com.google.common.eventbus.EventBus;

import java.util.Random;

public class PostRunner extends Thread{

    private static EventBus eventBus;

    public PostRunner(EventBus eventBus){
        eventBus.register(this);
        this.eventBus = eventBus;
    }

    public void run(){
        while (true){
            System.out.println("send message on eventbus");
            eventBus.post(new BusMessage(new Random().nextInt(11)));
            try {
                int a = new Random().nextInt(5);
                this.sleep(1000*a);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
