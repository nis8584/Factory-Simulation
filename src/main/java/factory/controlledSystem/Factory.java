package factory.controlledSystem;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import factory.communication.message.SetLayoutMessage;
import factory.communication.message.WorkStationCostChangeMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.*;

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

    @Subscribe
    public void onWorkStationCostChangeMessage (WorkStationCostChangeMessage message){
        calculatePaths();
    }

    @Subscribe
    public void onSetLayoutMessage(SetLayoutMessage message){
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

    public List<FactoryNode> getFactoryNodes(){
        return factoryNodes;
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

    public List<WorkStation> getAvalibleWorkStations(String s){
        List<WorkStation> list = new ArrayList<>();
        for( FactoryNode node : factoryNodes){
            if(node instanceof WorkStation && ((WorkStation) node).getTypeOfWork().equals(s)){
                list.add((WorkStation) node);
            }
        }
        return list;
    }
}
