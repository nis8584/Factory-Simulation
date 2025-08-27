package factory.controlledSystem;

import java.util.*;

public class FactoryNode {
    private Map<FactoryNode, Integer> neighbors = new HashMap<>();

    private String position;

    private final char key;

    public FactoryNode(char k){key = k;}

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

    public char getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "FactoryNode{" +
                "position='" + position + '\'' +
                ", key='" + key + '\'' +
                //", neighbors='" + neighbors +'\'' +
                '}';
    }

 //todo consider putting pathfinding in new class maybe also animation class?
    /**
     * Method to recursively find the cheapest path from one FactoryNode to another.
     *
     * @param from Starting point
     * @param to End point
     * @param previousIterations parameter used to limit recursion. use with 0.
     * @return cheapest path from Node 'from' to 'to'
     */
    public static LinkedList<FactoryNode> findPath(FactoryNode from, FactoryNode to, int previousIterations){
        LinkedList<FactoryNode> list = new LinkedList<>();
        // running too long?
        if(previousIterations > 100) return null;
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
            } else if(from.getNeighbors().size() > 1){
                //multiple next possible
                int currentBestCost = -1;
                //check all possible next
                LinkedList<FactoryNode> currentBestPath = new LinkedList<>();
                for(FactoryNode node: from.neighbors.keySet()){
                    //recursive call
                    LinkedList<FactoryNode> path = findPath(node, to, previousIterations+1);
                    //if not ran too long
                    if(path != null && !(from.equals(path.getLast()))){
                        //if first path then write down cost
                        LinkedList<FactoryNode> costPath = new LinkedList<>();
                        costPath.add(from);
                        costPath.addAll(path);
                        int newCost = FactoryNode.costPerPath(costPath);
                        if(path.isEmpty() || newCost == -1) break;
                        if(currentBestCost == -1){
                            currentBestCost = newCost;
                            currentBestPath = path;
                        }
                        //only add path if cheapest
                        if(newCost<= currentBestCost){
                            currentBestCost = newCost;
                            currentBestPath = path;
                        }
                    }
                }
                if(!currentBestPath.isEmpty()){
                    list.addAll(currentBestPath);
                }
            }
        return list;
    }

    /**
     * calculates the cost of a given path of FactoryNodes
     * @param list LinkedList of FactoryNodes
     * @return Integer cost to 'walk' the given path
     */
    public static int costPerPath(LinkedList<FactoryNode> list){
        int cost = 0;
        int i = 0;
        if(list == null) return -1;
        for(FactoryNode node: list){
            if(node.equals(list.getLast())) break;
            FactoryNode nextNode = list.get(i+1);
            i++;
            cost += node.neighbors.get(nextNode);
        }
        return cost;
    }

    public static boolean alreadyPresentInList(char key, LinkedList<FactoryNode> list){
        for(FactoryNode node : list){
            if(node.getKey() == key) return true;
        }
        return false;
    }
    public static boolean alreadyPresentInList(char key1, char key2, LinkedList<FactoryNode> list){
        for(FactoryNode node : list){
            if(node.getKey() == key1 || node.getKey() == key2) return true;
        }
        return false;
    }
}
