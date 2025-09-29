package factory.controlledSystem;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import factory.communication.message.SetFactoryMessage;
import factory.communication.message.WorkStationCostChangeMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.*;

/**
 * Class that acts as a parent to all nodes in the simulation. Holds data over specific nodes, costs between all connected nodes and provides helper methods for scheduling.
 */
@Singleton
public class Factory implements FactoryInterface{

    private DispenserStation dispenserStation;

    private DropOffStation dropOffStation;

    private LinkedList<FactoryNode> factoryNodes;

    private Map<Character,Map<Character,LinkedList<FactoryNode>>> pathTable;

    @Inject
    public Factory(EventBus eventBus) {
        eventBus.register(this);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onWorkStationCostChangeMessage (WorkStationCostChangeMessage message){
        calculatePaths();
    }

    @Subscribe
    public void onSetFactoryMessage(SetFactoryMessage message){
        setFactoryNodes(message.getFactoryNodes());
    }

    public void setFactoryNodes(LinkedList<FactoryNode> factoryNodes) {
        if(factoryNodes == null) return;
        this.factoryNodes = factoryNodes;
        for(FactoryNode node : factoryNodes){
            if(node instanceof DispenserStation) dispenserStation = (DispenserStation) node;
            if(node instanceof DropOffStation) dropOffStation = (DropOffStation) node;
        }
        calculatePaths();
    }

    /**
     * calculates costs between all nodes that are connected
     */
    private void calculatePaths(){
        pathTable = new TreeMap<>();
        for(FactoryNode fromNode: factoryNodes){
            char k = fromNode.getKey();
            Map<Character, LinkedList<FactoryNode>> pathsFromk = new TreeMap<>();
            for(FactoryNode toNode: factoryNodes){
                char c = toNode.getKey();
                pathsFromk.put(c,FactoryNode.findPath(fromNode,toNode,0));
            }
            pathTable.put(k,pathsFromk);
        }
    }

    public Map<Character, Map<Character, LinkedList<FactoryNode>>> getPathTable() {
        return pathTable;
    }


    public DispenserStation getDispenserStation() {
        return dispenserStation;
    }

    public DropOffStation getDropOffStation() {
        return dropOffStation;
    }

    public FactoryNode getNodeByKey(char k){
        return FactoryNode.getNodeByChar(k, factoryNodes);
    }

    /**
     * Helper method for scheduling
     *
     * @param s String of desired work step
     * @return List of stations which are able to complete the desired work step
     */
    public List<WorkStation> getAvailableWorkStations(String s){
        List<WorkStation> list = new ArrayList<>();
        for( FactoryNode node : factoryNodes){
            if(node instanceof WorkStation && ((WorkStation) node).getTypesOfWork().containsKey(s)){
                list.add((WorkStation) node);
            }
        }
        return list;
    }
}
