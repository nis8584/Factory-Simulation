package factory.queueAndScheduler;

import factory.TimeService;
import factory.controlledSystem.FactoryNode;

import java.util.List;
import java.util.Objects;

/**
 * Abstract task class that provides generic parameters and methods that are needed for a task
 */
public abstract class Task {

    private final List<String> requiredTasks;

    private  long startTime;

    private FactoryNode currentNodeLocation;

    protected Task(List<String> requiredTasks) {
        this.requiredTasks = requiredTasks;
    }

    public void setStartTime(long time){
        if(startTime != 0) return;
        startTime = time;
    }

    public double getRunningTime(){
        long endTime = TimeService.getTime();
        return (double) (endTime - startTime);
    }

    public List<String> getRequiredWorkStations() {
        return requiredTasks;
    }

    public void doWorkAt(){
        requiredTasks.removeFirst();
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
