package factory.queueAndScheduler;

import java.util.LinkedList;

public class Scheduler implements Runnable {

    private ScheduleStrategies strategy;

    private final Queue queue;

    @Override
    public void run() {
        Thread t = new Thread(()->{
        while(true){


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        });
        t.start();
    }
    public Scheduler(Queue queue){
        this.queue = queue;
        strategy = ScheduleStrategies.FirstInFirstOut;
    }

    public Task getNext(){
        LinkedList<Task> taskList = queue.getTasks();
        Task nextTask = null;
        switch (strategy){
            case FirstInFirstOut:
                nextTask = taskList.getFirst();
            case ShortestProcessingTime:
                //logic here
            case EarliestDueDate:
                //logic here
            case OperationalDueDate:
                //logic here
        }
        return nextTask;
    }

    public void addTask(Task task){
        queue.addToQueue(task);
    }

    public void setStrategy(ScheduleStrategies strategy) {
        this.strategy = strategy;
    }
}
