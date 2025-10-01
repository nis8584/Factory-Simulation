package factory.controlledSystem;

import factory.communication.GlobalConstants;
import factory.communication.message.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
/**
 * Class that represents a real life work station in this simulation.
 * <p>
 *     Has types of work and associated costs
 *     Reacts to DoWorkMessages and "does" the required work(waits for the duration equal to the work cost)
 * </p>
 */
public class WorkStation extends FactoryNode{

    private final Map<String, Integer> typeOfWork = new TreeMap<>();


    public WorkStation(char key){
        super(key);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    public Map<String, Integer> getTypesOfWork() {
        return Map.copyOf(typeOfWork);
    }

    public void addTypeOfWork(String typeOfWork, int cost) {
        this.typeOfWork.put(typeOfWork, cost);
    }

    public int getProcessingCost(String forWork) {
        return typeOfWork.get(forWork);
    }

    public int getProcessingCost(List<String> forWork){
        int sum = 0;
        for(String s: forWork){
            if(typeOfWork.containsKey(s)) sum += typeOfWork.get(s);
        }
        return sum;
    }

    public void setProcessingCost(String forWork, int processingCost) {
        typeOfWork.replace(forWork, processingCost);
        eventBus.post(new WorkStationCostChangeMessage());
    }



    public static WorkStation getNodeByChar(char c, LinkedList<FactoryNode> list){
        for(FactoryNode node : list){
            if(node.getKey() == c && node instanceof WorkStation) return (WorkStation) node;
        }
        return null;
    }

    @Subscribe
    public void onDoWorkMessage(DoWorkMessage message){
        if(message.getWorkKey() == this.getKey()){
            Thread t = new Thread(()->{
                List<String> currentStep = message.getTask().getRequiredWorkStations().getFirst();
                if(typeOfWork.keySet().stream().anyMatch(currentStep::contains)){
                    try {
                        giveStatus("waiting for task to arrive");
                        Thread.sleep((long) message.getTravelCost()  * GlobalConstants.TimeFactor );
                        giveStatus("working on " + currentStep);
                        Thread.sleep((long) getWorkTime(currentStep) * GlobalConstants.TimeFactor);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    for(String s: currentStep){
                        eventBus.post(new TaskStepStatusMessage(message.getTask().getId(), s, "finished work"));
                    }
                    message.getTask().doWorkAt(typeOfWork.keySet());
                    giveStatus("idle");
                    eventBus.post(new DoSchedulingMessage(message.getTask()));
                }
            });
            t.start();
        }
    }
    private int getWorkTime(List<String> steps){
        int sum = 0;
        for(String s: steps){
            if(typeOfWork.containsKey(s)) sum += typeOfWork.get(s);
        }
        return sum;
    }

    private void giveStatus(String s){
        eventBus.post(new StatusMessage(s, key));
    }

    @Override
    public String toString() {
        return "WorkStation{" +
                "typeOfWork='" + typeOfWork + '\'' +
                ", neighbors=" + neighbors +
                ", position='" + position + '\'' +
                ", key=" + key +
                '}';
    }
}
