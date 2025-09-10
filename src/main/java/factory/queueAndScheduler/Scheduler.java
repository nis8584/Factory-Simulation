package factory.queueAndScheduler;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import factory.communication.PostingService;
import factory.communication.message.*;
import factory.controlledSystem.Factory;
import factory.controlledSystem.FactoryInterface;
import factory.controlledSystem.FactoryNode;
import factory.controlledSystem.WorkStation;
import javafx.scene.paint.Color;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.*;

@Singleton
public class Scheduler implements SchedulerInterface {

    private final Map<WorkStation, Integer> workStationStatus = new HashMap<>();// 0= idle, 1 = busy, 2 = broken?,....

    private ScheduleStrategies strategy;

    private Queue queue;

    private boolean queuePresent = false;
    private boolean layoutPresent = false;

    private final EventBus eventBus;

    private final Factory factory;
    private final int numberOfRunningChains = 5;
    private int runningChains = 0;

    private final Object chainCounterMutex = new Object();

    @Inject
    public Scheduler(EventBus eventBus, Injector injector){
        this.factory = (Factory) injector.getInstance(FactoryInterface.class);
        strategy = ScheduleStrategies.FirstInFirstOut;
        this.eventBus = eventBus;
        eventBus.register(this);
    }


    private Task getNext(){
        LinkedList<Task> taskList = queue.getTasks();
        Task nextTask = null;
        switch (strategy){
            case FirstInFirstOut:
                try{
                    nextTask = taskList.getFirst();

                } catch (NoSuchElementException e){
                    eventBus.post(new LogMessage("queue is empty!"));
                    return null;
                }
                eventBus.post(new LogMessage("Remaining tasks in queue: " + taskList.size()));
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
        taskList.remove(nextTask);
        return nextTask;
    }
    private boolean canNextTaskBeStarted(){
        LinkedList<Task> tasks = queue.getTasks();
        if(tasks.isEmpty()) return false;
        List<WorkStation> availableStations = factory.getAvalibleWorkStations(tasks.getFirst().getRequiredWorkStations().getFirst());
        return !availableStations.isEmpty();
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

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onStartSimulation(StartWorkSimulation message){
        if(!(queuePresent && layoutPresent)) {
            PostingService.log("first set queue and layout");
            return;
        }
        startTask();
    }

    public void startTask(){
        Task next = getNext();
        if(next == null) return;
        WorkStation cheapestStation = findNextStation(next, factory.getDispenserStation().getKey());
        if(cheapestStation == null && !next.getRequiredWorkStations().isEmpty()) {
            addTask(next);
            int n;
            synchronized (chainCounterMutex) {
                n = runningChains;
            }
            if (n <= 1) {
                Thread t = new Thread(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    PostingService.log("No Workstation suits next step in Task");
                    eventBus.post(new StartWorkSimulation());
                });
                t.start();
            } else {
                synchronized (chainCounterMutex){
                    runningChains--;
                }
            }
            return;
        }
        FactoryNode from = factory.getDispenserStation();
        LinkedList<FactoryNode> path;
        if(next.getRequiredWorkStations().isEmpty()){
            path = factory.getPathTable().get(from.getKey()).get(factory.getDropOffStation().getKey());
        } else {
            path = factory.getPathTable().get(from.getKey()).get(cheapestStation.getKey());
        }

        next.setStartTime();
            eventBus.post(new AnimateMoveMessage(path, Color.GREEN));
            eventBus.post(new DoWorkMessage(next, FactoryNode.costPerPath(path), cheapestStation.getKey()));
            synchronized (chainCounterMutex){
                runningChains++;
            }
            int n;
            synchronized (chainCounterMutex){
                n = runningChains;
            }
            if(canNextTaskBeStarted() && n<numberOfRunningChains){
                try {
                    Thread.sleep(500);
                    eventBus.post(new StartWorkSimulation());
                } catch (InterruptedException e) {
                    //todo better logging
                    throw new RuntimeException(e);
                }
            }
    }

    @Subscribe
    public void onSendTaskToNextMessage(SendTaskToNextMessage message){
            FactoryNode from = factory.getNodeByKey(message.getKey());
            FactoryNode to;
            Task currentTask = message.getTask();
            workStationStatus.replace((WorkStation) from, 0);
            if(!currentTask.isTaskDone()){
                to = findNextStation(currentTask, from.getKey());
            } else {
                to = factory.getDropOffStation();
            }
            if(to == null) {
                to = factory.getDispenserStation();
                addTask(currentTask);
            }
            LinkedList<FactoryNode> path = factory.getPathTable().get(from.getKey()).get(to.getKey());
            eventBus.post(new AnimateMoveMessage(path, Color.GREEN));
            eventBus.post(new DoWorkMessage(currentTask, FactoryNode.costPerPath(path), to.getKey()));
    }

    private WorkStation findNextStation(Task task, char from){
            if(task == null) return null;
            if(task.getRequiredWorkStations().isEmpty()) return null;
            List<WorkStation> possibleStations = factory.getAvalibleWorkStations(task.getRequiredWorkStations().getFirst());
            WorkStation cheapestStation = null;
            Map<Character, Map<Character, LinkedList<FactoryNode>>> paths = factory.getPathTable();
        synchronized (this) {
            for(WorkStation station: possibleStations){
                if(workStationStatus.get(station) >= 1) continue;
                if(cheapestStation == null) cheapestStation = station;
                if(cheapestStation.getProcessingCost() +  FactoryNode.costPerPath(paths.get(from).get(cheapestStation.getKey())) > station.getProcessingCost() + FactoryNode.costPerPath(paths.get(from).get(station.getKey())) && workStationStatus.get(station) == 0) cheapestStation = station;
            }
            workStationStatus.replace(cheapestStation,1);
        }
        return cheapestStation;
    }

}
