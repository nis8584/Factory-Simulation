package factory.controlledSystem;

import java.util.*;

public class FactoryNode {
    private Map<FactoryNode, Integer> neighbors = new HashMap<>();

    private String position;


    public FactoryNode(){}

    public Map<FactoryNode, Integer> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(Map<FactoryNode, Integer> neighbors) {
        this.neighbors = neighbors;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "FactoryNode{" +
                "position='" + position + '\'' +
                '}';
    }
//todo fix cost factor when looking for best path(currently takes path with least stops in between // also: fn2 to fn1 not working
    public static LinkedList<FactoryNode> findPath(FactoryNode from, FactoryNode to, int previousIterations){
        LinkedList<FactoryNode> list = new LinkedList<>();
        // running too long?
        if(previousIterations > 15) return null;
        //already there?
        if(from.equals(to)){
            list.add(from);
            return list;
        }
        //if not there
        list.add(from);

            // one possible next node
            if(from.getNeighbors().size() == 1){
                FactoryNode nextNode =  from.neighbors.keySet().iterator().next();
                //add to list and look from there
                list.addAll(findPath(nextNode,to,previousIterations));
            } else {
                //multiple next possible
                int currentBestCost = -1;
                //check all possible next
                LinkedList<FactoryNode> currentBestPath = new LinkedList<>();
                for(FactoryNode node: from.neighbors.keySet()){
                    //recursive call
                    LinkedList<FactoryNode> path = findPath(node, to, previousIterations+1);
                    //if not ran too long
                    if(path != null){
                        //if first path then write down cost
                        int newCost = FactoryNode.costPerPath(path);
                        if(currentBestCost == -1){
                            currentBestCost = newCost;
                            currentBestPath = path;
                        }
                        //only add path if cheapest
                        if(newCost<= currentBestCost){
                            currentBestCost = newCost;
                            currentBestPath = path;
                        }
                        if(path.size() == 1) break;
                    }
                }
                if(!currentBestPath.isEmpty()){
                    list.addAll(currentBestPath);
                }
            }
        return list;
    }
    public static int costPerPath(LinkedList<FactoryNode> list){
        int cost = 0;
        if(list == null) return 0;
        for(FactoryNode node: list){
            if(node.equals(list.getLast())) break;
            FactoryNode nextNode = list.get(list.indexOf(node)+1);
            cost += node.neighbors.get(nextNode);
        }
        return cost;
    }
}
