package org.example;

import com.google.common.eventbus.EventBus;
// Test for functionality of Eventbus with multiple Runners
public class Main {
    public static void main(String[] args) {
        EventBus eventBus = new EventBus();
        PostRunner[] runners = new PostRunner[3];
        MessageGetter messageGetter = new MessageGetter(eventBus);
        System.out.println("Jetzt gehts los:");
        for(int i = 0; i < runners.length; i++){
            runners[i] = new PostRunner(eventBus);
            runners[i].start();
        }

    }
}