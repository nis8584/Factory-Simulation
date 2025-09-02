package factory.queueAndScheduler;

import factory.communication.SetLayoutMessage;
import factory.communication.SetQueueMessage;
import factory.controlledSystem.FactoryNode;
import factory.controlledSystem.WorkStation;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Scheduler implements Runnable {

    private final Map<WorkStation, Integer> workStationStatus = new HashMap<>();// 0= idle, 1 = busy, 2 = broken?,....

    private ScheduleStrategies strategy;

    private Queue queue;

    private boolean queuePresent = false;
    private boolean layoutPresent = false;

    private EventBus eventBus;

    public Scheduler(EventBus eventBus){
        strategy = ScheduleStrategies.FirstInFirstOut;
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Override
    public void run() {
        Thread t = new Thread(()->{
        while(true){


            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        });
        t.start();
    }

    private Task getNext(){
        LinkedList<Task> taskList = queue.getTasks();
        Task nextTask = null;
        switch (strategy){
            case FirstInFirstOut:
                nextTask = taskList.getFirst();
                break;
            case ShortestProcessingTime:
                //logic here
                break;
            case EarliestDueDate:
                //logic here
                break;
            case OperationalDueDate:
                //logic here
                break;
        }
        return nextTask;
    }

    public void addTask(Task task){
        queue.addToQueue(task);
    }

    public void setStrategy(ScheduleStrategies strategy) {
        this.strategy = strategy;
    }

    @Subscribe
    public void onSetQueueMessage(SetQueueMessage message){
        if(message.getQueue() == null) return;
        queue = message.getQueue();
        queuePresent = true;
    }

    @Subscribe
    public void onSetLayoutMessage(SetLayoutMessage message){
        if(message.getFactoryNodes() == null) return;
        for(FactoryNode fn : message.getFactoryNodes()){
            if(fn instanceof WorkStation){
                workStationStatus.put((WorkStation) fn,0);
            }
        }
        layoutPresent = true;
    }
}
