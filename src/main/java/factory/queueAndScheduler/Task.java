package factory.queueAndScheduler;

import factory.TimeService;
import factory.controlledSystem.FactoryNode;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

/**
 * Abstract task class that provides generic parameters and methods that are needed for a task
 */
public abstract class Task {

    private final LinkedList<LinkedList<String>> requiredTasks;

    private  long startTime;

    private FactoryNode currentNodeLocation;

    private final String name;

    private final int id;

    protected Task(LinkedList<LinkedList<String>> requiredTasks, String name, int id) {
        this.requiredTasks = requiredTasks;
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public void setStartTime(long time){
        if(startTime != 0) return;
        startTime = time;
    }

    public double getRunningTime(){
        long endTime = TimeService.getTime();
        return (double) (endTime - startTime);
    }

    public LinkedList<LinkedList<String>> getRequiredWorkStations() {
        return requiredTasks;
    }

    public void doWorkAt(Set<String> s){
        requiredTasks.getFirst().removeAll(s);
        if(requiredTasks.getFirst().isEmpty()) requiredTasks.removeFirst();
    }
    public boolean isTaskDone(){
        return requiredTasks.isEmpty();
    }

    public FactoryNode getCurrentNodeLocation() {
        return currentNodeLocation;
    }

    public void setCurrentNodeLocation(FactoryNode currentNodeLocation) {
        this.currentNodeLocation = currentNodeLocation;
    }

    @Override
    public String toString() {
        return "Task{" +
                "requiredWorkStations=" + requiredTasks +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(requiredTasks, task.requiredTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(requiredTasks);
    }
}
