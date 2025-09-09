package factory.controlledSystem;

import factory.communication.GlobalConstants;
import factory.communication.PostingService;
import factory.communication.message.DoWorkMessage;
import factory.communication.message.SendTaskToNextMessage;
import factory.communication.message.WorkStationCostChangeMessage;
import factory.queueAndScheduler.Task;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class WorkStation extends FactoryNode{

    private String typeOfWork;

    private int processingCost = 1;

    public WorkStation(char key){
        super(key);

        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }


    public String getTypeOfWork() {
        return typeOfWork;
    }

    public void setTypeOfWork(String typeOfWork) {
        this.typeOfWork = typeOfWork;
        PostingService.log("Set WorkStation " + key + " to work on: " + typeOfWork);
    }

    public int getProcessingCost() {
        return processingCost;
    }

    public void setProcessingCost(int processingCost) {
        this.processingCost = processingCost;
        eventBus.post(new WorkStationCostChangeMessage());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDoWorkMessage(DoWorkMessage message){
        synchronized (this){
            if(message.getWorkKey() != key) return;
            Task currentTask = message.getTask();
            if(currentTask.getRequiredWorkStations().getFirst().equals(typeOfWork)){
                try {
                    Thread.sleep((long) message.getTravelCost() * GlobalConstants.TimeFactor);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                    currentTask.doWorkAt(typeOfWork);
                try {
                    Thread.sleep((long) processingCost * GlobalConstants.TimeFactor);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                eventBus.post(new SendTaskToNextMessage(key,currentTask));
            }
        }
    }

}
