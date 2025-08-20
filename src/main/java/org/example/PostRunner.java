package org.example;

import com.google.common.eventbus.EventBus;

import java.util.Random;

public class PostRunner extends Thread{

    private static EventBus eventBus;

    public PostRunner(EventBus eventBus){
        eventBus.register(this);
        this.eventBus = eventBus;
    }

    @Override
    public void run(){
        while (true){
            Random r = new Random();
            System.out.println("send message on eventbus");
            eventBus.post(new BusMessage(r.nextInt(11)));
            try {
                int a = r.nextInt(5);
                Thread.sleep(1000L*a);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
