package factory.queueAndScheduler;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import factory.TimeService;
import factory.TimeServiceInterface;
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

    private final EventBus eventBus;

    private final Factory factory;

    private final TimeService timeService;

    private int concurrentTaskLimit = 1;

    private int currentRunningTasks = 0;

    private final Object chainCounterMutex = new Object();

    private long startTime;

    private boolean started = false;


    @Inject
    public Scheduler(EventBus eventBus, Injector injector, TimeServiceInterface timeService){
        this.factory = (Factory) injector.getInstance(FactoryInterface.class);
        strategy = ScheduleStrategies.FirstInFirstOut;
        this.eventBus = eventBus;
        eventBus.register(this);
        this.timeService = (TimeService) timeService;
    }


    private Task getNext(){
        LinkedList<Task> taskList = queue.getTasks();
        Task nextTask = null;
        switch (strategy){
            case FirstInFirstOut:
                try{
                    nextTask = taskList.getFirst();

                } catch (NoSuchElementException e){
                    eventBus.post(new RunTimeMessage(TimeService.getTime() - startTime));
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

    public void addTask(Task task){
        queue.addToQueue(task);
        if(queue.getTasks().size() == 1){
            eventBus.post(new DoSchedulingMessage(null));
        }
    }

    public void setStrategy(ScheduleStrategies strategy) {
        this.strategy = strategy;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onAddTaskToQueueMessage(AddTaskToQueueMessage message){
        message.getTask().setCurrentNodeLocation(factory.getDispenserStation());
        addTask(message.getTask());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void setConcurrentTaskLimit(SetConcurrencyMessage message){
        concurrentTaskLimit = message.getCount();
        System.out.println(concurrentTaskLimit);
    }

    @Subscribe
    public void onSetFactoryMessage(SetFactoryMessage message){
        if(message.getFactoryNodes() == null || message.getQueue() == null) return;
        queue = message.getQueue();
        started = false;
        workStationStatus.clear();
        for(FactoryNode fn : message.getFactoryNodes()){
            if(fn instanceof WorkStation){
                workStationStatus.put((WorkStation) fn,0);
            }
        }
        queue.getTasks().forEach(t -> t.setCurrentNodeLocation(factory.getDispenserStation()));
    }

    @Subscribe()
    public void doScheduling(DoSchedulingMessage message){
        timeService.start();
        if(!started){
            startTime = TimeService.getTime();
            started = true;
            currentRunningTasks = 0;
        }
        Task t = message.getTask();
        if(t == null){
            if(!moreTasksAllowed()) return;
            t = getNext();
            synchronized (chainCounterMutex){
                currentRunningTasks++;
            }
        } else {
            if(t.getCurrentNodeLocation() instanceof  WorkStation){
                workStationStatus.replace((WorkStation) t.getCurrentNodeLocation(),0);
            }
        }
        if(t == null){
            //queue empty
            synchronized (chainCounterMutex){
                currentRunningTasks--;
            }
            return;
        }
        if(t.isTaskDone()){
            moveTaskFromTo(t,t.getCurrentNodeLocation(),factory.getDropOffStation());
            synchronized (chainCounterMutex){
                currentRunningTasks--;
            }
        } else {
            WorkStation cheapestStation;
            synchronized (chainCounterMutex){
            cheapestStation = findBestWorkstation(t, t.getCurrentNodeLocation());
            }
            if(cheapestStation != null){
                moveTaskFromTo(t, t.getCurrentNodeLocation(), cheapestStation);
                workStationStatus.replace(cheapestStation,1);
            } else {
                moveTaskFromTo(t, t.getCurrentNodeLocation(), factory.getDispenserStation());
                addTask(t);
            }
        }
        if(moreTasksAllowed()){
            eventBus.post(new DoSchedulingMessage(null));
        }
    }
    private void moveTaskFromTo(Task task, FactoryNode from, FactoryNode to){
        LinkedList<FactoryNode> path = factory.getPathTable().get(from.getKey()).get(to.getKey());      //retrieve precalculated path
        task.setStartTime(TimeService.getTime());
        eventBus.post(new AnimateMoveMessage(path, Color.GREEN));                                       //message for graphics
        to.onDoWorkMessage(new DoWorkMessage(task, FactoryNode.costPerPath(path), to.getKey()));        //message for internal logic
        task.setCurrentNodeLocation(to);

    }
    private boolean moreTasksAllowed(){
        synchronized (chainCounterMutex){
        return  currentRunningTasks < concurrentTaskLimit;
        }
    }


    private WorkStation findBestWorkstation(Task task, FactoryNode from){
        String nextRequiredStep = task.getRequiredWorkStations().getFirst();
        WorkStation result = null;
        for(WorkStation ws: factory.getAvailableWorkStations(nextRequiredStep)){
            if(workStationStatus.get(ws) > 0) continue;
            if(result == null){
                result = ws;
            } else if (getCostFromTo(from, result) + result.getProcessingCost(nextRequiredStep) > getCostFromTo(from, ws) + ws.getProcessingCost(nextRequiredStep)) result = ws;

        }
        return result;

    }

    private int getCostFromTo(FactoryNode from, FactoryNode to){
        return FactoryNode.costPerPath(factory.getPathTable().get(from.getKey()).get(to.getKey()));
    }


}
