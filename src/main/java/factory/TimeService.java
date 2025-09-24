package factory;

import com.google.inject.Singleton;
import factory.communication.GlobalConstants;

/**
 * Class that keeps a counter which represents time passing in this system.
 * Uses specified timeunit in GlobalConstants.TimeFactor
 */
@Singleton
public class TimeService implements TimeServiceInterface {

    private static long time = 0;

    private boolean running = false;

    private void increment(){
        if(time<0){
            //replace with exception + logging instead
            time = 0;
        }
        time++;
    }

    public static long getTime(){
        return time;
    }

    public void reset(){
        time = 0;
    }

    public void start(){
        if(running) return;
        running = true;
        Thread t = new Thread(()->{
            while (true){
                try {
                    Thread.sleep((long) GlobalConstants.TimeFactor);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                increment();
            }
        });
        t.start();
    }
}
